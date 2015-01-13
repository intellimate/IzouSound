package jundl77.izou.izousound.outputplugin;

import intellimate.izou.system.Context;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class runs as a task in a thread pool and plays songs, it is controlled by the AudioFilePlayer.There should be
 * no reason for using this class, as it is the engine that is running behind the AudioFilePlayer and should therefore
 * not be touched.
 */
class SoundEngine {
    private MediaPlayer mediaPlayer;
    private Media media;
    private SoundIdentityFactory soundIdentityFactory;
    private HashMap<Integer, SoundIdentity> soundFileMap;
    private SoundIdentity currentSound;
    private AtomicInteger playIndex;
    private Context context;

    /**
     * Creates a new sound-object in order to play sound files
     *
     * @param context the Context of the output-plugin
     */
    public SoundEngine(Context context) {
        playIndex = new AtomicInteger();
        JFXPanel panel = new JFXPanel();
        this.context = context;
        soundFileMap = new HashMap<>();
        soundIdentityFactory = new SoundIdentityFactory();
        currentSound = null;
        playIndex.set(-1);
    }

    /**
     * Adds a new list with sound files to the blocking queue that will be processed as soon as possible
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     * @param startTime the start time of the sound file (in milliseconds)
     * @param stopTime the stop time of the sound file  (in milliseconds)
     */
    public void addSoundFiles(List<String> soundFilePaths, int startTime, int stopTime) {
        List<SoundInfo> soundInfos = new ArrayList<>();

        for (String path : soundFilePaths) {
            SoundInfo soundInfo = new SoundInfo(path, startTime, stopTime);
            soundInfos.add(soundInfo);
        }

        run(soundInfos);
    }

    /**
     * Adds a new list with sound files to the blocking queue that will be processed as soon as possible
     *
     * @param soundURLs the list of URLs to sound files that should be played
     * @param startTime the start time of the sound file (in milliseconds)
     * @param stopTime the stop time of the sound file  (in milliseconds)
     */
    public void addSoundURL(List<URL> soundURLs, int startTime, int stopTime) {
        List<SoundInfo> soundInfos = new ArrayList<>();

        for (URL url : soundURLs) {
            SoundInfo soundInfo = new SoundInfo(url, startTime, stopTime);
            soundInfos.add(soundInfo);
        }

        run(soundInfos);
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
        if (mediaPlayer == null) {
            return null;
        }
        return mediaPlayer.getStatus().toString();
    }

    /**
     * Resume the sound if there is sound to resume and if it is paused
     */
    public void resumeSound() throws IllegalStateException {
        if (getState().equals("PAUSED")) {
            mediaPlayer.play();
        } else {
            throw new IllegalStateException("sound is not paused, so it cannot be resumed");
        }
    }

    /**
     * Stops the current playback entirely
     */
    private void stopSound() {
        Runnable r = mediaPlayer.getOnEndOfMedia();
        mediaPlayer.dispose();
        r.run();
    }

    /**
     * Stops playback for entire session, the task will go back to the blocking-queue
     */
    public void stopSession() {
        if (getState() != null) {
            mediaPlayer.dispose();
            resetSession();
        }
    }

    /**
     * Pauses sound
     */
    public void pauseSound() {
        if (getState() != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Jumps to next sound-file if there is one, else jumps back to the start
     */
    public void nextFile() {
        if (getState() == null) {
            return;
        }

        if (playIndex.get() < soundFileMap.size() - 1) {
            //no need to increment index because loop does it on its own
            stopSound();
        } else {
            //the index is set to -1 and not 0 because the loop will increment the index to 0 on its own
            playIndex.set(-1);
            stopSound();
        }
    }

    /**
     * Jumps back to beginning of current sound file if one is playing
     */
    public void restartFile() {
        if (getState() == null) {
            return;
        }

        //index is decremented by one, yet the loop will bring it back up to the same number, causing the sound to
        //start over
        playIndex.decrementAndGet();

        stopSound();
    }

    /**
     * Jumps to previous sound-file if there is one, else jump to last sound-file
     */
    public void previousFile() {
        if (getState() == null) {
            return;
        }

        if (playIndex.get() > 0) {
            //the index is decreased by 2 and not 0 because the loop will increment the index to 0 on its own
            playIndex.decrementAndGet();
            playIndex.decrementAndGet();

            stopSound();
        } else {
            playIndex.set(soundFileMap.size() - 2);
            stopSound();
        }
    }

    /**
     * Sets the volume from 0 - 100
     *
     * @param volume volume level (from 0 - 100)
     */
    public void controlVolume(double volume) {
        if (getState() == null) {
            return;
        }

        mediaPlayer.setVolume(volume / 100);
    }

    /**
     * Plays the sound at {@code path}
     *
     * @param soundId The id of the sound to be played
     * @throws java.lang.IndexOutOfBoundsException thrown if start or end time are out of bounds (-1 not included)
     */
    private void playSoundFile(SoundIdentity soundId) throws IndexOutOfBoundsException {
        currentSound = soundId;
        if (soundId.getSoundInfo().getPath() != null) {
            String path = soundId.getSoundInfo().getPath();

            File file = new File(path);
            try {
                media = new Media(file.toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                context.logger.getLogger().error("file to url conversion issue", e);
            }
        } else if (soundId.getSoundInfo().getURL() != null) {
            media = new Media(soundId.getSoundInfo().getURL().toExternalForm());
        }

        prepareMediaPlayer();

        setPlayDuration(soundId);

        currentSound = soundId;
        mediaPlayer.play();
        //context.logger.getLogger().debug("Started sound playback of: " + soundId.getSoundInfo().getName());

        mediaPlayer.setOnEndOfMedia(() -> {
            //context.logger.getLogger().debug("Finished sound playback of: " + soundId.getSoundInfo().getName());
            if (playIndex.get() > soundFileMap.size()) {
                return;
            }
            playIndex.incrementAndGet();
            SoundIdentity id = soundFileMap.get(playIndex.intValue());
            mediaPlayer.dispose();
            playSoundFile(id);
        });
    }

    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer(media);

        final Lock lock = new ReentrantLock();
        final Condition ready = lock.newCondition();

        mediaPlayer.setOnReady(() -> {
            lock.lock();
                ready.signal();
            lock.unlock();
        });

        lock.lock();
        try {
            ready.await();
        } catch (InterruptedException e) {
            context.logger.getLogger().warn("thread interrupted", e);
        }
        lock.unlock();

        mediaPlayer.setOnReady(null);
    }

    private void setPlayDuration(SoundIdentity soundId) {
        double duration = media.getDuration().toMillis();

        if (soundId.getSoundInfo().getStartTime() == -1) {
            mediaPlayer.setStartTime(Duration.millis(0));
        } else if (soundId.getSoundInfo().getStartTime() > 0
                && soundId.getSoundInfo().getStartTime() < duration) {
            mediaPlayer.setStartTime(Duration.millis(soundId.getSoundInfo().getStartTime()));
        } else {
            throw new IndexOutOfBoundsException("start-time out of bounds");
        }

        if (soundId.getSoundInfo().getStopTime() == -1) {
            mediaPlayer.setStopTime(Duration.millis(duration));
        } else if (soundId.getSoundInfo().getStopTime() > 0
                && soundId.getSoundInfo().getStopTime() < duration) {
            mediaPlayer.setStopTime(Duration.millis(soundId.getSoundInfo().getStopTime()));
        } else {
            throw new IndexOutOfBoundsException("end-time out of bounds");
        }
    }

    /**
     * Add a sound-file-path to the soundFileMap where all songs are stored that are to be played
     *
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     */
    private void addToQueuedSongs(SoundInfo soundInfo) {
        SoundIdentity soundIdentity = soundIdentityFactory.make(soundInfo);
        soundFileMap.put(soundIdentity.getId(), soundIdentity);
    }

    /**
     * Recursively goes through all files to play at path
     *
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     */
    private void recursiveSoundFileSearch(SoundInfo soundInfo) {
        String filePath = soundInfo.getPath();
        if (new File(filePath).isFile()
                && (filePath.endsWith(".mp3")
                || filePath.endsWith(".wav"))) {
            addToQueuedSongs(soundInfo);
        } else if (new File(filePath).isDirectory()) {
            File[] files = new File(filePath).listFiles();

            if (files == null)
                return;

            for (File path: files) {
                String pathString = path.getAbsolutePath();
                recursiveSoundFileSearch(new SoundInfo(pathString));
            }
        }
    }

    private void fillQueuedSoundFiles(List<SoundInfo> fileInfos) {
        if (fileInfos.get(0).getPath() != null) {
            for (SoundInfo soundInfo : fileInfos) {
                String filePath = soundInfo.getPath();
                if (!new File(filePath).exists()) {
                    context.logger.getLogger().error(filePath + "does not exists - Unable to play sound");
                    continue;
                }
                recursiveSoundFileSearch(soundInfo);
            }
        } else if (fileInfos.get(0).getURL() != null) {
            for (SoundInfo soundInfo : fileInfos) {
                addToQueuedSongs(soundInfo);
            }
        }
    }

    private void resetSession() {
        soundIdentityFactory.startNewSession();
        soundFileMap.clear();
        mediaPlayer = null;
        media = null;
    }

    /**
     * Run method for sound object, gets started on instantiation and waits for paths to process
     */
    public void run(List<SoundInfo> soundInfos) {
        resetSession();

        playIndex.set(0);
        fillQueuedSoundFiles(soundInfos);
        SoundIdentity id = soundFileMap.get(playIndex.intValue());
        playSoundFile(id);
    }
}