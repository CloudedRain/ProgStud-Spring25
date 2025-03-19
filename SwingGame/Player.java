import java.awt.Color;

public class Player extends GameObject {
    private int speed;
    private double velocityY = 0; // New field for vertical velocity
    
    public Player(int x, int y, int size, Color color, int speed) {
        super(x, y, size, color);
        this.speed = speed;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public double getVelocityY() {
        return velocityY;
    }
    
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
}
