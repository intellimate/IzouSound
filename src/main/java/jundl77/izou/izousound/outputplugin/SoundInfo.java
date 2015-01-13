package jundl77.izou.izousound.outputplugin;

import java.io.File;

/**
 * SoundInfo contains general data about the sound file for which it is created
 *
 * @author Julian Brendl
 * @version 1.0
 */
class SoundInfo {
    private String name;
    private String path;
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
     * Gets the file path to the sound file
     *
     * @return path to the sound file
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the start time of the sound (in milliseconds), if the start time is -1 the sound is played from the beginning
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
