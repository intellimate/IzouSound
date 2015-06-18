package jundl77.izou.izousound;

import jundl77.izou.izousound.outputplugin.AudioFilePlayer;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to debug
 */
public class Debug {
    public static void main(String[] args) {
        AudioFilePlayer a = new AudioFilePlayer(null);
        List<String> list = new ArrayList<>();
        list.add("/Users/julianbrendl/music");
        //list.add(URI.create("http://ia902508.us.archive.org/5/items/testmp3testfile/mpthreetest.mp3").toURL());
        PlaylistGenerator playlistGenerator = new PlaylistGenerator(null);
        Playlist playlist = playlistGenerator.createRecursiveSearchFilePlaylist(list);
        a.setShuffle(true);
        a.playPlaylist(playlist);
        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a.nextSound();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a.nextSound();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a.previousSound();
            //a.setRepeatPlaylist(!a.getRepeatSong());
        }
        a.previousSound();
        a.nextSound();
        a.pause();
        a.resume();
    }
}
