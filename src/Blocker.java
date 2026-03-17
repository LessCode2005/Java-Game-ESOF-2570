import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Blocker extends MovingRect {

    public Blocker(double x, double y, double w, double h, double vy) {
        super(x, y, w, h, vy);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.DARKBLUE);
        gc.fillRect(x, y, w, h);
    }
}