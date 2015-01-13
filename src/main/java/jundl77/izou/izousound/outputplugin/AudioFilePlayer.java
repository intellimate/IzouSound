package jundl77.izou.izousound.outputplugin;


import intellimate.izou.system.Context;

import java.util.concurrent.ExecutorService;

public class AudioFilePlayer {
    private SoundEngine soundEngine;
    private Context context;
    private ExecutorService executorService;

    public AudioFilePlayer(Context context) {
        this.context = context;
        this.executorService = context.threadPool.getThreadPool();
        soundEngine = new SoundEngine(context);
        executorService.execute(soundEngine);
    }
}