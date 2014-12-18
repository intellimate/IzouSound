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
        AudioFilePlayer audioFilePlayer = new AudioFilePlayer(null);

        for (SoundOutputData outputData : getTDoneList()) {
            audioFilePlayer.play(outputData.getPaths());
        }
    }

    private void handleFile(AudioFilePlayer audioFilePlayer, String loc) {
        if (new File(loc).isFile() && (loc.endsWith(".mp3") || loc.endsWith(".wav"))) {
            //audioFilePlayer.initPlayer(loc);
        }
    }
}
