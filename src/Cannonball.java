import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cannonball {

    private double x, y;
    private double vx, vy;

    private boolean active = false;

    // Draw it as a circle, but collisions use bounding box (square)
    private final double radius = 8;

    public void fireFrom(double startX, double startY, double angleRad, double speed) {
        this.x = startX;
        this.y = startY;
        this.vx = Math.cos(angleRad) * speed;
        this.vy = Math.sin(angleRad) * speed;
        this.active = true;
    }

    public void update(double dt) {
        if (!active) return;
        x += vx * dt;
        y += vy * dt;
    }

    public void bounceBack() {
        vx = -vx;
        vy = -vy;
    }

    public boolean isOffScreen(double w, double h) {
        // bounding box check
        return x < -40 || x > w + 40 || y < -40 || y > h + 40;
    }

    public Rectangle2D getBounds() {
        // bounding box around the drawn ball (square)
        double size = radius * 2;
        return new Rectangle2D(x - radius, y - radius, size, size);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public void draw(GraphicsContext gc) {
        if (!active) return;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }
}