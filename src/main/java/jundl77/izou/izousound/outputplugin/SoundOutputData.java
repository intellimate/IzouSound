package jundl77.izou.izousound.outputplugin;

import java.net.URL;
import java.util.List;

/**
 * SoundOutputData is the output data object for the {@link SoundOutputPlugin}. It can store a URL or a path to a sound
 * file to be played, or both. If one type is not wanted, just set it to null in the constructor.
 */
public class SoundOutputData {
    private List<String> paths;
    private List<URL> urls;
    private int startTime;
    private int stopTime;

    /**
     * Creates a new SoundOuputData with either a list of paths or urls to be played, or both. If one type is not wanted
     * just set that type to null. The whole file is played.
     *
     * @param paths the paths to the sound files to be played
     * @param urls the URLs to the sounds to be played
     */
    public SoundOutputData(List<String> paths, List<URL> urls) {
        this(paths, urls, -1, -1);
    }

    /**
     * Creates a new SoundOuputData with either a list of paths or urls to be played, or both. If one type is not wanted
     * just set that type to null.
     *
     * This should probably only be used when 1 sound file is added, otherwise all sound files will play from
     * {@code startTime} to {@code endTime}
     *
     * @param paths the paths to the sound files to be played
     * @param urls the URLs to the sounds to be played
     * @param startTime the time all sound file in {@code filePath} should start in milliseconds
     * @param stopTime the time all sound files in {@code filePath} should end in milliseconds
     */
    public SoundOutputData(List<String> paths, List<URL> urls, int startTime, int stopTime) {
        this.paths = paths;
        this.urls = urls;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    /**
     * Get paths to be played
     *
     * @return paths to be played
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * Get urls to be played
     *
     * @return urls to be played
     */
    public List<URL> getURLs() {
        return urls;
    }

    /**
     * Gets the startTime of all files associated with this soundOutputData, in milliseconds.
     *
     * <p>If -1 is used, the playback is started front the beginning</p>
     *
     * @return playback starting point for this {@code SoundOutputData}, or -1 if it starts at 0
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Gets the stopTime of all files associated with this soundOutputData, in milliseconds.
     *
     * <p>If -1 is used, the playback is stopped at the end</p>
     *
     * @return playback ending point for this {@code SoundOutputData}, or -1 if it plays until the end
     */
    public int getStopTime() {
        return stopTime;
    }
}
