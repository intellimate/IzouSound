package jundl77.izou.izousound.outputplugin;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class plays songs by chaining threads, it is controlled by the AudioFilePlayer.There should be
 * no reason for using this class; it is the "engine" that is running behind the AudioFilePlayer and should therefore
 * not be touched.
 */
class SoundEngine {
    public static final String READY_STATE = "READY";
    public static final String PLAYING_STATE = "PLAYING";
    public static final String PAUSED_STATE = "PAUSED";

    private AdvancedPlayer player;
    private InputStream inputStream;
    private SoundLoader soundLoader;
    private HashMap<Integer, SoundIdentity> soundFileMap;
    private HashMap<Integer, SoundIdentity> shuffeledSoundFileMap;
    private Playlist playlist;
    private Playlist shuffeledPlaylist;
    private AtomicInteger playIndex;
    private AudioFilePlayer audioFilePlayer;
    private Context context;
    private boolean outOfBoundsError;
    private boolean paused;
    private int pausedOnFrame;
    private volatile String state;
    private long startSecond;

    private AtomicBoolean shuffle;
    private AtomicBoolean repeatPlaylist;
    private AtomicBoolean repeatSong;

    /**
     * Creates a new sound-object in order to play sound files
     *
     * @param context the Context of the output-plugin
     */
    public SoundEngine(Context context, AudioFilePlayer audioFilePlayer) {
        this.playIndex = new AtomicInteger();
        this.context = context;
        this.soundLoader = new SoundLoader(context);
        this.audioFilePlayer = audioFilePlayer;
        this.playIndex.set(-1);
        this.state = null;
        this.soundFileMap = new HashMap<>();
        audioFilePlayer.setCurrentSound(null);

        this.shuffle = new AtomicBoolean(false);
        this.repeatPlaylist = new AtomicBoolean(true);
        this.repeatSong = new AtomicBoolean(false);
    }

    /**
     * Returns the state of the player
     *
     * @return state of player
     */
    synchronized String getState() {
        return state;
    }

    /**
     * Resume the sound if there is sound to resume and if it is paused
     */
    void resumeSound() throws IllegalStateException {
        if (getState() == null) {
            context.getLogger().warn("State is null, quitting");
            return;
        }

        if (state.equals(PAUSED_STATE)) {
            if (pausedOnFrame >= 0) {
                Thread t = new Thread(() ->
                        playSoundFile(audioFilePlayer.getCurrentSound(), pausedOnFrame, Integer.MAX_VALUE));
                paused = false;
                t.start();
                context.getLogger().debug("Resumed sound");
            }
        } else {
            throw new IllegalStateException("Sound is not paused, so it cannot be resumed");
        }
    }

    /**
     * Stops the current playback entirely
     */
    private void stopSound() {
        if (player != null) {
            PlaybackListener r = player.getPlayBackListener();
            closeAll();
            r.playbackFinished(null);
        }
    }

    // Closes all resources used in the playback (input stream and the advanced player)
    private void closeAll() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                context.getLogger().error("Unable to close input stream", e);
            }
            inputStream = null;
        }
    }

    /**
     * Stops playback for entire session
     */
    void stopSession() {
        if (getState() != null) {
            closeAll();
            resetSession();
            context.getLogger().debug("Stopped sound");
            state = null;
        } else {
            context.getLogger().warn("State is null, quitting");
        }
        audioFilePlayer.stopMusicPlayback();
    }

    /**
     * Pauses sound
     */
    void pauseSound() {
        if (getState() != null) {
            paused = true;
            player.stop();
            state = PAUSED_STATE;
            context.getLogger().debug("Paused sound");
        }
    }

    /**
     * Jumps to the track info in the playlist if it is found
     *
     * @param trackInfo the track info to jump to and play
     * @throws IllegalArgumentException thrown if index is not in bounds of playlist
     */
    void jumpToFile(TrackInfo trackInfo) throws IllegalArgumentException {
        if (playlist.getQueue().contains(trackInfo)) {
            playIndex.set(playlist.getQueue().indexOf(trackInfo) - 1);
            stopSound();
        } else {
            throw new IllegalArgumentException("TrackInfo:" + trackInfo.getName() + " is not in the current playlist.");
        }
    }

    /**
     * Jumps to next sound-file if there is one, else jumps back to the start if loopPlaylist is set to true, else stop
     * the current playback session. If shuffle is set to true, a random song is chosen next
     */
    void nextFile() {
        if (getState() == null) {
            context.getLogger().warn("State is null, quitting");
            return;
        }

        context.getLogger().debug("Started playing next sound");
        if (repeatSong.get()) {
            // Decrement index by 1 so that the loop will increase it back to the current song
            playIndex.decrementAndGet();
            stopSound();
        } else if (playIndex.get() < soundFileMap.size() - 1) {
            stopSound();
        } else if (repeatPlaylist.get()) {
            // The index is set to -1 and not 0 because the loop will increment the index to 0 on its own
            playIndex.set(-1);
            stopSound();
        } else {
            stopSession();
        }
    }

    /**
     * Jumps back to beginning of current sound file if one is playing
     */
    void restartFile() {
        if (getState() == null) {
            context.getLogger().warn("State is null, quitting");
            return;
        }

        // Index is decremented by one, yet the loop will bring it back up to the same number, causing the sound to
        // start over
        playIndex.decrementAndGet();
        context.getLogger().debug("Restarted current sound playback");
        stopSound();
    }

    /**
     * Jumps to previous sound-file if there is one, else jump to last sound-file
     */
    void previousFile() {
        if (getState() == null) {
            context.getLogger().warn("State is null, quitting");
            return;
        }
        context.getLogger().debug("Started playing previous sound");

        if (repeatSong.get()) {
            // Decrement index by 1 so that the loop will increase it back to the current song
            playIndex.decrementAndGet();
            stopSound();
        } else if (playIndex.get() > 0) {
            playIndex.decrementAndGet();
            playIndex.decrementAndGet();
            stopSound();
        } else if (repeatPlaylist.get()) {
            playIndex.set(soundFileMap.size() - 2);
            stopSound();
        } else {
            stopSession();
        }
    }

    /**
     * Sets the volume from 0 - 100
     *
     * @param volume volume level (from 0 - 100)
     */
    void controlVolume(double volume) {
        if (getState() == null) {
            context.getLogger().warn("State is null, quitting");
            return;
        }
        //TODO: implement volume control
        //player.setVolume(volume / 100);
        context.getLogger().debug("Set volume to " + volume + "%");
    }

    /**
     * Plays the sound at {@code path}
     *
     * @param soundId The id of the sound to be played
     * @throws java.lang.IndexOutOfBoundsException thrown if start or end time are out of bounds (-1 not included)
     */
    private void playSoundFile(SoundIdentity soundId, int startFrame, int endFrame) {
        inputStream = null;
        if (soundId == null) {
            audioFilePlayer.setCurrentSound(null);
            context.getLogger().debug("Stopped playback");
            return;
        } else if (soundId.getSoundInfo().getPath() != null) {
            String path = soundId.getSoundInfo().getPath();
            try {
                inputStream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                context.getLogger().error("Was not able to find " + path, e);
            }
        } else if (soundId.getSoundInfo().getURL() != null) {
            try {
                inputStream = soundId.getSoundInfo().getURL().openStream();
            } catch (IOException e) {
                context.getLogger().error("Was not able to find to find " +
                        soundId.getSoundInfo().getURL().toString(), e);
            }
        }

        updatePlaylist(soundId);

        context.getLogger().debug("Preparing for playback");
        if (inputStream == null) {
            context.getLogger().debug("An input stream was null, quiting");
            return;
        }

        try {
            player = new AdvancedPlayer(inputStream);
        } catch (JavaLayerException e) {
            context.getLogger().error("Unable to create AdvancedPlayer object", e);
        }
        state = READY_STATE;

        // Sets behavior at end of playback
        setOnEnd(soundId);

        startSecond = System.currentTimeMillis() / 1000;
        try {
            state = PLAYING_STATE;
            context.getLogger().debug("Started playback of " + soundId.getSoundInfo().getTrackInfo().getName());
            player.play(startFrame, endFrame);
        } catch (JavaLayerException e) {
            context.getLogger().error("Error playing sound file", e);
        }

    }

    /**
     * Sets the behavior of the SoundIdentity {@code id} at the end of its playback (so that the next song will be
     * started)
     * @param id the SoundIdentity for which to set an end behaviour
     */
    private void setOnEnd(SoundIdentity id) {
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent event) {
                Thread t = new Thread(() -> {
                    if (paused) {
                        pausedOnFrame = event.getFrame();
                        if (id.getSoundInfo().getFramesPerSecond() != -1) {
                            // Calculating current frame
                            pausedOnFrame = (int) ((System.currentTimeMillis() / 1000 - startSecond)
                                    * id.getSoundInfo().getFramesPerSecond());
                        } else {
                            // Not ideal value as it is a bit off, but for websites and other likes, there is no choice
                            pausedOnFrame = event.getFrame();
                        }
                        closeAll();
                        return;
                    }

                    context.getLogger().debug("Finished sound playback of: " + id.getSoundInfo().getName());
                    if (playIndex.get() > soundFileMap.size()) {
                        audioFilePlayer.setCurrentSound(null);
                        context.getLogger().debug("Stopped playback");
                        stopSession();
                        return;
                    }

                    // Increment sound index by 1 (this is where the loop "increments itself")
                    playIndex.incrementAndGet();

                    SoundIdentity id;
                    if (shuffle.get()) {
                        id = shuffeledSoundFileMap.get(playIndex.intValue());
                    } else {
                        id = soundFileMap.get(playIndex.intValue());
                    }
                    closeAll();

                    if (id != null) {
                        playSoundFile(id, 0, Integer.MAX_VALUE);
                    } else {
                        stopSession();
                        context.getLogger().debug("a null sound file found, stopping");
                    }
                });
                t.start();
            }
        });
    }

    /**
     * Gets the meta data and updates the sound id to the currently being played song
     *
     * @param soundId the sound id to update
     */
    private void updatePlaylist(SoundIdentity soundId) {
        if (shuffle.get()) {
            shuffeledPlaylist = soundLoader.getMetaData(shuffeledPlaylist, soundId.getSoundInfo());
            shuffeledPlaylist.setNewPosition(playIndex.get());
        } else {
            playlist = soundLoader.getMetaData(playlist, soundId.getSoundInfo());
            playlist.setNewPosition(playIndex.get());
        }

        audioFilePlayer.setCurrentSound(soundId);
    }

    private int[] setPlayDuration(SoundIdentity soundId) throws IndexOutOfBoundsException {
        outOfBoundsError = false;
        int[] startEndFrames = new int[2];

        // Gets the duration that the sound in milliseconds
        int duration;
        if (soundId.getSoundInfo().getDuration() != -1) {
            duration = (int) soundId.getSoundInfo().getDuration() * 1000;
        } else {
            duration = Integer.MAX_VALUE;
            context.getLogger().warn("Unable to get duration of " + soundId.getSoundInfo().getName() +
            ", so duration was set to Integer.MAX_VALUE");
        }

        // Checks if frame duration and frames per second exist
        int frameDuration;
        int framesPerSecond;
        if (soundId.getSoundInfo().getFramesPerSecond() == -1) {
            framesPerSecond = 1;
            frameDuration = Integer.MAX_VALUE;
        } else {
            framesPerSecond = (int)soundId.getSoundInfo().getFramesPerSecond();
            if (soundId.getSoundInfo().getDuration() != -1) {
                frameDuration = (int)(soundId.getSoundInfo().getDuration() * framesPerSecond);
            } else {
                frameDuration = Integer.MAX_VALUE;
            }
        }

        // Checks if start time exists
        if (soundId.getSoundInfo().getStartTime() == -1) {
            startEndFrames[0] = 0;
            soundId.getSoundInfo().setStartTime(0);
        } else if (soundId.getSoundInfo().getStartTime() >= 0
                && soundId.getSoundInfo().getStartTime() <= duration) {
            startEndFrames[0] = soundId.getSoundInfo().getStartTime() / 1000 * framesPerSecond;
        } else {
            outOfBoundsError = true;
            throw new IndexOutOfBoundsException("start-time out of bounds");
        }

        // Checks if end time exists
        if (soundId.getSoundInfo().getStopTime() == -1) {
            startEndFrames[1] = frameDuration;
            soundId.getSoundInfo().setStopTime((int) (soundId.getSoundInfo().getDuration() * 1000));
        } else if (soundId.getSoundInfo().getStopTime() >= 0
                && soundId.getSoundInfo().getStopTime() <= duration) {
            startEndFrames[1] = soundId.getSoundInfo().getStopTime() / 1000 * framesPerSecond;
        } else {
            outOfBoundsError = true;
            throw new IndexOutOfBoundsException("end-time out of bounds");
        }
        return startEndFrames;
    }

    private void resetSession() {
        soundFileMap.clear();
        playlist = null;
        if (player != null) {
            player.close();
        }
        closeAll();
        context.getLogger().debug("Resetting playback session");
    }

    /**
     * Run method for sound object, gets started on instantiation and waits for paths to process
     */
    void run(Playlist playlist) {
        resetSession();
        playIndex.set(0);
        soundFileMap = soundLoader.convertFromPlaylist(playlist);
        this.playlist = playlist;
        SoundIdentity id;
        if (shuffle.get()) {
            long seed = System.nanoTime();
            List<TrackInfo> trackInfos = new ArrayList<>(playlist.getQueue());
            Collections.shuffle(trackInfos, new Random(seed));
            shuffeledPlaylist = new Playlist(trackInfos);
            shuffeledSoundFileMap = soundLoader.convertFromPlaylist(shuffeledPlaylist);

            id = shuffeledSoundFileMap.get(playIndex.intValue());
            shuffeledPlaylist = soundLoader.getMetaData(shuffeledPlaylist, id.getSoundInfo());
        } else {
            id = soundFileMap.get(playIndex.intValue());
            this.playlist = soundLoader.getMetaData(this.playlist, id.getSoundInfo());
        }

        try {
            context.getLogger().debug("Setting play duration");
            int[] duration = setPlayDuration(id);
            Thread t = new Thread(() -> playSoundFile(id, duration[0], duration[1]));
            t.start();
        } catch (IndexOutOfBoundsException e) {
            context.getLogger().warn("Start or end times were probably out of bounds", e);
        }
    }

    /**
     * Returns true if an out of bounds error occurred with play indices, else false
     *
     * @return true if an out of bounds error occurred with play indices, else false
     */
    synchronized boolean isOutOfBoundsError() {
        return outOfBoundsError;
    }

    /**
     * Returns true if the player is set to shuffle the current playlist, else false
     *
     * @return true if the player is set to shuffle the current playlist, else false
     */
    synchronized AtomicBoolean getShuffle() {
        return shuffle;
    }

    /**
     * Sets whether or not the current playlist is to be shuffled
     *
     * @param shuffle true if the playlist is to be shuffled, else false
     */
    synchronized void setShuffle(AtomicBoolean shuffle) {
        this.shuffle = shuffle;

        if (shuffle.get() && playlist != null) {
            long seed = System.nanoTime();
            List<TrackInfo> trackInfos = new ArrayList<>(playlist.getQueue());
            Collections.shuffle(trackInfos, new Random(seed));
            shuffeledPlaylist = new Playlist(trackInfos);
            shuffeledSoundFileMap = soundLoader.convertFromPlaylist(shuffeledPlaylist);
        }
    }

    /**
     * Returns true if the player is set to repeat the current playlist, else false
     *
     * @return true if the player is set to repeat the current playlist, else false
     */
    synchronized AtomicBoolean getRepeatPlaylist() {
        return repeatPlaylist;
    }

    /**
     * Sets whether or not the current playlist is to be repeated
     *
     * @param repeatPlaylist true if the playlist is to be repeated, else false
     */
    synchronized void setRepeatPlaylist(AtomicBoolean repeatPlaylist) {
        this.repeatPlaylist = repeatPlaylist;
    }

    /**
     * Returns true if the player is set to repeat the current song, else false
     *
     * @return true if the player is set to repeat the current song, else false
     */
    synchronized AtomicBoolean getRepeatSong() {
        return repeatSong;
    }

    /**
     * Sets whether or not the current song is to be repeated
     *
     * @param repeatSong true if the song is to be repeated, else false
     */
    synchronized void setRepeatSong(AtomicBoolean repeatSong) {
        this.repeatSong = repeatSong;
    }
}