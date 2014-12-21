package jundl77.izou.izousound.outputplugin;

/**
 * Created by julianbrendl on 12/13/14.
 */
class SoundIdentity {
    private int id;
    private String name;

    public SoundIdentity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the id of the sound identity
     *
     * The id is determined by the order of which the sound file was found and added to the list
     *
     * @return the id of the sound identity
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
