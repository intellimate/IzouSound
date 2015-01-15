package jundl77.izou.izousound.outputplugin;

import intellimate.izou.events.Event;
import intellimate.izou.events.EventListener;
import intellimate.izou.output.OutputPlugin;
import intellimate.izou.system.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * The SoundOutputPlugin is capable of playing MP3 and various other sound formats. It can take file paths,
 * or HTTP URLs. Various events can be fired to control the various aspects of the sound playback.
 *
 * @author Julian Brendl
 * @version 1.0
 */
public class SoundOutputPlugin extends OutputPlugin<SoundOutputData> implements EventListener {
    public static final String ID = SoundOutputPlugin.class.getCanonicalName();

    public static final String NEXT_SOUND_EVENT_ID = "IzouSound.NextSound";

    public static final String PREVIOUS_SOUND_EVENT_ID = "IzouSound.PreviousSound";

    public static final String RESTART_SOUND_EVENT_ID = "IzouSound.RestartSound";

    public static final String RESUME_EVENT_ID = "IzouSound.Resume";

    public static final String PAUSE_EVENT_ID = "IzouSound.Pause";

    public static final String STOP_EVENT_ID = "IzouSound.Stop";

    public static final String VOLUME_EVENT_ID = "IzouSound.Volume";

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
        eventList.add(VOLUME_EVENT_ID);
        context.events.registerEventListener(eventList, this);
    }

    @Override
    public void eventFired(Event event) {
         if (event.containsDescriptor(NEXT_SOUND_EVENT_ID)) {
             audioFilePlayer.nextSound();
         } else if (event.containsDescriptor(PREVIOUS_SOUND_EVENT_ID)) {
             audioFilePlayer.previousSound();
         } else if (event.containsDescriptor(RESTART_SOUND_EVENT_ID)) {
             audioFilePlayer.restartSound();
         } else if (event.containsDescriptor(RESUME_EVENT_ID)) {
             audioFilePlayer.resume();
         } else if (event.containsDescriptor(PAUSE_EVENT_ID)) {
             audioFilePlayer.pause();
         } else if (event.containsDescriptor(STOP_EVENT_ID)) {
             audioFilePlayer.stop();
         } else if (event.containsDescriptor(VOLUME_EVENT_ID)) {
            //audioFilePlayer.setVolume(event.getListResourceContainer().;
        }
    }

    @Override
    public void renderFinalOutput() {
        for (SoundOutputData outputData : getTDoneList()) {
            //audioFilePlayer.play(outputData.getPaths());
        }
    }
}
