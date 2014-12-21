package jundl77.izou.izousound.outputplugin;

import intellimate.izou.system.Context;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * This class runs as a task in a thread pool and plays songs, it is controlled by the AudioFilePlayer.There should be
 * no reason for using this class, as it is the engine that is running behind the AudioFilePlayer and should therefore
 * not be touched.
 */
class SoundEngine implements Runnable {
    private BlockingQueue<List<String>> pathsBlockingQueue;
    private AudioInputStream inputStream;
    private AudioFormat outFormat;
    private DataLine.Info info;
    private SourceDataLine line;
    private SoundIdentityFactory soundIdentityFactory;
    private HashMap<SoundIdentity, String> queuedSoundFiles;
    private SoundIdentity currentSound;
    private boolean paused;
    private boolean canPlay;
    private boolean validSession;
    private int playIndex;
    private Context context;

    /**
     * Creates a new sound-object in order to play sound files
     *
     * @param context the Context of the output-plugin
     */
    public SoundEngine(Context context) {
        paused = false;
        canPlay = true;
        validSession = true;
        pathsBlockingQueue = new LinkedBlockingQueue<>();
        this.context = context;
        queuedSoundFiles = new HashMap<>();
        soundIdentityFactory = new SoundIdentityFactory();
        currentSound = null;
        playIndex = 0;
    }

    /**
     * Adds a new List with sound files to the blocking queue that will be processed as soon as possible
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     */
    public void addSoundFiles(List<String> soundFilePaths) {
        try {
            pathsBlockingQueue.put(soundFilePaths);
        } catch (InterruptedException e) {
            context.logger.getLogger().error("Failed to add sound file paths to blocking queue", e);
        }
    }

    /**
     * Returns true if paused, false otherwise
     *
     * @return state of song (if true paused, else false)
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * If the data-line is currently engaging in I/O activity, the line is considered active, and hence the
     * sound-object is also considered active
     *
     * @return state of activity of sound-object (and data-line)
     */
    public boolean isActive () {
        if (line != null) {
            return line.isActive();
        }
        return false;
    }

    /**
     * Resume the sound if there is sound to resume and if it is paused
     */
    public void resumeSound() throws NullPointerException {
        if (line != null && line.isOpen() && isPaused()) {
            paused = false;
        } else {
            throw new NullPointerException("Line in soundObject is null or closed");
        }
    }

    /**
     * Stops the current playback entirely
     */
    private void stopSound() {
        if (line != null && line.isOpen()) {
            canPlay = false;
        } else {
            context.logger.getLogger().warn("Unable to stop current playback because there is none");
        }
    }

    /**
     * Stops playback for entire session, the task will go back to the blockingqueue
     */
    public void stopSession() {
        if (line != null && line.isOpen()) {
            canPlay = false;
            validSession = false;
        } else {
            context.logger.getLogger().warn("Unable to stop current playback because there is none");
        }
    }

    /**
     * Pauses sound
     */
    public void pauseSound() {
        paused = true;
    }

    /**
     * Jumps to next sound-file if there is one, else jumps back to the start
     */
    public void nextFile() {
        if (playIndex < queuedSoundFiles.size() - 1) {
            //no need to increment index because loop does it on its own
            stopSound();

            if (paused) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    context.logger.getLogger().error("Failed to put the thread asleep", e);
                }
                paused = true;
            }
        } else {
            stopSound();

            //the index is set to -1 and not 0 because the loop will increment the index to 0 on its own
            playIndex = -1;

            if (paused) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    context.logger.getLogger().error("Failed to put the thread asleep", e);
                }
                paused = true;
            }
        }
    }

    /**
     * Jumps back to beginning of current sound file if one is playing
     */
    public void restartFile() {
        stopSound();

        //index is decremented by one, yet the loop will bring it back up to the same number, causing the sound to
        //start over
        playIndex--;

        if (paused) {
            paused = false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                context.logger.getLogger().error("Failed to put the thread asleep", e);
            }
            paused = true;
        }
    }

    /**
     * Jumps to previous sound-file if there is one, else jump to last sound-file
     */
    public void previousFile() {
        if (playIndex > 0) {
            stopSound();

            //the index is decreased by 2 and not 0 because the loop will increment the index to 0 on its own
            playIndex -= 2;

            if (paused) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    context.logger.getLogger().error("Failed to put the thread asleep", e);
                }
                paused = true;
            }
        } else {
            stopSound();

            playIndex = queuedSoundFiles.size() - 2;

            if (paused) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    context.logger.getLogger().error("Failed to put the thread asleep", e);
                }
                paused = true;
            }
        }
    }

    /**
     *
     * @param buffer
     * @param volume
     */
    private void controlVolume(byte[] buffer, int volume) {
        for (int i = 0; i < buffer.length; i += 16) {
            byte[] buffer16Bit = new byte[16];
            for (int j = i; j < buffer16Bit.length; j++) {
                buffer16Bit[0] = buffer[j];
            }

            for (int k = 0; k < 15; k++) {
                double sample = ((buffer16Bit[k] << 8) | (buffer16Bit[k + 1] & 0xFF)) / 32768.0;
            }
        }
    }

    /**
     * Helper method for playSoundFile method
     *
     * @param inFormat audioformat for input-stream
     * @return new audioformat
     */
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        int ch = inFormat.getChannels();
        float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    /**
     * Streams the sound from sound file into a buffer which is then loaded into a data line from which it is retrieved
     * to play
     *
     * @param in the java audioInputStream
     * @param line data line into which the buffer is loaded
     * @throws IOException IO exception as always with IO
     */
    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        byte[] buffer = new byte[65536];

        for (int n = 0; n != -1 && canPlay; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
            while(paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles blocking queue behavior
     *
     * @return list of paths that should be processed next
     */
    private List<String> handleBlockingQueue() {
        try {
             return pathsBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Plays the sound at {@code path}
     *
     * @param soundId The id of the sound to be played
     */
    private void playSoundFile(SoundIdentity soundId) {
        canPlay = true;
        currentSound = soundId;
        String path = queuedSoundFiles.get(soundId);
        try {
            inputStream = getAudioInputStream(new File(path));
            outFormat = getOutFormat(inputStream.getFormat());
            info = new DataLine.Info(SourceDataLine.class, outFormat);

            line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                line.open(outFormat);
                line.start();
                currentSound = soundId;
                stream(getAudioInputStream(outFormat, inputStream), line);
                line.drain();
                line.stop();
                currentSound = null;
            }
        } catch (UnsupportedAudioFileException
                | LineUnavailableException
                | IOException e) {
            context.logger.getLogger().error("unable to play sound file at path: " + path, e);
        }
    }

    /**
     * Add a sound-file-path to the queuedSoundFiles where all songs are stored that are to be played
     *
     * @param filePath the path of the sound-file to add
     */
    private void addToQueuedSongs(String filePath) {
        String[] fileParts = filePath.split(File.separator);
        queuedSoundFiles.put(soundIdentityFactory.make(fileParts[fileParts.length - 1]), filePath);
    }

    /**
     * Recursively goes through all files to play at path
     *
     * @param filePath path where to start searching
     */
    private void recursiveSoundFileSearch(String filePath) {
        if (new File(filePath).isFile()
                && (filePath.endsWith(".mp3")
                || filePath.endsWith(".wav"))) {
            addToQueuedSongs(filePath);
        } else if (new File(filePath).isDirectory()) {
            File[] files = new File(filePath).listFiles();

            if (files == null)
                return;

            for (File path: files) {
                String pathString = path.getAbsolutePath();
                recursiveSoundFileSearch(pathString);
            }
        }
    }

    private void fillQueuedSoundFiles(List<String> filePaths) {
        for (String filePath : filePaths) {
            if (!new File(filePath).exists()) {
                context.logger.getLogger().error(filePath + "does not exists - Unable to play sound");
                continue;
            }
            recursiveSoundFileSearch(filePath);
        }
    }

    private void resetSession() {
        validSession = true;
        soundIdentityFactory.startNewSession();
        queuedSoundFiles.clear();
    }

    /**
     * Run method for sound object, gets started on instantiation and waits for paths to process
     */
    @Override
    public void run() {
        while (true) {
            List<String> filePaths = handleBlockingQueue();
            resetSession();

            fillQueuedSoundFiles(filePaths);

            for (playIndex = 0; playIndex < queuedSoundFiles.size(); playIndex++) {
                SoundIdentity id = soundIdentityFactory.getSoundIdentity(playIndex);
                playSoundFile(id);

                if (!validSession) {
                    break;
                }
            }
        }
    }
}
