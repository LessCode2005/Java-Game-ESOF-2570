import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Target extends MovingRect {

    private boolean alive = true;

    public Target(double x, double y, double w, double h, double vy) {
        super(x, y, w, h, vy);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!alive) return;
        gc.setFill(Color.FIREBRICK);
        gc.fillRect(x, y, w, h);
    }
}