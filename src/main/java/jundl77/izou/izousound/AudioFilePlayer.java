package jundl77.izou.izousound;

public class AudioFilePlayer {
    private SoundObject soundObject;

    public static void main(String[] args) {
        AudioFilePlayer audioFilePlayer = new AudioFilePlayer();

        while (true) {
            audioFilePlayer.play("./src/main/resources/test.wav");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            audioFilePlayer.stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            audioFilePlayer.play("./src/main/resources/selfie.mp3");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            audioFilePlayer.stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param filePath
     */
    public void play(String filePath) {
        SoundObject soundObject = new SoundObject();
        this.soundObject = soundObject;
        Thread thread = new Thread(soundObject);
        soundObject.setFilePath(filePath);
        thread.start();
    }

    /**
     *
     */
    public void stop() {
        soundObject.stopSound();
    }
}