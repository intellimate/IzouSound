package jundl77.izou.izousound.outputplugin;

import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.net.URL;

/**
 * SoundInfo is an internal "extension" of {@link TrackInfo}. While it contains a TrackInfo, it also contains a start
 * and stop time to enable cropping for sound files, and either a URL or a path to the sound file to be played. These
 * are the only two types of playback supported by IzouSound as of right now.
 *
 * @author Julian Brendl
 * @version 1.0
 */
class SoundInfo {
    private String path;
    private URL url;
    private int startTime;
    private int durationTime;
    private long framesPerSecond;
    private TrackInfo trackInfo;
    private boolean hasMetaData;

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param trackInfo the track info around which to base the sound info
     * @param path the path to the sound file, if it is a sound file
     */
    public SoundInfo(TrackInfo trackInfo, String path) {
        this(trackInfo, path, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param trackInfo the track info around which to base the sound info
     * @param url the url to the sound on the internet, if it is on the internet (has to be http, not https)
     */
    public SoundInfo(TrackInfo trackInfo, URL url) {
        this(trackInfo, url, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param trackInfo the track info around which to base the sound info
     * @param path the path to the sound file, if it is a sound file
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param durationTime the duration of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(TrackInfo trackInfo, String path, int startTime, int durationTime) {
        this.trackInfo = trackInfo;
        this.startTime = startTime;
        this.durationTime = durationTime;
        this.url = null;
        this.path = path;
        this.framesPerSecond = -1;
        this.hasMetaData = false;
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param trackInfo the track info around which to base the sound info
     * @param url the url to the sound on the internet, if it is on the internet (has to be http, not https)
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param durationTime the duration of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(TrackInfo trackInfo, URL url, int startTime, int durationTime) {
        this.trackInfo = trackInfo;
        this.startTime = startTime;
        this.durationTime = durationTime;
        this.url = url;
        this.path = null;
        this.framesPerSecond = -1;
        this.hasMetaData = false;
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
     * Gets the track info of the sound info
     *
     * @return the track info of the sound info
     */
    public TrackInfo getTrackInfo() {
        return trackInfo;
    }

    /**
     * Gets the name of the sound info if it has one
     *
     * @return the name of the sound info if it has one
     */
    public String getName() {
        if (trackInfo.getName().isPresent()) {
            return trackInfo.getName().get();
        } else {
            return null;
        }
    }

    /**
     * Gets the full length duration of the sound info (how long it actually is, not what length is has been clipped to)
     *
     * @return the full length duration of the sound info (how long it actually is, not what length is has been clipped to)
     */
    public long getFullLengthDuration() {
        if (trackInfo.getDuration().isPresent()) {
            return trackInfo.getDuration().get() / 1000;
        } else {
            return -1;
        }
    }

    /**
     * Gets the frames per second of the song if it is known, else returns -1
     *
     * @return the frames per second of the song
     */
    public long getFramesPerSecond() {
        return framesPerSecond;
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
    public int getDurationTime() {
        return durationTime;
    }

    /**
     * Returns true if the meta data has already been added, else false
     *
     * @return true if the meta data has already been added, else false
     */
    public boolean getHasMetaData() {
        return hasMetaData;
    }

    /**
     * Sets the track info object for this sound info
     *
     * @param trackInfo the track info to set
     */
    public void setTrackInfo(TrackInfo trackInfo) {
        this.trackInfo = trackInfo;
    }

    /**
     * Sets the start time of the sound (in milliseconds)
     *
     * @param startTime the start time of the sound
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the stop time of the sound (in milliseconds), if the stop time is -1 the sound is played until the end
     *
     * @param stopTime the stop time of the sound
     */
    public void setDurationTime(int stopTime) {
        this.durationTime = stopTime;
    }

    /**
     * Gets the frames per second of the song if it is known, else returns -1
     *
     * @param framesPerSecond the frames per second of the song to set
     */
    public void setFramesPerSecond(long framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }


    /**
     * Sets whether the sound info already has its meta data or not
     *
     * @param hasMetaData sets whether the sound info already has its meta data or not
     */
    public void setHasMetaData(boolean hasMetaData) {
        this.hasMetaData = hasMetaData;
    }
}
