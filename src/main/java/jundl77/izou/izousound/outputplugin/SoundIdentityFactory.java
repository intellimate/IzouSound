package jundl77.izou.izousound.outputplugin;

import java.util.HashMap;

/**
 * {@code SoundIdentityFactory} handles sound file registration for each playback session
 */
class SoundIdentityFactory {
    private int fileIdentityId;
    private HashMap<Integer, SoundIdentity> soundIdentities;

    /**
     * Creates a new {@code SoundIdentityFactory} which handles playback-session order.
     *
     * When a new session is started, it creates new {@code SoundIdentities} and gives them an id based on the order
     * they were found in and then stores them in a HashMap with the id as they key
     */
    public SoundIdentityFactory() {
        fileIdentityId = 0;
        soundIdentities = new HashMap<>();
    }

    /**
     * Clears the HashMap and resets id counter
     */
    public void startNewSession() {
        fileIdentityId = 0;
        soundIdentities.clear();
    }

    /**
     * Creates and registers and returns a new SoundIdentity with the name of the song
     *
     * @param soundInfo The {@link SoundInfo} object for the song for which the sound identity was created
     * @return The new sound identity
     */
    public SoundIdentity make(SoundInfo soundInfo) {
        SoundIdentity soundIdentity =  new SoundIdentity(fileIdentityId, soundInfo);
        soundIdentities.put(fileIdentityId, soundIdentity);
        fileIdentityId++;
        return soundIdentity;
    }

    /**
     * Gets the sound identity in position with key {@code i} of the HashMap
     *
     * @param i The index/key of the sound identity to get
     * @return The sound identity with key {@code i}
     */
    public SoundIdentity getSoundIdentity(int i) {
        if (soundIdentities.containsKey(i)) {
            return soundIdentities.get(i);
        } else {
            throw new NullPointerException("Index is out of bounds");
        }
    }

    /**
     * Gets number of registered songs
     *
     * @return The number of registered songs
     */
    public int getNumberOfSounds() {
        return soundIdentities.size();
    }
}
