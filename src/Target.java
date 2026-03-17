import javafx.scene.paint.Color;

// Target object that can be hit and removed
public class Target extends MovingObject
{
   private boolean alive;
   private Color color;

   public Target(double x, double y, double width, double height,
      double speedY, Color color)
   {
      super(x, y, width, height, speedY);
      this.color = color;
      alive = true;
   }

   // Check if target is still in the game
   public boolean isAlive()
   {
      return alive;
   }

   // Remove target after it gets hit
   public void destroy()
   {
      alive = false;
   }

   // Return target color for drawing
   public Color getColor()
   {
      return color;
   }
}