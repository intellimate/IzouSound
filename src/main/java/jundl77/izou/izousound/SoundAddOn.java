package jundl77.izou.izousound;

import intellimate.izou.activator.Activator;
import intellimate.izou.addon.AddOn;
import intellimate.izou.contentgenerator.ContentGenerator;
import intellimate.izou.events.EventsController;
import intellimate.izou.output.OutputExtension;
import intellimate.izou.output.OutputPlugin;
import jundl77.izou.izousound.outputplugin.SoundOutputPlugin;

/**
 * Created by julianbrendl on 12/21/14.
 */
public class SoundAddOn extends AddOn {
    public static final String ID = SoundAddOn.class.getCanonicalName();

    public SoundAddOn() {
        super(ID);
    }

    @Override
    public void prepare() {

    }

    @Override
    public Activator[] registerActivator() {
        return new Activator[0];
    }

    @Override
    public ContentGenerator[] registerContentGenerator() {
        return new ContentGenerator[0];
    }

    @Override
    public EventsController[] registerEventController() {
        return new EventsController[0];
    }

    @Override
    public OutputPlugin[] registerOutputPlugin() {
        OutputPlugin[] outputPlugins = new OutputPlugin[1];
        outputPlugins[0] = new SoundOutputPlugin(getContext());
        return outputPlugins;
    }

    @Override
    public OutputExtension[] registerOutputExtension() {
        return new OutputExtension[0];
    }

    @Override
    public String getID() {
        return ID;
    }
}
