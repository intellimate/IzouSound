package jundl77.izou.izousound;

import java.util.List;

/**
 * Created by julianbrendl on 12/10/14.
 */
public class SoundOutputData {
    private List<String> locations;

    public SoundOutputData(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getLocations() {
        return locations;
    }
}
