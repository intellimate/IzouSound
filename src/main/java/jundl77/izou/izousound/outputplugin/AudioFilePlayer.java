package jundl77.izou.izousound.outputplugin;

import jundl77.izou.izousound.PlaylistGenerator;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;
import org.intellimate.izou.sdk.frameworks.music.player.template.CommandHandler;
import org.intellimate.izou.sdk.frameworks.music.player.template.Player;
import org.intellimate.izou.sdk.frameworks.music.resources.CommandResource;
import org.intellimate.izou.sdk.frameworks.music.resources.PlaylistResource;
import org.intellimate.izou.sdk.frameworks.music.resources.TrackInfoResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code AudioFilePlayer} is a wrapper for the {@code SoundEngine}. It controls the sound-engine.
 */
public class AudioFilePlayer extends Player {
    public static final String ID = AudioFilePlayer.class.getCanonicalName();
    private SoundEngine soundEngine;
    private SoundIdentity currentSound;
    private Context context;

    /**
     * Instantiates a new {@code AudioFilePlayer} which can play mp3 and wav files.
     *
     * @param context The context of the OutputPlugin
     */
    public AudioFilePlayer(Context context) {
        super(context, ID, false, true, true, true, true, true, true);
        this.context = context;
        this.soundEngine = new SoundEngine(context, this);

        CommandHandler commandHandler = getCommandHandler();
        commandHandler.setNextPreviousController(command -> {
            if (command.equals(CommandResource.NEXT)) {
                nextSound();
            } else if (command.equals(CommandResource.PREVIOUS)) {
                previousSound();
            }
        });
        commandHandler.setPlayPauseController(command -> {
            if (command.equals(CommandResource.PLAY)) {
                resume();
            } else if (command.equals(CommandResource.PAUSE)) {
                pause();
            }
        });
        commandHandler.setTrackSelectorController(this::jumpToTrackInfo);
        commandHandler.setVolumeChangeableController(volume -> {
            setVolume(volume.getVolume());
        });
        commandHandler.setJumpProgressController(progress -> {

        });
    }

    /**
     * Starts a new playback session with the given playlist (if a current playlist session is in progress, it is
     * canceled)
     * <p>
     *     Playlists can only be played if they have been generated by the {@link PlaylistGenerator}
     * </p>
     * @param playlist the playlist to play
     */
    public void playPlaylist(Playlist playlist) {
        if (soundEngine.getState() != null && soundEngine.getState().equals(SoundEngine.PLAYING_STATE)) {
            soundEngine.stopSession();
        }

        soundEngine.run(playlist);
        waitForReady();
    }

    private void waitForReady() {
        int sleepCounter = 0;
        while ((soundEngine.getState() == null || !soundEngine.getState().equals(SoundEngine.PLAYING_STATE))
                && sleepCounter < 50) {
            try {
                Thread.sleep(100);
                sleepCounter++;
            } catch (InterruptedException e) {
                context.getLogger().error("Error while sleeping", e);
            }
        }

        if (sleepCounter >= 50) {
            context.getLogger().error("Player timed out");
            stop();
        }
    }

    /**
     * Resumes sound if it has been paused
     */
    public void resume() {
        try {
            soundEngine.resumeSound();
        } catch (IllegalStateException e) {
            context.getLogger().error("Unable to resume, because sound is not paused", e);
        }
    }

    /**
     * Stops the entire playback session (Do not confuse with @{code pause}, which just pauses the playback
     */
    public void stop() {
        soundEngine.stopSession();
    }

    /**
     * Pauses the playback session (Do not confuse with @{code stop}, which entirely stops the playback
     */
    public void pause() {
        soundEngine.pauseSound();
    }

    /**
     * Jumps to next sound-file if there is one, else jumps back to the start
     */
    public void nextSound() {
        soundEngine.nextFile();
    }

    /**
     * Jumps to previous sound-file if there is one, else jump to last sound-file
     */
    public void previousSound() {
        soundEngine.previousFile();
    }

    /**
     * Jumps back to beginning of current sound file if one is playing
     */
    public void restartSound() {
        soundEngine.restartFile();
    }

    /**
     * Jumps to the track info in the playlist if it is found
     *
     * @param trackInfo the track info to jump to and play
     */
    public void jumpToTrackInfo(TrackInfo trackInfo) {
        try {
            soundEngine.jumpToFile(trackInfo);
        } catch (IllegalArgumentException e) {
            context.getLogger().error("TrackInfo not found", e);
        }
    }

    /**
     * Sets the volume from 0 - 100
     * <p>
     *     If values greater than 100 or less than 0 are entered, they are set to 100 or 0, respectively
     * </p>
     * @param volume volume level (from 0 - 100)
     */
    public void setVolume(double volume) {
        if (volume > 100) {
            volume = 100;
        } else if (volume < 0) {
            volume = 0;
        }
        soundEngine.controlVolume(volume);
    }

    /**
     * Gets the current sound, null if none is playing
     *
     * @return the current sound, null if none is playing
     */
    public SoundIdentity getCurrentSound() {
        return currentSound;
    }

    /**
     * Sets the current sound, null if none is playing
     *
     * @param currentSound the current sound to set, null if none is playing
     */
    void setCurrentSound(SoundIdentity currentSound) {
        this.currentSound = currentSound;
        if (currentSound != null && getCurrentPlaylist() != null) {
            updatePlayInfo(currentSound.getSoundInfo().getTrackInfo());
        } else if (getCurrentPlaylist() != null) {
            updatePlayInfo((TrackInfo) null);
            stopMusicPlayback();
        }
    }

    /**
     * Sets the current playlist, null if none is playing
     *
     * @param playlist the current playlist to set, null if none is playing
     */
    void setCurrentPlaylist(Playlist playlist) {
        if (playlist != null) {
            updatePlayInfo(playlist);
        } else {
            updatePlayInfo((Playlist) null);
        }
    }

    /**
     * Returns true if an out of bounds error occurred with play indices, else false
     *
     * @return true if an out of bounds error occurred with play indices, else false
     */
    public boolean isOutOfBoundsError() {
        return soundEngine.isOutOfBoundsError();
    }

    /**
     * Returns true if the player is set to shuffle the current playlist, else false
     *
     * @return true if the player is set to shuffle the current playlist, else false
     */
    public boolean getShuffle() {
        return soundEngine.getShuffle().get();
    }

    /**
     * Sets whether or not the current playlist is to be shuffled
     *
     * @param shuffle true if the playlist is to be shuffled, else false
     */
    public void setShuffle(boolean shuffle) {
        soundEngine.setShuffle(new AtomicBoolean(shuffle));
    }

    /**
     * Returns true if the player is set to repeat the current playlist, else false
     *
     * @return true if the player is set to repeat the current playlist, else false
     */
    public boolean getRepeatPlaylist() {
        return soundEngine.getRepeatPlaylist().get();
    }

    /**
     * Sets whether or not the current playlist is to be repeated
     *
     * @param repeatPlaylist true if the playlist is to be repeated, else false
     */
    public void setRepeatPlaylist(boolean repeatPlaylist) {
        soundEngine.setRepeatPlaylist(new AtomicBoolean(repeatPlaylist));
    }

    /**
     * Returns true if the player is set to repeat the current song, else false
     *
     * @return true if the player is set to repeat the current song, else false
     */
    public boolean getRepeatSong() {
        return soundEngine.getRepeatSong().get();
    }

    /**
     * Sets whether or not the current song is to be repeated
     *
     * @param repeatSong true if the song is to be repeated, else false
     */
    public void setRepeatSong(boolean repeatSong) {
        soundEngine.setRepeatSong(new AtomicBoolean(repeatSong));
    }

    @Override
    public void mute() {
        pause();
    }

    @Override
    public void unMute() {
        resume();
    }

    @Override
    public void stopSound() {
        stop();
    }

    @Override
    public void play(EventModel eventModel) {
        Optional<Playlist> playlistTemp = PlaylistResource.getPlaylist(eventModel);
        Optional<TrackInfo> trackInfoTemp = TrackInfoResource.getTrackInfo(eventModel);
        Playlist playlist = null;

        if (playlistTemp.isPresent()) {
            playlist = playlistTemp.get();
        }

        if (trackInfoTemp.isPresent()) {
            TrackInfo trackInfo = trackInfoTemp.get();
            List<TrackInfo> trackInfoList = new ArrayList<>();
            trackInfoList.add(trackInfo);
            Playlist newPlaylist = new Playlist(trackInfoList);

            if (playlist != null) {
                PlaylistGenerator generator = new PlaylistGenerator(context);
                playlist = generator.combinePlaylist(playlist, newPlaylist);
            } else {
                playlist = newPlaylist;
            }
        }

        playPlaylist(playlist);
    }
}