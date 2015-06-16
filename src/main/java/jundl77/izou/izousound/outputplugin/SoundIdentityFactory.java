package jundl77.izou.izousound.outputplugin;

/**
 *  The SoundIdentityFactory creates sound identities, each time incrementing its index in the playlist, unless
 *  startNewSession() is called
 */
class SoundIdentityFactory {
    private int fileIdentityId;

    /**
     * Creates a new {@code SoundIdentityFactory} which handles playback-session order.
     *
     * When a new session is started, it creates new {@code SoundIdentities} and gives them an id based on the order
     * they were found in and then stores them in a HashMap with the id as they key
     */
    public SoundIdentityFactory() {
        fileIdentityId = 0;
    }

    /**
     * Clears the HashMap and resets id counter
     */
    public void startNewSession() {
        fileIdentityId = 0;
    }

    /**
     * Creates and registers and returns a new SoundIdentity with the name of the song
     *
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     * @return The new sound identity
     */
    public SoundIdentity make(SoundInfo soundInfo) {
        SoundIdentity soundIdentity =  new SoundIdentity(fileIdentityId, soundInfo);
        fileIdentityId++;
        return soundIdentity;
    }

    /**
     * Gets the current counter value
     *
     * @return the current counter value
     */
    public int getCurrentCounter() {
        return fileIdentityId;
    }
}
