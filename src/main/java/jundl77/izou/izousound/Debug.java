package jundl77.izou.izousound;

import intellimate.izou.addon.AddOn;
import intellimate.izou.main.Main;

import java.util.LinkedList;

/**
 * Use this class to debug
 */
public class Debug {
    public static void main(String[] args) {
        LinkedList<AddOn> addOns = new LinkedList<>();
        addOns.add(new SoundAddOn());
        Main main = new Main(addOns);

//        AudioFilePlayer a = new AudioFilePlayer(null);
//        List<String> list = new ArrayList<>();
//        list.add("/Users/julianbrendl/music");
//        a.play(list);
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
