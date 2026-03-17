import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cannon {

    private final double baseX;
    private final double baseY;
    private final double barrelLength;

    private double angleRad = 0; // pointing right initially

    public Cannon(double baseX, double baseY, double barrelLength) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.barrelLength = barrelLength;
    }

    public void aimAt(double mx, double my) {
        double dx = mx - baseX;
        double dy = my - baseY;
        angleRad = Math.atan2(dy, dx);
    }

    public double getAngleRad() {
        return angleRad;
    }

    public double[] getBarrelTip() {
        double tipX = baseX + Math.cos(angleRad) * barrelLength;
        double tipY = baseY + Math.sin(angleRad) * barrelLength;
        return new double[]{tipX, tipY};
    }

    public void draw(GraphicsContext gc) {
        // Base
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(baseX - 25, baseY - 25, 50, 50);

        // Barrel
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(10);

        double tipX = baseX + Math.cos(angleRad) * barrelLength;
        double tipY = baseY + Math.sin(angleRad) * barrelLength;

        gc.strokeLine(baseX, baseY, tipX, tipY);

        // Small tip marker
        gc.setFill(Color.BLACK);
        gc.fillOval(tipX - 3, tipY - 3, 6, 6);
    }
}