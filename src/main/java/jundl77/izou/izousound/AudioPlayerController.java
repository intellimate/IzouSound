package jundl77.izou.izousound;

import jundl77.izou.izousound.outputplugin.AudioFilePlayer;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;
import org.intellimate.izou.sdk.frameworks.music.player.template.PlayerController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The AudioPlayerController should be used to start a new playing session. If it is not used, the sound playback will
 * be denied. Call {@link #playPlaylist(Playlist)} or {@link #playTrackInfo(TrackInfo)} to play. Playlists and
 * track infos have to be created by the {@link PlaylistGenerator} and by the {@link TrackInfoGenerator}, otherwise
 * the playlists and track infos will have the wrong format.
 */
public class AudioPlayerController extends PlayerController {
    public static final String ID = AudioPlayerController.class.getCanonicalName();
    private BlockingQueue<Object> requestBlockingQueue;
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
        requestBlockingQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Do not call this method to play sound, nothing will happen. Call {@link #playPlaylist(Playlist)} or
     * {@link #playTrackInfo(TrackInfo)} instead.
     */
    @Override
    public void activatorStarts() {
        try {
            requestBlockingQueue.take();
        } catch (InterruptedException e) {
            error("IzouSound was interrupted.", e);
        }

        if (playlist != null) {
            startPlaying(playlist);
        } else if (trackInfo != null) {
            startPlaying(trackInfo);
        }
    }

    /**
     * Call this method to start playing a playlist
     *
     * @param playlist the playlist to play, it should have been created by the {@link PlaylistGenerator}
     */
    public void playPlaylist(Playlist playlist) {
        AudioPlayerController.playlist = playlist;
        trackInfo = null;

        // Signal the activator that a new request has arrived (wake up the thread)
        requestBlockingQueue.add(new Object());
    }

    /**
     * Call this method to start playing a track info
     *
     * @param trackInfo the track info to play, it should have been created by the {@link TrackInfoGenerator}
     */
    public void playTrackInfo(TrackInfo trackInfo) {
        playlist = null;
        AudioPlayerController.trackInfo = trackInfo;

        // Signal the activator that a new request has arrived (wake up the thread)
        requestBlockingQueue.add(new Object());
    }
}
