package jundl77.izou.izousound;

/**
 * Created by julianbrendl on 12/13/14.
 */
public class FileIdentity {
    private int id;
    private String name;

    public FileIdentity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
