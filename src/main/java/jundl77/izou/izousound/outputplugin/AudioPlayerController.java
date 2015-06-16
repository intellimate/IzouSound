package jundl77.izou.izousound.outputplugin;

import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.template.Player;
import org.intellimate.izou.sdk.frameworks.music.player.template.PlayerController;

/**
 * Created by julianbrendl on 6/10/15.
 */
public class AudioPlayerController extends PlayerController {


    public AudioPlayerController(Context context, String ID, Player player) {
        super(context, ID, player);
    }

    @Override
    public void activatorStarts() {
        startPlaying();
    }
}
