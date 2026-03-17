import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class MovingRect {

    protected double x, y, w, h;
    protected double vy;

    public MovingRect(double x, double y, double w, double h, double vy) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.vy = vy;
    }

    public void update(double dt, double canvasH) {
        y += vy * dt;

        // bounce top/bottom
        if (y <= 0) {
            y = 0;
            vy = -vy;
        } else if (y + h >= canvasH) {
            y = canvasH - h;
            vy = -vy;
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, w, h);
    }

    public abstract void draw(GraphicsContext gc);
}