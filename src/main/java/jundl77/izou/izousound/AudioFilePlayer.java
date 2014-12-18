package jundl77.izou.izousound;

import intellimate.izou.system.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioFilePlayer {
    private ExecutorService executorService;
    private SoundObject soundObject;
    private Context context;

    public static void main(String[] args) {
        AudioFilePlayer audioFilePlayer = new AudioFilePlayer(null);
        List<String> paths = new ArrayList<>();
        paths.add("./src/main/resources/selfie.mp3");
        audioFilePlayer.play(paths);
        while (true) {
            audioFilePlayer.resume();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            audioFilePlayer.pause();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //audioFilePlayer.play("./src/main/resources/selfie.mp3");
            audioFilePlayer.resume();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            audioFilePlayer.pause();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public AudioFilePlayer(Context context) {
        this.context = context;
        this.soundObject = new SoundObject(context);
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(soundObject);
    }

    public void play(List<String> filePath) {
        if (!soundObject.isActive()) {
            soundObject.addSoundFiles(filePath);
        }
    }

    /**
     *
     */
    public void resume() {
        if (soundObject.isPaused()) {
            soundObject.resumeSound();
        }
    }

    /**
     *
     */
    public void stop() {
        soundObject.stopSound();
    }

    public void pause() {
        soundObject.pauseSound();
    }

    public void setVolume() {}
}