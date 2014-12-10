package jundl77.izou.izousound;

import intellimate.izou.output.OutputPlugin;
import intellimate.izou.system.Context;

import java.io.File;

/**
 * Created by julianbrendl on 12/10/14.
 */
public class SoundOutputPlugin extends OutputPlugin<SoundOutputData> {
    public static final String ID = SoundOutputPlugin.class.getCanonicalName();

    public  SoundOutputPlugin(Context context) {
        super(ID, context);
    }

    @Override
    public void renderFinalOutput() {
        AudioFilePlayer audioFilePlayer = new AudioFilePlayer();

        for (SoundOutputData outputData : getTDoneList()) {
            for (String loc : outputData.getLocations()) {
                if (!new File(loc).exists()) {
                    getContext().logger.getLogger().warn("Path " + loc + "does not exist");
                    continue;
                }

                if (new File(loc).isDirectory()) {
                    File[] files = new File(loc).listFiles();
                    if (files == null) continue;
                    for(File file: files) {
                        handleFile(audioFilePlayer, file.getAbsolutePath());
                    }
                }

                handleFile(audioFilePlayer, loc);
            }
        }
    }

    private void handleFile(AudioFilePlayer audioFilePlayer, String loc) {
        if (new File(loc).isFile() && (loc.endsWith(".mp3") || loc.endsWith(".wav"))) {
            audioFilePlayer.play(loc);
        }
    }
}
