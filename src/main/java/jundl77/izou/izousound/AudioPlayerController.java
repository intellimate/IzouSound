package jundl77.izou.izousound;

import jundl77.izou.izousound.outputplugin.AudioFilePlayer;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;
import org.intellimate.izou.sdk.frameworks.music.player.template.PlayerController;

/**
 * The AudioPlayerController should be used to start a new playing session. If it is not used, the sound playback will
 * be denied. Call {@link #playPlaylist(Playlist)} or {@link #playTrackInfo(TrackInfo)} to play. Playlists and
 * track infos have to be created by the {@link PlaylistGenerator} and by the {@link TrackInfoGenerator}, otherwise
 * the playlists and track infos will have the wrong format.
 */
public class AudioPlayerController extends PlayerController {
    public static final String ID = AudioPlayerController.class.getCanonicalName();
    private static final Object lock = new Object();
    private static boolean play = false;
    private static Playlist playlist;
    private static TrackInfo trackInfo;

    /**
     * Creates a new AudioPlayerController. It can be used to play sound from IzouSound
     *
     * @param context the context of the addOn, mostly used for logging
     * @param player the {@link AudioFilePlayer} used to play music
     */
    public AudioPlayerController(Context context, AudioFilePlayer player) {
        super(context, ID, player);
        playlist = null;
        trackInfo = null;
    }

    /**
     * Do not call this method to play sound, nothing will happen. Call {@link #playPlaylist(Playlist)} or
     * {@link #playTrackInfo(TrackInfo)} instead.
     */
    @Override
    public void activatorStarts() {
        synchronized (lock) {
            while (!play) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    error("IzouSound music thread interrupted", e);
                }
            }

            if (playlist != null) {
                startPlaying(playlist);
            } else if (trackInfo != null) {
                startPlaying(trackInfo);
            }

            playlist = null;
            trackInfo = null;

            play = false;
        }
    }

    /**
     * Call this method to start playing a playlist
     *
     * @param playlist the playlist to play, it should have been created by the {@link PlaylistGenerator}
     */
    public static void playPlaylist(Playlist playlist) {
        synchronized (lock) {
            AudioPlayerController.playlist = playlist;
            trackInfo = null;

            play = true;
            lock.notifyAll();
        }
    }

    /**
     * Call this method to start playing a track info
     *
     * @param trackInfo the track info to play, it should have been created by the {@link TrackInfoGenerator}
     */
    public static void playTrackInfo(TrackInfo trackInfo) {
        synchronized (lock) {
            playlist = null;
            AudioPlayerController.trackInfo = trackInfo;

            play = true;
            lock.notifyAll();
        }
    }
}
