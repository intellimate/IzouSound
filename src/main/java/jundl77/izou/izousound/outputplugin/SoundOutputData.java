package jundl77.izou.izousound.outputplugin;

import java.util.List;

/**
 * Created by julianbrendl on 12/10/14.
 */
public class SoundOutputData {
    private List<String> paths;

    public SoundOutputData(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getPaths() {
        return paths;
    }
}
