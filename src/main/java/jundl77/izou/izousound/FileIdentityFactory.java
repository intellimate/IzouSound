package jundl77.izou.izousound;

/**
 * Created by julianbrendl on 12/13/14.
 */
public class FileIdentityFactory {
    private int fileIdentityId;

    public FileIdentityFactory() {
        fileIdentityId = 0;
    }

    public void startNewSession() {
        fileIdentityId = 0;
    }

    public FileIdentity make(String name) {
        FileIdentity fileIdentity =  new FileIdentity(fileIdentityId, name);
        fileIdentityId++;
        return fileIdentity;
    }
}
