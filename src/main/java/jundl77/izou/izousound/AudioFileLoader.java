package jundl77.izou.izousound;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import jundl77.izou.izousound.outputplugin.SoundInfo;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julianbrendl on 6/12/15.
 */
public class AudioFileLoader {

    public static String URL_TYPE = "URL";
    public static String FILE_TYPE = "FILE";

    /**
     * Passes a new list with sound files to the run method, if all requirements are met (no null etc.)
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     * @param startTime the start time of the sound file (in milliseconds)
     * @param stopTime the stop time of the sound file  (in milliseconds)
     */
    public void addSoundFiles(List<String> soundFilePaths, int startTime, int stopTime) {
        boolean shouldPlay = false;
        List<SoundInfo> soundInfos = new ArrayList<>();
        for (String path : soundFilePaths) {
            if (new File(path).exists()) {
                SoundInfo soundInfo = new SoundInfo(context, path, startTime, stopTime);
                soundInfos.add(soundInfo);
                shouldPlay = true;
            }
        }
        if (shouldPlay) {
            context.getLogger().debug("Added sound files to queue");
            run(soundInfos);
        } else {
            context.getLogger().debug("No valid sound files found, quitting");
        }
    }

    /**
     * Passes a new list with sound files to the run method, if all requirements are met (no null etc.)
     *
     * @param soundURLs the list of URLs to sound files that should be played
     * @param startTime the start time of the sound file (in milliseconds)
     * @param stopTime the stop time of the sound file  (in milliseconds)
     */
    public void addSoundURL(List<URL> soundURLs, int startTime, int stopTime) {
        List<SoundInfo> soundInfos = new ArrayList<>();

        for (URL url : soundURLs) {
            SoundInfo soundInfo = new SoundInfo(context, url, startTime, stopTime);
            soundInfos.add(soundInfo);
        }
        context.getLogger().debug("Adding sound URLs to queue");

        run(soundInfos);
    }

    /**
     * Passes a new list with sound files to the run method, if all requirements are met (no null etc.)
     *
     * @param soundFilePaths the list of paths to sound files that should be played
     *
     */
    public void addSoundFiles(List<String> soundFilePaths) {
        addSoundFiles(soundFilePaths, -1, -1);
    }

    /**
     * Recursively goes through all files to play at path
     *
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     */
    private void recursiveSoundFileSearch(SoundInfo soundInfo) {
        String filePath = soundInfo.getPath();
        if (new File(filePath).isFile()
                && (filePath.endsWith(".mp3")
                || filePath.endsWith(".wav"))) {
            addToQueuedSongs(soundInfo);
        } else if (new File(filePath).isDirectory()) {
            File[] files = new File(filePath).listFiles();

            if (files == null)
                return;

            for (File path: files) {
                String pathString = path.getAbsolutePath();
                recursiveSoundFileSearch(new SoundInfo(context, pathString));
            }
        }
    }

    private void fillQueuedSoundFiles(List<SoundInfo> fileInfos) {
        context.getLogger().debug("Doing recursive search for more sound files");
        if (fileInfos.get(0).getPath() != null) {
            for (SoundInfo soundInfo : fileInfos) {
                String filePath = soundInfo.getPath();
                if (!new File(filePath).exists()) {
                    context.getLogger().error(filePath + " does not exists - Unable to play sound");
                } else {
                    recursiveSoundFileSearch(soundInfo);
                }
            }
        } else if (fileInfos.get(0).getURL() != null) {
            for (SoundInfo soundInfo : fileInfos) {
                addToQueuedSongs(soundInfo);
            }
        }
        context.getLogger().debug("Found " + soundFileMap.size() + " files");
    } 
}
