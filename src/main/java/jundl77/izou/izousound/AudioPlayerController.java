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
    private Playlist playlist;
    private TrackInfo trackInfo;

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
        if (playlist != null) {
            startPlaying(playlist);
        } else if (trackInfo != null) {
            startPlaying(trackInfo);
        }

        playlist = null;
        trackInfo = null;
    }

    /**
     * Call this method to start playing a playlist
     *
     * @param playlist the playlist to play, it should have been created by the {@link PlaylistGenerator}
     */
    public void playPlaylist(Playlist playlist) {
        this.playlist = playlist;
        this.trackInfo = null;
        activatorStarts();
    }

    /**
     * Call this method to start playing a track info
     *
     * @param trackInfo the track info to play, it should have been created by the {@link TrackInfoGenerator}
     */
    public void playTrackInfo(TrackInfo trackInfo) {
        this.playlist = null;
        this.trackInfo = trackInfo;
        activatorStarts();
    }
}
