package jundl77.izou.izousound.outputplugin;

import intellimate.izou.events.Event;
import intellimate.izou.events.EventListener;
import intellimate.izou.output.OutputPlugin;
import intellimate.izou.system.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julianbrendl on 12/10/14.
 */
public class SoundOutputPlugin extends OutputPlugin<SoundOutputData> implements EventListener {
    public static final String ID = SoundOutputPlugin.class.getCanonicalName();

    public static final String NEXT_SOUND_EVENT_ID = "IzouSound.NextSound";

    public static final String PREVIOUS_SOUND_EVENT_ID = "IzouSound.PreviousSound";

    public static final String RESTART_SOUND_EVENT_ID = "IzouSound.RestartSound";

    public static final String RESUME_EVENT_ID = "IzouSound.Resume";

    public static final String PAUSE_EVENT_ID = "IzouSound.Pause";

    public static final String STOP_EVENT_ID = "IzouSound.Stop";

    public AudioFilePlayer audioFilePlayer;

    public SoundOutputPlugin(Context context) {
        super(ID, context);
        audioFilePlayer = new AudioFilePlayer(context);

        List<String> eventList = new ArrayList<>();
        eventList.add(NEXT_SOUND_EVENT_ID);
        eventList.add(PREVIOUS_SOUND_EVENT_ID);
        eventList.add(RESTART_SOUND_EVENT_ID);
        eventList.add(RESUME_EVENT_ID);
        eventList.add(PAUSE_EVENT_ID);
        eventList.add(STOP_EVENT_ID);
        context.events.registerEventListener(eventList, this);
    }

    @Override
    public void eventFired(Event event) {
        switch (event.getID()) {
            case NEXT_SOUND_EVENT_ID:
                //audioFilePlayer.nextSound();
                break;
            case PREVIOUS_SOUND_EVENT_ID:
                //audioFilePlayer.previousSound();
                break;
            case RESTART_SOUND_EVENT_ID:
                //audioFilePlayer.restartSound();
                break;
            case RESUME_EVENT_ID:
                //audioFilePlayer.resume();
                break;
            case PAUSE_EVENT_ID:
                //audioFilePlayer.pause();
                break;
            case STOP_EVENT_ID:
                //audioFilePlayer.stop();
                break;
        }
    }

    @Override
    public void renderFinalOutput() {
        for (SoundOutputData outputData : getTDoneList()) {
            //audioFilePlayer.play(outputData.getPaths());
        }
    }
}
