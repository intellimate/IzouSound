package jundl77.izou.izousound.outputplugin;

import intellimate.izou.events.Event;
import intellimate.izou.events.EventListener;
import intellimate.izou.output.OutputPlugin;
import intellimate.izou.resource.Resource;
import intellimate.izou.system.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * The SoundOutputPlugin is capable of playing MP3 and various other sound formats. It can take file paths,
 * or HTTP URLs. Various events can be fired to control the various aspects of the sound playback.
 */
public class SoundOutputPlugin extends OutputPlugin<SoundOutputData> implements EventListener {
    public static final String ID = SoundOutputPlugin.class.getCanonicalName();

    public static final String RESOURCE_ID = "izou.sound.general";

    public static final String NEXT_SOUND_EVENT_ID = "IzouSound.NextSound";

    public static final String PREVIOUS_SOUND_EVENT_ID = "IzouSound.PreviousSound";

    public static final String RESTART_SOUND_EVENT_ID = "IzouSound.RestartSound";

    public static final String RESUME_EVENT_ID = "IzouSound.Resume";

    public static final String PAUSE_EVENT_ID = "IzouSound.Pause";

    public static final String STOP_EVENT_ID = "IzouSound.Stop";

    public static final String VOLUME_EVENT_ID = "IzouSound.Volume";

    public AudioFilePlayer audioFilePlayer;

    /**
     * Creates a new SoundOutputPlugin
     *
     * @param context the context of the addOn
     */
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

    private void processEventID(String eventID) {
        switch (eventID) {
            case NEXT_SOUND_EVENT_ID:
                audioFilePlayer.nextSound();
                break;
            case PREVIOUS_SOUND_EVENT_ID:
                audioFilePlayer.previousSound();
                break;
            case RESTART_SOUND_EVENT_ID:
                audioFilePlayer.restartSound();
                break;
            case RESUME_EVENT_ID:
                audioFilePlayer.resume();
                break;
            case PAUSE_EVENT_ID:
                audioFilePlayer.pause();
                break;
            case STOP_EVENT_ID:
                audioFilePlayer.stop();
                break;
        }

        if (eventID.startsWith(VOLUME_EVENT_ID)) {
            String[] parts = eventID.split("=");
            if (parts.length == 2) {
                audioFilePlayer.setVolume(Double.valueOf(parts[1]));
            }
        }
    }

    @Override
    public void eventFired(Event event) {
        List<Resource> resources = event.getListResourceContainer().provideResource(RESOURCE_ID);
        for (Resource r : resources) {
            if (r.getResource() instanceof String) {
                String eventID = (String) r.getResource();
                processEventID(eventID);
            }
        }
    }

    @Override
    public void renderFinalOutput() {
        for (SoundOutputData outputData : pollTDoneList()) {
            if (outputData.getPaths() != null) {
                audioFilePlayer.playFile(outputData.getPaths(), outputData.getStartTime(), outputData.getStopTime());
            } else if (outputData.getURLs() != null) {
                audioFilePlayer.playURL(outputData.getURLs(), outputData.getStartTime(), outputData.getStopTime());
            }

            int startTime = audioFilePlayer.getCurrentSound().getSoundInfo().getStartTime();
            int stopTime = audioFilePlayer.getCurrentSound().getSoundInfo().getStopTime();
            int duration = stopTime - startTime;
            if (duration < 30000) {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
