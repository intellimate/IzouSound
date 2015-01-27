package jundl77.izou.izousound;

import jundl77.izou.izousound.outputplugin.AudioFilePlayer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to debug
 */
public class Debug {
    public static void main(String[] args) {
//        LinkedList<AddOn> addOns = new LinkedList<>();
//        addOns.add(new SoundAddOn());
//        Main main = new Main(addOns);

//        AudioFilePlayer a = new AudioFilePlayer(null);
//        List<String> list = new ArrayList<>();
//        list.add("/Users/julianbrendl/music");
//        a.playFile(list);
        AudioFilePlayer a = new AudioFilePlayer(null);
        List<URL> list = new ArrayList<>();
        try {
            list.add(URI.create("http://ia902508.us.archive.org/5/items/testmp3testfile/mpthreetest.mp3").toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        a.playURL(list);
//
//        a.previousSound();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        a.nextSound();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        a.previousSound();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        a.stop();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        a.previousSound();
    }
}
