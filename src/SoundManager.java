import javafx.scene.media.AudioClip;

public class SoundManager {

    private final AudioClip fire;
    private final AudioClip targetHit;
    private final AudioClip blockerHit;

    public SoundManager() {
        fire = load("/sounds/cannon_fire.wav");
        targetHit = load("/sounds/target_hit.wav");
        blockerHit = load("/sounds/blocker_hit.wav");
    }

    private AudioClip load(String path) {
        try {
            return new AudioClip(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            // If sounds fail to load, the game still runs (helps debugging).
            System.out.println("Sound load failed: " + path + " (" + e.getMessage() + ")");
            return null;
        }
    }

    public void playFire() {
        if (fire != null) fire.play();
    }

    public void playTargetHit() {
        if (targetHit != null) targetHit.play();
    }

    public void playBlockerHit() {
        if (blockerHit != null) blockerHit.play();
    }
}