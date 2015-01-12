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
        List<String> list = new ArrayList<>();
        list.add("./resources/IzouClock/mama-geb.mp3");
        a.play(list);
    }
}
