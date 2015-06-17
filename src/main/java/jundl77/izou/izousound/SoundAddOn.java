package jundl77.izou.izousound;


import jundl77.izou.izousound.outputplugin.AudioFilePlayer;
import org.intellimate.izou.sdk.activator.Activator;
import org.intellimate.izou.sdk.addon.AddOn;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;
import org.intellimate.izou.sdk.events.EventsController;
import org.intellimate.izou.sdk.output.OutputExtension;
import org.intellimate.izou.sdk.output.OutputPluginArgument;
import ro.fortsoft.pf4j.Extension;

@Extension
public class SoundAddOn extends AddOn {

    @SuppressWarnings("WeakerAccess")
    public static final String ID = SoundAddOn.class.getCanonicalName();
    private AudioFilePlayer audioFilePlayer;

    public SoundAddOn() {
        super(ID);
    }

    @Override
    public void prepare() {
        audioFilePlayer = new AudioFilePlayer(getContext());
    }

    @Override
    public Activator[] registerActivator() {
        //AudioPlayerController audioPlayerController = new AudioPlayerController(getContext(),audioFilePlayer);
        return null;
    }

    @Override
    public ContentGenerator[] registerContentGenerator() {
        return null;
    }

    @Override
    public EventsController[] registerEventController() {
        return null;
    }

    @Override
    public OutputPluginArgument[] registerOutputPlugin() {
        OutputPluginArgument[] outputPlugins = new OutputPluginArgument[1];
        outputPlugins[0] = audioFilePlayer;
        return outputPlugins;
    }

    @Override
    public OutputExtension[] registerOutputExtension() {
        return null;
    }

    /**
     * An ID must always be unique.
     * A Class like Activator or OutputPlugin can just provide their .class.getCanonicalName()
     * If you have to implement this interface multiple times, just concatenate unique Strings to
     * .class.getCanonicalName()
     *
     * @return A String containing an ID
     */
    @Override
    public String getID() {
        return ID;
    }
}
