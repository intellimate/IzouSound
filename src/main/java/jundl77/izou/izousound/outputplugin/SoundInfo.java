package jundl77.izou.izousound.outputplugin;

import java.io.File;
import java.net.URL;

/**
 * SoundInfo contains general data about the sound file for which it is created
 *
 * @author Julian Brendl
 * @version 1.0
 */
class SoundInfo {
    private String name;
    private final String path;
    private final URL url;
    private int startTime;
    private int stopTime;

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param path the path to the sound file
     */
    public SoundInfo(String path) {
        this(path, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param path the path to the sound file
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param stopTime the stop time of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(String path, int startTime, int stopTime) {
        String[] fileParts = path.split(File.separator);
        this.name = fileParts[fileParts.length - 1];
        this.path = path;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.url = null;
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param url the url to the sound file
     */
    public SoundInfo(URL url) {
        this(url, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param url the url to the sound file
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param stopTime the stop time of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(URL url, int startTime, int stopTime) {
        String[] fileParts = url.toString().split("/");
        this.name = fileParts[fileParts.length - 1];
        this.url = url;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.path = null;
    }

    /**
     * Gets name of the sound
     *
     * @return name of the sound
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the file path to the sound file, null if the {@code SoundInfo} object does not have a file path
     *
     * @return path to the sound file, null if the {@code SoundInfo} object does not have a file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the URL to the sound file, null if the {@code SoundInfo} object does not have a URL
     *
     * @return URL to the sound file, null if the {@code SoundInfo} object does not have a URL
     */
    public URL getURL() {
        return url;
    }

    /**
     * Gets the start time of the sound (in milliseconds), if the start time is -1 the sound is played from the
     * beginning
     *
     * @return the start time of the sound, -1 if not specified
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Gets the stop time of the sound (in milliseconds), if the stop time is -1 the sound is played until the end
     *
     * @return the stop time of the sound, -1 if not specified
     */
    public int getStopTime() {
        return stopTime;
    }
}
