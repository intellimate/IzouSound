package jundl77.izou.izousound.outputplugin;

/**
 * A SoundIdentity is a {@link SoundInfo} matched with its position in the playlist. This is done to make jumping back
 * and forth between songs easy.
 */
class SoundIdentity {
    private int id;
    private SoundInfo soundInfo;

    /**
     * Create a new SoundIdentity. A SoundIdentity keeps track of how many songs have been created, and in what order,
     * as to allow jumping around sounds.
     *
     * @param id the track number (or ID)
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     */
    public SoundIdentity(int id, SoundInfo soundInfo) {
        this.id = id;
        this.soundInfo = soundInfo;
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

    /**
     * Gets {@code soundInfo}
     *
     * @return the {@code soundInfo}
     */
    public SoundInfo getSoundInfo() {
        return soundInfo;
    }
}
