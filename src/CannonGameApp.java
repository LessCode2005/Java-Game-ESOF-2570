import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CannonGameApp extends Application {

    public static final int W = 900;
    public static final int H = 520;

    private Canvas canvas;
    private GraphicsContext gc;

    private Cannon cannon;
    private Cannonball ball;
    private Blocker blocker;
    private List<Target> targets;

    private SoundManager sound;

    // Gameplay state
    private double timeRemaining = 10.0;      // starts at 10 seconds
    private int shotsFired = 0;

    private boolean gameOver = false;         // we won’t show Alert yet (you asked to leave end-game UI for later)

    private long lastNs = 0;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(W, H);
        gc = canvas.getGraphicsContext2D();
        
        System.out.println("START() CALLED");

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        stage.setTitle("Cannon Game - Gameplay");
        stage.setScene(scene);
        stage.show();

        sound = new SoundManager();

        initGame();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (gameOver) return;
            // Only allow a new shot if the ball is not currently active
            if (!ball.isActive()) {
                fireToward(e.getX(), e.getY());
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNs == 0) lastNs = now;
                double dt = (now - lastNs) / 1_000_000_000.0;
                lastNs = now;

                if (!gameOver) {
                    update(dt);
                }
                draw();
            }
        };
        timer.start();
    }

    private void initGame() {
        cannon = new Cannon(80, H - 80, 55); // base position near bottom-left
        ball = new Cannonball();

        // Blocker in front of targets
        blocker = new Blocker(W * 0.55, 120, 22, 260, 120); // x, y, w, h, verticalSpeed

        // 9 targets in a 3x3 grid area on the right side
        targets = new ArrayList<>();
        Random rand = new Random();

        double startX = W * 0.72;
        double startY = 90;
        double gapX = 60;
        double gapY = 70;

        int count = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                double x = startX + col * gapX;
                double y = startY + row * gapY;

                // Different vertical speeds
                double speed = 60 + rand.nextInt(120); // 60..179 px/sec
                if (rand.nextBoolean()) speed = -speed;

                targets.add(new Target(x, y, 28, 28, speed));
                count++;
            }
        }
    }

    private void fireToward(double mx, double my) {
        cannon.aimAt(mx, my);

        // Spawn cannonball at barrel tip
        double[] tip = cannon.getBarrelTip();
        ball.fireFrom(tip[0], tip[1], cannon.getAngleRad(), 520); // speed px/sec

        shotsFired++;
        sound.playFire();
    }

    private void update(double dt) {
        // Timer countdown
        timeRemaining -= dt;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            gameOver = true; // End-game UI later
            return;
        }

        // Move blocker and targets
        blocker.update(dt, H);

        for (Target t : targets) {
            if (t.isAlive()) {
                t.update(dt, H);
            }
        }

        // Move ball
        if (ball.isActive()) {
            ball.update(dt);

            // Keep it inside canvas (optional)
            if (ball.isOffScreen(W, H)) {
                ball.deactivate();
            }

            // Collision with blocker
            if (ball.isActive() && ball.getBounds().intersects(blocker.getBounds())) {
                sound.playBlockerHit();

                // Bounce back toward the cannon (simple: reverse velocity)
                ball.bounceBack();

                // -3 seconds penalty
                timeRemaining = Math.max(0, timeRemaining - 3.0);
            }

            // Collision with targets
            if (ball.isActive()) {
                for (Target t : targets) {
                    if (t.isAlive() && ball.getBounds().intersects(t.getBounds())) {
                        t.setAlive(false);
                        sound.playTargetHit();

                        // +3 seconds bonus
                        timeRemaining += 3.0;

                        // Remove ball after hit (simple gameplay)
                        ball.deactivate();
                        break;
                    }
                }
            }
        }

        // Win condition (all targets destroyed) — we’ll keep this logic but not show Alert yet
        boolean allDown = true;
        for (Target t : targets) {
            if (t.isAlive()) {
                allDown = false;
                break;
            }
        }
        if (allDown) {
            gameOver = true; // end-game UI later
        }
    }

    private void draw() {
        // background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, W, H);

        // HUD
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("Time: %.1f", timeRemaining), 15, 20);
        gc.fillText("Shots: " + shotsFired, 15, 40);
        gc.fillText("Click to aim + shoot (new shot only when ball is inactive)", 15, 60);

        // Draw game objects
        blocker.draw(gc);

        for (Target t : targets) {
            t.draw(gc);
        }

        cannon.draw(gc);
        ball.draw(gc);

        // Minimal “game over” text (no Alert yet)
        if (gameOver) {
            gc.setFill(Color.rgb(0, 0, 0, 0.75));
            gc.fillRect(0, 0, W, H);
            gc.setFill(Color.WHITE);

            boolean allDown = targets.stream().noneMatch(Target::isAlive);
            String msg = "GAME OVER";
            gc.fillText(msg, W / 2.0 - 110, H / 2.0);
            gc.fillText("will add Alert + restart at the end.", W / 2.0 - 120, H / 2.0 + 20);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}