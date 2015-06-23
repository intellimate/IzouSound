package jundl77.izou.izousound;

import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class generates playlists for the IzouSound music player. The IzouSound music player can ONLY play playlists
 * that have been generated using the format used in this class. Playlists can be generated from local file paths and
 * URLs.
 */
public class PlaylistGenerator {
    public static String URL_TYPE = "URL";
    public static String FILE_TYPE = "FILE";
    public static String DATA_SEPERATOR = "@@@";

    private Context context;
    private TrackInfoGenerator trackInfoGenerator;

    /**
     * Creates a new PlaylistGenerator object
     *
     * @param context the context of the addOn, mainly used for logging
     */
    public PlaylistGenerator(Context context) {
        this.context = context;
        this.trackInfoGenerator = new TrackInfoGenerator();
    }

    /**
     * Takes a list of sound-file paths and converts them into a playlist. Invalid paths are skipped. (Whether or not
     * the path points to a sound file is not checked) This method also has the ability to crop ALL sound files found
     * in {@code soundFilePaths} to the desired {@code startTime} and {@code stopTime}. (both in milliseconds)
     *
     * @param soundFilePaths the list of paths to sound files that should be turned into a playlist
     * @param startTime the start time of the sound files (in milliseconds)
     * @param stopTime the stop time of the sound files  (in milliseconds)
     */
    public Playlist createFilePlaylist(List<String> soundFilePaths, int startTime, int stopTime) {
        if (soundFilePaths == null) {
            return null;
        }

        context.getLogger().debug("Creating file based playlist..");
        List<TrackInfo> trackInfos = soundFilePaths
                .stream()
                .filter(p -> p != null)
                .map(path -> trackInfoGenerator.generatFileTrackInfo(path, startTime, stopTime))
                .collect(Collectors.toList());
        context.getLogger().debug("Found " + trackInfos.size() + " items");

        return new Playlist(trackInfos);
    }

    /**
     * Takes a list of sound-file paths and converts them into a playlist. Invalid paths are skipped. (Whether or not
     * the path points to a sound file is not checked) (for time cropping,
     * look at {@link #createFilePlaylist(List, int, int)}
     *
     * @param soundFilePaths the list of paths to sound files that should be turned into a playlist
     */
    public Playlist createFilePlaylist(List<String> soundFilePaths) {
        return createFilePlaylist(soundFilePaths, -1, -1);
    }

    /**
     * Takes a list of sound-file paths and converts them into a playlist. Invalid paths are skipped. (Whether or not
     * the path points to a sound file is not checked) All paths contained in {@code paths} are recursively descended
     * through all folders until only files are found. All mp3 and wav files encountered in the process are added to the
     * playlist. That means you can add a single path to your music folder, and all mp3 and wav files in that folder (
     * even if they are contained in sub folders) will be added to the playlist.
     *
     * @param paths the list of paths to sound files that should be turned into a playlist
     */
    public Playlist createRecursiveSearchFilePlaylist(List<String> paths) {
        if (paths == null) {
            return null;
        }

        context.getLogger().debug("Doing recursive search for more sound files");
        List<TrackInfo> trackInfos = new ArrayList<>();
        for (String filePath : paths) {
            if (filePath != null && !new File(filePath).exists()) {
                context.getLogger().error(filePath + " does not exists - Unable to play sound");
            } else {
                recursiveSoundFileSearch(trackInfos, filePath);
            }
        }
        context.getLogger().debug("Found " + trackInfos.size() + " files");

        return new Playlist(trackInfos);
    }

    /**
     * Takes a list of sound URLs and converts them into a playlist. URLs have to be HTTP and NOT HTTPS.
     * This method also has the ability to crop ALL sound urls found in {@code soundURLs} to the desired
     * {@code startTime} and {@code stopTime}. (both in milliseconds)
     * <p>
     *     Playlists generated with this method can be passed to the
     *     {@link jundl77.izou.izousound.outputplugin.AudioFilePlayer} where they can be played.
     * </p>
     * @param soundURLs the list of paths to sound files that should be turned into a playlist
     * @param startTime the start time of the sound files (in milliseconds)
     * @param stopTime the stop time of the sound files  (in milliseconds)
     */
    public Playlist createURLPlaylist(List<URL> soundURLs, int startTime, int stopTime) {
        if (soundURLs == null) {
            return null;
        }

        context.getLogger().debug("Creating URL based playlist..");
        List<TrackInfo> trackInfos = soundURLs
                .stream()
                .map(url -> trackInfoGenerator.generatURLTrackInfo(url, startTime, stopTime))
                .collect(Collectors.toList());
        context.getLogger().debug("Found " + trackInfos.size() + " items");

        return new Playlist(trackInfos);
    }

    /**
     * Takes a list of sound URLs and converts them into a playlist. URLs have to be HTTP and NOT HTTPS.
     *
     * @param soundURLs the list of paths to sound files that should be turned into a playlist
     */
    public Playlist createURLPlaylist(List<URL> soundURLs) {
        return createURLPlaylist(soundURLs, -1, -1);
    }

    /**
     * Combines playlist1 and playlist2 into a new playlist. Playlist1 and playlist2 should be be created by one of the
     * methods contained in this class, otherwise they will not be able to be played by IzouSound the output plugin.
     * <p>
     *     This method makes it possible to combine URL and file based playlist into a single playlist.
     * </p>
     *
     * @param playlist1 The first playlist to combine
     * @param playlist2 The second playlist to combine
     * @return a new playlist containing playlist2 appended to the end of playlist1
     */
    public Playlist combinePlaylist(Playlist playlist1, Playlist playlist2) {
        if (playlist1 == null || playlist2 == null) {
            return null;
        }

        List<TrackInfo> trackInfos = playlist1.getQueue();
        trackInfos.addAll(playlist2.getQueue());
        return new Playlist(trackInfos);
    }

    /**
     * Recursively goes through the folder in {@code filePath} to find all mp3 and wav files in sub folders and add them
     * to {@code trackInfos}
     *
     * @param filePath the path to recursively search for sound files (if it is a sound file, add it directly)
     */
    private void recursiveSoundFileSearch(List<TrackInfo> trackInfos, String filePath) {
        if (new File(filePath).isFile()
                && (filePath.endsWith(".mp3")
                || filePath.endsWith(".wav"))) {
            trackInfos.add(trackInfoGenerator.generatFileTrackInfo(filePath, -1, -1));
        } else if (new File(filePath).isDirectory()) {
            File[] files = new File(filePath).listFiles();
            if (files == null) {
                return;
            }
            for (File path : files) {
                recursiveSoundFileSearch(trackInfos, path.getAbsolutePath());
            }
        }
    }
}
