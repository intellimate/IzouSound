package jundl77.izou.izousound.outputplugin;

import intellimate.izou.system.Context;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class runs as a task in a thread pool and plays songs, it is controlled by the AudioFilePlayer.There should be
 * no reason for using this class, as it is the engine that is running behind the AudioFilePlayer and should therefore
 * not be touched.
 */
public class AudioFilePlayer {
    private BlockingQueue<List<SoundInfo>> pathsBlockingQueue;
    private MediaPlayer mediaPlayer;
    private Media media;
    private SoundIdentityFactory soundIdentityFactory;
    private HashMap<SoundIdentity, String> soundFileMap;
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
    public AudioFilePlayer(Context context) {
        JFXPanel panel = new JFXPanel();
        paused = false;
        canPlay = true;
        validSession = true;
        pathsBlockingQueue = new LinkedBlockingQueue<>();
        this.context = context;
        soundFileMap = new HashMap<>();
        soundIdentityFactory = new SoundIdentityFactory();
        currentSound = null;
        playIndex = 0;
    }

    /**
     * Adds a new List with sound files to the blocking queue that will be processed as soon as possible
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     * @param startTime the start time of the sound file (in milliseconds)
     * @param stopTime the stop time of the sound file  (in milliseconds)
     */
    public void addSoundFiles(List<String> soundFilePaths, int startTime, int stopTime) {
        List<SoundInfo> soundInfos = new ArrayList<>();

        for (String path : soundFilePaths) {
            String[] fileParts = path.split(File.separator);
            SoundInfo soundInfo = new SoundInfo(fileParts[fileParts.length - 1], path, startTime, stopTime);
            soundInfos.add(soundInfo);
        }

        try {
            pathsBlockingQueue.put(soundInfos);
        } catch (InterruptedException e) {
            context.logger.getLogger().error("Failed to add sound file paths to blocking queue", e);
        }
    }

    /**
     * Adds a new List with sound files to the blocking queue that will be processed as soon as possible
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     *
     */
    public void addSoundFiles(List<String> soundFilePaths) {
        addSoundFiles(soundFilePaths, -1, -1);
    }

    /**
     * Returns the state of the mediaPlayer
     *
     * @return state of mediaPlayer
     */
    public String getState() {
        return mediaPlayer.getStatus().toString();
    }

    /**
     * Resume the sound if there is sound to resume and if it is paused
     */
    public void resumeSound() throws NullPointerException {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
        }
    }

    /**
     * Stops the current playback entirely
     */
    public void stopSound() {
        mediaPlayer.stop();
    }

    /**
     * Stops playback for entire session, the task will go back to the blockingqueue
     */
    public void stopSession() {
        validSession = false;
    }

    /**
     * Pauses sound
     */
    public void pauseSound() {
        mediaPlayer.pause();
    }

    /**
     * Jumps to next sound-file if there is one, else jumps back to the start
     */
    public void nextFile() {
        if (playIndex < soundFileMap.size() - 1) {
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

            playIndex = soundFileMap.size() - 2;

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
     * Handles blocking queue behavior
     *
     * @return list of paths that should be processed next
     */
    private List<SoundInfo> handleBlockingQueue() {
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
     * @throws java.lang.IndexOutOfBoundsException thrown if start or end time are out of bounds (-1 not included)
     */
    private void playSoundFile(SoundIdentity soundId) throws IndexOutOfBoundsException {
        canPlay = true;
        currentSound = soundId;
        String path = soundFileMap.get(soundId);

        File file = new File(path);
        try {
            media = new Media(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mediaPlayer = new MediaPlayer(media);

        double duration = media.getDuration().toMillis();

        if (soundId.getSoundInfo().getStartTime() > 0 && soundId.getSoundInfo().getStartTime() < duration) {
            mediaPlayer.setStartTime(Duration.millis(soundId.getSoundInfo().getStartTime()));
        } else {
            throw new IndexOutOfBoundsException("start time out of bounds");
        }

        if (soundId.getSoundInfo().getStopTime() > 0 && soundId.getSoundInfo().getStopTime() < duration) {
            mediaPlayer.setStartTime(Duration.millis(soundId.getSoundInfo().getStartTime()));
        } else {
            throw new IndexOutOfBoundsException("end time out of bounds");
        }

        mediaPlayer.play();

        Lock lock = new ReentrantLock();
        mediaPlayer.setOnEndOfMedia(() -> {
            lock.notify();
        });
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a sound-file-path to the soundFileMap where all songs are stored that are to be played
     *
     * @param filePath the path of the sound-file to add
     */
    private void addToQueuedSongs(String filePath) {
        String[] fileParts = filePath.split(File.separator);
        soundFileMap.put(soundIdentityFactory.make(fileParts[fileParts.length - 1]), filePath);
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

    private void fillQueuedSoundFiles(List<SoundInfo> filePaths) {
        for (SoundInfo soundInfo : filePaths) {
            String filePath = soundInfo.getPath();
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
        soundFileMap.clear();
    }

    /**
     * Run method for sound object, gets started on instantiation and waits for paths to process
     */
    public void run() {
        while (true) {
            List<SoundInfo> filePaths = handleBlockingQueue();
            resetSession();

            fillQueuedSoundFiles(filePaths);

            for (playIndex = 0; playIndex < soundFileMap.size(); playIndex++) {
                SoundIdentity id = soundIdentityFactory.getSoundIdentity(playIndex);

                playSoundFile(id);

                if (!validSession) {
                    break;
                }
            }
        }
    }
}