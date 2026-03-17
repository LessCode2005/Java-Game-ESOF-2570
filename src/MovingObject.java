// Base class for objects that move up and down
public abstract class MovingObject
{
   protected double x;
   protected double y;
   protected double width;
   protected double height;
   protected double speedY;
   protected int direction;

   public MovingObject(double x, double y, double width, double height, double speedY)
   {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.speedY = speedY;
      direction = 1;
   }

   // Move vertically and reverse at the limits
   public void update(double elapsedSeconds, double topLimit, double bottomLimit)
   {
      y += direction * speedY * elapsedSeconds;

      if (y <= topLimit)
      {
         y = topLimit;
         direction = 1;
      }
      else if (y + height >= bottomLimit)
      {
         y = bottomLimit - height;
         direction = -1;
      }
   }

   public double getX()
   {
      return x;
   }

   public double getY()
   {
      return y;
   }

   public double getWidth()
   {
      return width;
   }

   public double getHeight()
   {
      return height;
   }
}