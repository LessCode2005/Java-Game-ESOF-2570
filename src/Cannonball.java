// Cannonball object fired by the cannon
public class CannonBall
{
   private double x;
   private double y;
   private double velocityX;
   private double velocityY;
   private double size;
   private boolean active;
   private int bounceCooldown;

   public CannonBall(double size)
   {
      this.size = size;
      active = false;
      bounceCooldown = 0;
   }

   // Start a new cannonball shot
   public void fire(double startX, double startY, double velocityX, double velocityY)
   {
      x = startX;
      y = startY;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      active = true;
      bounceCooldown = 0;
   }

   // Move the cannonball
   public void update(double elapsedSeconds)
   {
      x += velocityX * elapsedSeconds;
      y += velocityY * elapsedSeconds;
   }

   // Reverse the cannonball direction
   public void bounceBack()
   {
      velocityX = -velocityX;
      velocityY = -velocityY;
   }

   // Check if ball went off screen
   public boolean isOutsideWindow(double width, double height)
   {
      return x + size < 0 || x > width || y + size < 0 || y > height;
   }

   // Turn off the cannonball
   public void deactivate()
   {
      active = false;
   }

   public double getX()
   {
      return x;
   }

   public double getY()
   {
      return y;
   }

   public double getSize()
   {
      return size;
   }

   public boolean isActive()
   {
      return active;
   }

   public int getBounceCooldown()
   {
      return bounceCooldown;
   }

   public void setBounceCooldown(int bounceCooldown)
   {
      this.bounceCooldown = bounceCooldown;
   }

   // Lower cooldown so the ball does not bounce repeatedly right away
   public void decreaseBounceCooldown()
   {
      if (bounceCooldown > 0)
      {
         bounceCooldown--;
      }
   }
}