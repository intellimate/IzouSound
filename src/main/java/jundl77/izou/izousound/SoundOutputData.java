package jundl77.izou.izousound;

import java.util.List;

/**
 * Created by julianbrendl on 12/10/14.
 */
public class SoundOutputData {
    private List<String> paths;

    public SoundOutputData(List<String> locations) {
        this.paths = locations;
    }

    public List<String> getPaths() {
        return paths;
    }
}
