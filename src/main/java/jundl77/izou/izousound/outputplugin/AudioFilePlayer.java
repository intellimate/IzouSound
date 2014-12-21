package jundl77.izou.izousound.outputplugin;

import intellimate.izou.system.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * The {@code AudioFilePlayer} is a wrapper for the {@code SoundEngine}, which allows the AudioFilePlayer to put the
 * sound-engine in a single thread pool and hence control its actions externally, while the sound-engine is still
 * streaming sound.
 *
 * The {@code AudioFilePlayer} has any methods you would except in a normal music player. This is the class that should
 * be used to play music, and NOT {@code SoundEngine}. {@code SoundEngine} is only backend and should not be touched.
 */
class AudioFilePlayer {
    private ExecutorService executorService;
    private SoundEngine soundEngine;
    private Context context;

    /**
     * Instantiates a new {@code AudioFilePlayer} which can play mp3 and wav files.
     *
     * It has to be passed a List of file locations, which can either be paths to direct files, or paths to directories.
     * The AudioFilePlayer goes through all paths recursively and adds any eligible sound files found to a list for
     * later playback.
     *
     * @param context The context of the OutputPlugin
     */
    public AudioFilePlayer(Context context) {
        this.context = context;
        this.soundEngine = new SoundEngine(context);
        this.executorService = context.threadPool.getThreadPool();
        executorService.execute(soundEngine);
    }

    /**
     * Starts a new playback session with the paths passed to it as an array
     *
     * @param filePath {@code filePath} can be a path to a file or directory. Directories will be recursively searched
     *                                 for any more found eligible sound files.
     */
    public void play(List<String> filePath) {
        if (!soundEngine.isActive()) {
            soundEngine.addSoundFiles(filePath);
        }
    }

    /**
     * Resumes sound if it has been paused
     */
    public void resume() {
        if (soundEngine.isPaused()) {
            soundEngine.resumeSound();
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
     *
     */
    public void setVolume() {}
}