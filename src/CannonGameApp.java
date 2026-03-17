import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CannonGameApp extends Application
{
   // Window size
   private static final double WIDTH = 900;
   private static final double HEIGHT = 600;

   // Top and bottom movement limits
   private static final double TOP_LIMIT = 50;
   private static final double BOTTOM_LIMIT = HEIGHT - 30;

   // Cannon position and size
   private static final double CANNON_X = 70;
   private static final double CANNON_Y = HEIGHT / 2.0;
   private static final double BARREL_LENGTH = 65;
   private static final double BARREL_WIDTH = 16;

   // Cannonball values
   private static final double CANNONBALL_SIZE = 16;
   private static final double CANNONBALL_SPEED = 420.0;

   // Timer values
   private static final double START_TIME = 10.0;
   private static final double BONUS_TIME = 3.0;
   private static final double PENALTY_TIME = 3.0;

   private Canvas canvas;
   private GraphicsContext gc;

   // Current cannon angle
   private double barrelAngle = 0.0;

   private CannonBall cannonBall;
   private Blocker blocker;
   private Target[] targets;

   private AnimationTimer timer;

   private long previousTime = 0;
   private long gameStartTime = 0;

   private double timeRemaining = START_TIME;
   private int shotsFired = 0;
   private boolean gameOver = false;
   private boolean playerWon = false;

   // Sound effects
   private AudioClip fireSound;
   private AudioClip targetHitSound;
   private AudioClip blockerHitSound;

   @Override
   public void start(Stage stage)
   {
      canvas = new Canvas(WIDTH, HEIGHT);
      gc = canvas.getGraphicsContext2D();

      Pane root = new Pane(canvas);
      Scene scene = new Scene(root, WIDTH, HEIGHT);

      loadSounds();
      createObjects();
      registerMouseHandler();

      stage.setTitle("Cannon Game App");
      stage.setScene(scene);
      stage.show();

      startGameLoop();
   }

   // Load all game sounds
   private void loadSounds()
   {
      fireSound = loadClip("cannon_fire.wav");
      targetHitSound = loadClip("target_hit.wav");
      blockerHitSound = loadClip("blocker_hit.wav");
   }

   // Load one sound file
   private AudioClip loadClip(String fileName)
   {
      try
      {
         File file = new File(fileName);

         if (file.exists())
         {
            return new AudioClip(file.toURI().toString());
         }
      }
      catch (Exception exception)
      {
         System.out.println("Could not load sound: " + fileName);
      }

      return null;
   }

   // Play a sound only if it was loaded properly
   private void playSound(AudioClip clip)
   {
      if (clip != null)
      {
         clip.play();
      }
   }

   // Create the cannonball, blocker, and targets
   private void createObjects()
   {
      cannonBall = new CannonBall(CANNONBALL_SIZE);

      blocker = new Blocker(360, 210, 22, 150, 180);

      targets = new Target[9];

      targets[0] = new Target(610, 70, 22, 55, 130, Color.CRIMSON);
      targets[1] = new Target(660, 130, 22, 55, 170, Color.DARKORANGE);
      targets[2] = new Target(710, 90, 22, 55, 150, Color.GOLD);
      targets[3] = new Target(760, 150, 22, 55, 190, Color.FORESTGREEN);
      targets[4] = new Target(810, 110, 22, 55, 160, Color.DODGERBLUE);

      targets[5] = new Target(610, 340, 22, 55, 145, Color.MEDIUMPURPLE);
      targets[6] = new Target(670, 390, 22, 55, 200, Color.HOTPINK);
      targets[7] = new Target(740, 330, 22, 55, 155, Color.SIENNA);
      targets[8] = new Target(810, 380, 22, 55, 175, Color.TEAL);
   }

   // Fire when the user clicks the mouse
   private void registerMouseHandler()
   {
      canvas.setOnMouseClicked(event ->
      {
         if (gameOver)
         {
            return;
         }

         aimCannon(event.getX(), event.getY());
         fireCannonBall();
      });
   }

   // Rotate cannon toward mouse click
   private void aimCannon(double mouseX, double mouseY)
   {
      double dx = mouseX - CANNON_X;
      double dy = mouseY - CANNON_Y;
      barrelAngle = Math.atan2(dy, dx);
   }

   // Fire a new cannonball if one is not already active
   private void fireCannonBall()
   {
      if (cannonBall.isActive())
      {
         return;
      }

      Point2D barrelTip = getBarrelTip();

      double startX = barrelTip.getX() - cannonBall.getSize() / 2.0;
      double startY = barrelTip.getY() - cannonBall.getSize() / 2.0;

      double velocityX = CANNONBALL_SPEED * Math.cos(barrelAngle);
      double velocityY = CANNONBALL_SPEED * Math.sin(barrelAngle);

      cannonBall.fire(startX, startY, velocityX, velocityY);

      shotsFired++;
      playSound(fireSound);
   }

   // Find the end of the cannon barrel
   private Point2D getBarrelTip()
   {
      double tipX = CANNON_X + BARREL_LENGTH * Math.cos(barrelAngle);
      double tipY = CANNON_Y + BARREL_LENGTH * Math.sin(barrelAngle);

      return new Point2D(tipX, tipY);
   }

   // Start the animation loop
   private void startGameLoop()
   {
      gameStartTime = System.nanoTime();

      timer = new AnimationTimer()
      {
         @Override
         public void handle(long now)
         {
            if (previousTime == 0)
            {
               previousTime = now;
               draw();
               return;
            }

            double elapsedSeconds = (now - previousTime) / 1_000_000_000.0;
            previousTime = now;

            update(elapsedSeconds);
            draw();
         }
      };

      timer.start();
   }

   // Update the whole game
   private void update(double elapsedSeconds)
   {
      if (gameOver)
      {
         return;
      }

      // Decrease time every frame
      timeRemaining -= elapsedSeconds;

      // Move blocker
      blocker.update(elapsedSeconds, TOP_LIMIT, BOTTOM_LIMIT);

      // Move all targets that are still alive
      for (Target target : targets)
      {
         if (target.isAlive())
         {
            target.update(elapsedSeconds, TOP_LIMIT, BOTTOM_LIMIT);
         }
      }

      updateCannonBall(elapsedSeconds);

      // Check win or lose
      if (allTargetsDestroyed())
      {
         playerWon = true;
         endGame();
      }
      else if (timeRemaining <= 0)
      {
         timeRemaining = 0;
         playerWon = false;
         endGame();
      }
   }

   // Update cannonball movement and collisions
   private void updateCannonBall(double elapsedSeconds)
   {
      if (!cannonBall.isActive())
      {
         return;
      }

      cannonBall.update(elapsedSeconds);

      // Remove ball if it leaves the screen
      if (cannonBall.isOutsideWindow(WIDTH, HEIGHT))
      {
         cannonBall.deactivate();
         return;
      }

      // Reduce bounce cooldown slowly
      if (cannonBall.getBounceCooldown() > 0)
      {
         cannonBall.decreaseBounceCooldown();
      }

      // Check collision with blocker
      if (cannonBall.getBounceCooldown() == 0 &&
         intersects(cannonBall, blocker))
      {
         cannonBall.bounceBack();
         cannonBall.setBounceCooldown(12);

         timeRemaining -= PENALTY_TIME;
         if (timeRemaining < 0)
         {
            timeRemaining = 0;
         }

         playSound(blockerHitSound);
         return;
      }

      // Check collision with targets
      for (Target target : targets)
      {
         if (target.isAlive() && intersects(cannonBall, target))
         {
            target.destroy();
            cannonBall.deactivate();
            timeRemaining += BONUS_TIME;

            playSound(targetHitSound);
            break;
         }
      }
   }

   // Simple rectangle collision check
   private boolean intersects(CannonBall ball, MovingObject object)
   {
      double ballLeft = ball.getX();
      double ballRight = ball.getX() + ball.getSize();
      double ballTop = ball.getY();
      double ballBottom = ball.getY() + ball.getSize();

      double objectLeft = object.getX();
      double objectRight = object.getX() + object.getWidth();
      double objectTop = object.getY();
      double objectBottom = object.getY() + object.getHeight();

      return ballRight >= objectLeft &&
         ballLeft <= objectRight &&
         ballBottom >= objectTop &&
         ballTop <= objectBottom;
   }

   // Check if all targets are gone
   private boolean allTargetsDestroyed()
   {
      for (Target target : targets)
      {
         if (target.isAlive())
         {
            return false;
         }
      }

      return true;
   }

   // Stop game and show result
   private void endGame()
   {
      gameOver = true;
      timer.stop();

      double elapsedGameTime =
         (System.nanoTime() - gameStartTime) / 1_000_000_000.0;

      Platform.runLater(() ->
      {
         Alert alert = new Alert(AlertType.INFORMATION);
         alert.setTitle("Game Over");

         if (playerWon)
         {
            alert.setHeaderText("You won!");
         }
         else
         {
            alert.setHeaderText("You lost!");
         }

         String message = String.format(
            "Shots fired: %d%nElapsed time: %.1f seconds",
            shotsFired,
            elapsedGameTime);

         alert.setContentText(message);
         alert.show();
      });
   }

   // Draw everything
   private void draw()
   {
      drawBackground();
      drawTopText();
      drawGuideLine();
      drawCannon();
      drawBlocker();
      drawTargets();
      drawCannonBall();
   }

   // Draw white background and movement lines
   private void drawBackground()
   {
      gc.setFill(Color.WHITE);
      gc.fillRect(0, 0, WIDTH, HEIGHT);

      gc.setStroke(Color.LIGHTGRAY);
      gc.strokeLine(0, TOP_LIMIT, WIDTH, TOP_LIMIT);
      gc.strokeLine(0, BOTTOM_LIMIT, WIDTH, BOTTOM_LIMIT);
   }

   // Draw timer and number of shots
   private void drawTopText()
   {
      gc.setFill(Color.BLACK);
      gc.setFont(Font.font(18));
      gc.fillText(
         String.format("Time remaining: %.1f seconds", timeRemaining),
         20,
         30);
      gc.fillText("Shots: " + shotsFired, 280, 30);
   }

   // Draw aiming guide line
   private void drawGuideLine()
   {
      Point2D barrelTip = getBarrelTip();

      gc.setStroke(Color.LIGHTGRAY);
      gc.setLineDashes(7);

      double lineEndX = barrelTip.getX() + 280 * Math.cos(barrelAngle);
      double lineEndY = barrelTip.getY() + 280 * Math.sin(barrelAngle);

      gc.strokeLine(CANNON_X, CANNON_Y, lineEndX, lineEndY);
      gc.setLineDashes(0);
   }

   // Draw cannon base and barrel
   private void drawCannon()
   {
      gc.save();
      gc.translate(CANNON_X, CANNON_Y);
      gc.rotate(Math.toDegrees(barrelAngle));

      gc.setFill(Color.BLACK);
      gc.fillRect(0, -BARREL_WIDTH / 2.0, BARREL_LENGTH, BARREL_WIDTH);

      gc.restore();

      gc.setFill(Color.BLACK);
      gc.fillOval(CANNON_X - 28, CANNON_Y - 28, 56, 56);
   }

   // Draw blocker
   private void drawBlocker()
   {
      gc.setFill(Color.BLACK);
      gc.fillRect(
         blocker.getX(),
         blocker.getY(),
         blocker.getWidth(),
         blocker.getHeight());
   }

   // Draw all targets that are still alive
   private void drawTargets()
   {
      for (Target target : targets)
      {
         if (target.isAlive())
         {
            gc.setFill(target.getColor());
            gc.fillRect(
               target.getX(),
               target.getY(),
               target.getWidth(),
               target.getHeight());
         }
      }
   }

   // Draw active cannonball
   private void drawCannonBall()
   {
      if (cannonBall.isActive())
      {
         gc.setFill(Color.BLACK);
         gc.fillOval(
            cannonBall.getX(),
            cannonBall.getY(),
            cannonBall.getSize(),
            cannonBall.getSize());
      }
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}