package jundl77.izou.izousound.outputplugin;

import intellimate.izou.system.Context;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * The {@code AudioFilePlayer} is a wrapper for the {@code SoundEngine}, which allows the AudioFilePlayer to put the
 * sound-engine in a single thread pool and hence control its actions externally, while the sound-engine is still
 * streaming sound.
 *
 * <p>
 *     You can play local sound files or URLs in one request, but not both.
 * </p>
 * <p>
 *     The {@code AudioFilePlayer} has any methods you would except in a normal music player. This is the class that should
 *     be used to play music.
 * </p>
 */
public class AudioFilePlayer {
    private SoundEngine soundEngine;
    private SoundIdentity currentSound;
    private Context context;

    /**
     * Instantiates a new {@code AudioFilePlayer} which can play mp3 and wav files.
     * <p>
     *     It has to be passed a List of file locations, which can either be paths to direct files, or paths to directories.
     *     The AudioFilePlayer goes through all paths recursively and adds any eligible sound files found to a list for
     *     later playback.
     * </p>
     * @param context The context of the OutputPlugin
     */
    public AudioFilePlayer(Context context) {
        this.context = context;
        try {
            this.soundEngine = new SoundEngine(context, this);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a new playback session with the paths passed to it as an array
     *
     * @param filePath {@code filePath} can be a path to a file or directory. Directories will be recursively searched
     *                                 for any more found eligible sound files.
     */
    public void playFile(List<String> filePath) {
        soundEngine.addSoundFiles(filePath);
    }

    /**
     * Starts a new playback session with the paths passed to it as an array
     * <p>
     *     This should probably only be used when 1 sound file is added, otherwise all sound files will play from
     *     {@code startTime} to {@code endTime}
     * </p>
     * @param filePath {@code filePath} can be a path to a file or directory. Directories will be recursively searched
     *                                 for any more found eligible sound files.
     * @param startTime the time all sound file in {@code filePath} should start in milliseconds
     * @param endTime the time all sound files in {@code filePath} should end in milliseconds
     */
    public void playFile(List<String> filePath, int startTime, int endTime) {
        soundEngine.addSoundFiles(filePath, startTime, endTime);
    }

    /**
     * Starts a new playback session with the URLs passed to it as an array
     * Only HTTP is supported (No HTTPS)
     *
     * @param url {@code filePath} a valid URL to a sound file
     *                            Ex: http://www.example.com/test.mp3
     */
    public void playURL(List<URL> url) {
        playURL(url, -1, -1);
    }

    /**
     * Starts a new playback session with the URLs passed to it as an array
     * Only HTTP is supported (No HTTPS)
     * <p>
     *     This should probably only be used when 1 sound file is added, otherwise all sound files will play from
     *     {@code startTime} to {@code endTime}
     * </p>
     * @param url {@code filePath} a valid URL to a sound file
     *                            Ex: http://www.example.com/test.mp3
     * @param startTime the time all sound file in {@code filePath} should start in milliseconds
     * @param endTime the time all sound files in {@code filePath} should end in milliseconds
     */
    public void playURL(List<URL> url, int startTime, int endTime) {
        soundEngine.addSoundURL(url, startTime, endTime);
    }

    /**
     * Resumes sound if it has been paused
     */
    public void resume() {
        soundEngine.resumeSound();
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
    public void setCurrentSound(SoundIdentity currentSound) {
        this.currentSound = currentSound;
    }
}