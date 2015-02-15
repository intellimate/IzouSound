package jundl77.izou.izousound.outputplugin;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import intellimate.izou.system.Context;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * SoundInfo contains general data about the sound file for which it is created
 *
 * @author Julian Brendl
 * @version 1.0
 */
class SoundInfo {
    private Context context;
    private String name;
    private final String path;
    private final URL url;
    private int startTime;
    private int stopTime;
    private long framesPerSecond;
    private long duration; // In seconds
    private String track;
    private String artist;
    private String title;
    private String album;
    private String year;
    private String genre;
    private String comment;
    public static int counter = 0;

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param context The context of the OutputPlugin
     * @param path the path to the sound file
     */
    public SoundInfo(Context context, String path) {
        this(context, path, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param context The context of the OutputPlugin
     * @param path the path to the sound file
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param stopTime the stop time of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(Context context, String path, int startTime, int stopTime) {
        String[] fileParts = path.split(File.separator);
        this.context = context;
        this.name = fileParts[fileParts.length - 1];
        this.path = path;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.url = null;
        this.framesPerSecond = -1;
        this.duration = -1;
        this.track = null;
        this.artist = null;
        this.title = null;
        this.album = null;
        this.year = null;
        this.genre = null;
        this.comment = null;
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param context The context of the OutputPlugin
     * @param url the url to the sound file
     */
    public SoundInfo(Context context, URL url) {
        this(context, url, -1, -1);
    }

    /**
     * Creates a new SoundInfo object, which contains general information about the sound.
     *
     * @param context The context of the OutputPlugin
     * @param url the url to the sound file
     * @param startTime the start time of the sound file (in milliseconds), if -1 it starts from the beginning
     * @param stopTime the stop time of the sound file (in milliseconds), if -1 it stops at the end
     */
    public SoundInfo(Context context, URL url, int startTime, int stopTime) {
        String[] fileParts = url.toString().split("/");
        this.context = context;
        this.name = fileParts[fileParts.length - 1];
        this.url = url;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.path = null;
        this.framesPerSecond = -1;
        this.duration = -1;
        this.track = null;
        this.artist = null;
        this.title = null;
        this.album = null;
        this.year = null;
        this.genre = null;
        this.comment = null;
    }

    public void getMetaData() {
        counter++;
        if (counter == 300) {
            System.out.println("at 600");
        }
        this.framesPerSecond = 1;
        try {
            Mp3File mp3file = new Mp3File(path);
            this.framesPerSecond = mp3file.getFrameCount() / mp3file.getLengthInSeconds();
            this.duration = mp3file.getLengthInSeconds();

            if (mp3file.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                this.track = id3v1Tag.getTrack();
                this.artist = id3v1Tag.getArtist();
                this.title = id3v1Tag.getTitle();
                this.album = id3v1Tag.getAlbum();
                this.year = id3v1Tag.getYear();
                this.genre = id3v1Tag.getGenre() + " " + id3v1Tag.getGenreDescription();
                this.comment = id3v1Tag.getComment();
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
            //this.context.logger.getLogger().error("Error getting meta data for sound file: " + path, e);
        }
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
    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

    /**
     * Gets name of the sound if it is known, else returns -1
     *
     * @return name of the sound
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        return name;
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
     * Gets the duration of the song if it is known, else returns null
     *
     * @return the duration of the song
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Gets the track number if it is known, else returns null
     *
     * @return the track number
     */
    public String getTrack() {
        return track;
    }

    /**
     * Gets the artist of the song if it is known, else returns null
     *
     * @return the artist of the song
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the album of the song if it is known, else returns null
     *
     * @return the track number
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the year of the song if it is known, else returns null
     *
     * @return the year of the song
     */
    public String getYear() {
        return year;
    }

    /**
     * Gets the genre of the song if it is known, else returns null
     *
     * @return the genre of the song
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets any comments of the song if it is known, else returns null
     *
     * @return comments of the song
     */
    public String getComment() {
        return comment;
    }
}
