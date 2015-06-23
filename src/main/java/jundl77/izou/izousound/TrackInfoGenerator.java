package jundl77.izou.izousound;

import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.io.File;
import java.net.URL;

/**
 * This class generates track infos for the IzouSound music player. The IzouSound music player can ONLY play track infos
 * that have been generated using the format used in this class. Track infos can be generated from local file paths and
 * URLs.
 */
public class TrackInfoGenerator {

    /**
     * Creates a new TrackInfoGenerator object
     */
    public TrackInfoGenerator() {
    }

    /**
     * Generates a file based track info from the path, start and stop time
     *
     * @param path path to the sound file
     * @param startTime the start time of the sound file (-1 for start from beginning)
     * @param endTime the end time of the sound file (-1 for full length)
     * @return a new TrackInfo generated based on the above data
     */
    public TrackInfo generatFileTrackInfo(String path, int startTime, int endTime) {
        if (path == null) {
            return null;
        }

        String data = PlaylistGenerator.FILE_TYPE + PlaylistGenerator.DATA_SEPERATOR + path +
                PlaylistGenerator.DATA_SEPERATOR + startTime + PlaylistGenerator.DATA_SEPERATOR + endTime;
        String[] pathParts = path.split(File.separator);
        String name = pathParts[pathParts.length - 1];
        return new TrackInfo(name, null, null, null, null, data, null, null, null, -1);
    }

    /**
     * Generates a URL based track info from the URL, start and stop time
     *
     * @param url url to the sound file
     * @param startTime the start time of the sound file (-1 for start from beginning)
     * @param endTime the end time of the sound file (-1 for full length)
     * @return a new TrackInfo generated based on the above data
     */
    public TrackInfo generatURLTrackInfo(URL url, int startTime, int endTime) {
        if (url == null) {
            return null;
        }

        String data = PlaylistGenerator.URL_TYPE + PlaylistGenerator.DATA_SEPERATOR + url.toExternalForm() +
                PlaylistGenerator.DATA_SEPERATOR + startTime + PlaylistGenerator.DATA_SEPERATOR + endTime;
        String[] pathParts = url.toExternalForm().split("/");
        String name = pathParts[pathParts.length - 1];
        return new TrackInfo(name, null, null, null, null, data, null, null, null, -1);
    }
}
