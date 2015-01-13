package jundl77.izou.izousound;

import jundl77.izou.izousound.outputplugin.AudioFilePlayer;

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

        AudioFilePlayer a = new AudioFilePlayer(null);
        Thread t = new Thread(a);
        List<String> list = new ArrayList<>();
        list.add("/Users/julianbrendl/music");
        a.addSoundFiles(list);
        t.start();

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a.previousFile();
        }
    }
}
