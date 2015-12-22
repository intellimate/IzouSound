package org.intellimate.izou.addon.izousound.outputplugin;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.intellimate.izou.addon.izousound.PlaylistGenerator;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

/**
 * The SoundLoader does two things. First it takes the general SDK music framework objects ({@link Playlist},
 * {@link TrackInfo}) and converts them into internal data structures for the sound engine. This way IzouSound supports
 * the music framework.
 * <p>
 *     Secondly, the sound loader updates soundInfos (and trackInfos) with their meta data (name, artist, etc.) once
 *     it is about to be played and if the meta data is found.
 * </p>
 */
class SoundLoader {
    private Context context;
    private SoundIdentityFactory soundIdentityFactory;

    /**
     * Creates a new SoundLoader object
     *
     * @param context the context of the addOn, mostly used for logging here
     */
    SoundLoader(Context context) {
        this.context = context;
        this.soundIdentityFactory = new SoundIdentityFactory();
    }

    /**
     * Converts a {@link Playlist} object from the music framework to the internal data structure used by the
     * SoundEngine to playback music.
     * <p>
     *     In the process, it is determined if the trackInfo is a URL or a sound file, and if it should be cropped or
     *     not
     * </p>
     *
     * @param playlist the playlist to convert to the internal data structure
     * @return the internal data structure used by the SoundEngine
     */
    HashMap<Integer, SoundIdentity> convertFromPlaylist(Playlist playlist) {
        soundIdentityFactory.startNewSession();

        HashMap<Integer, SoundIdentity> soundFileMap = new HashMap<>();

        for (TrackInfo trackInfo : playlist.getQueue()) {
            Optional<String> dataOpt = trackInfo.getData();

            if (dataOpt.isPresent()) {
                SoundInfo soundInfo = null;
                String[] parts;
                String data = dataOpt.get();

                // See if the track info contains a file or a url, and transform it into a sound info
                parts = data.split(PlaylistGenerator.DATA_SEPERATOR);

                if (parts[0].equals(PlaylistGenerator.FILE_TYPE) && new File(parts[1]).exists()) {
                    soundInfo = new SoundInfo(trackInfo, parts[1]);
                } else if (parts[0].equals(PlaylistGenerator.URL_TYPE)) {
                    try {
                        URL url = URI.create(parts[1]).toURL();
                        soundInfo = new SoundInfo(trackInfo, url);
                    } catch (MalformedURLException e) {
                        context.getLogger().error("Unable to turn given url into a URL object, skipping");
                    }
                }

                if (soundInfo != null) {
                    addSoundInfoToMap(soundFileMap, soundInfo, parts);
                }
            }
        }

        return soundFileMap;
    }

    private void addSoundInfoToMap(HashMap<Integer, SoundIdentity> soundFileMap, SoundInfo soundInfo, String[] parts) {
        int start = -1;
        int duration = -1;
        try {
            if (parts.length >= 3) {
                start = Integer.parseInt(parts[2]);
            }

            if (parts.length >= 4) {
                duration = Integer.parseInt(parts[3]);
            }
        } catch (NumberFormatException e) {
            context.getLogger().error("Start or end value for track info is not an integer, setting length of track to " +
                    "full length");
        }

        soundInfo.setStartTime(start);
        soundInfo.setDurationTime(duration);

        SoundIdentity soundIdentity = soundIdentityFactory.make(soundInfo);
        soundFileMap.put(soundIdentity.getId(), soundIdentity);
    }

    /**
     * Gets the metadata of the soundInfo, that is song name, artist, album etc. if the data is found and then updates
     * the track info (inside the soundInfo) in its playlist as well.
     *
     * @param playlist The playlist that contains the track info (inside the soundInfo)
     * @param soundInfo The soundInfo for which to get its meta data
     */
    Playlist getMetaData(Playlist playlist, SoundInfo soundInfo) {
        soundInfo.setFramesPerSecond(1);

        if (soundInfo.getPath() == null || soundInfo.getHasMetaData()) {
            return playlist;
        }

        try {
            Mp3File mp3file = new Mp3File(soundInfo.getPath());
            long framesPerSecond = mp3file.getFrameCount() / mp3file.getLengthInSeconds();
            long duration = mp3file.getLengthInMilliseconds();

            String name = null;
            String artist = null;
            String album = null;
            String year = null;
            String genre = null;
            if (mp3file.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                name = id3v1Tag.getTitle();
                artist = id3v1Tag.getArtist();
                album = id3v1Tag.getAlbum();
                year = id3v1Tag.getYear();
                genre = id3v1Tag.getGenre() + " " + id3v1Tag.getGenreDescription();
            }

            TrackInfo trackInfo = soundInfo.getTrackInfo();

            // In case the name is not found using the id3v1 tag, use the previously extracted name instead, if it is
            // also not null
            if (trackInfo.getName().isPresent() && trackInfo.getName().get() != null && name == null) {
                name = trackInfo.getName().get();
            }

            final TrackInfo finalTrackInfo = trackInfo;
            trackInfo = new TrackInfo(name, artist, album,
                    trackInfo.getAlbumCover().orElse(null), trackInfo.getAlbumCover()
                            .flatMap(unused -> finalTrackInfo.getAlbumCoverFormat()).orElse(null),
                    trackInfo.getData().orElse(null), year, genre, trackInfo.getBmp().orElse(null), duration);

            Playlist newPlaylist = playlist.update(soundInfo.getTrackInfo(), trackInfo);

            soundInfo.setTrackInfo(trackInfo);
            soundInfo.setFramesPerSecond(framesPerSecond);
            soundInfo.setHasMetaData(true);

            return newPlaylist;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            context.getLogger().error("Error getting meta data for sound file: " + soundInfo.getPath(), e);
        }

        return playlist;
    }
}
