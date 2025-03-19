import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class PushableObject extends GameObject {
    private double velocityY = 0;
    
    public PushableObject(int x, int y, int size, Color color) {
        super(x, y, size, color);
    }
    
    public double getVelocityY() {
        return velocityY;
    }
    
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
    
    // Updated update method now also checks collisions with other pushable objects.
    public void update(double gravity, int windowSize, ArrayList<GameTile> tiles, ArrayList<PushableObject> pushables) {
        velocityY += gravity;
        int candidateY = y + (int) velocityY;
        
        // Floor collision
        if (candidateY + size > windowSize) {
            candidateY = windowSize - size;
            velocityY = 0;
        }
        
        // Check collision with static tiles
        for (GameTile tile : tiles) {
            if (isColliding(x, candidateY, tile)) {
                if (velocityY > 0) {
                    candidateY = tile.getY() - size;
                } else if (velocityY < 0) {
                    candidateY = tile.getY() + tile.getSize();
                }
                velocityY = 0;
                break;
            }
        }
        
        // Check collision with other pushable objects
        for (PushableObject other : pushables) {
            if (other == this) continue;
            if (isColliding(x, candidateY, other)) {
                if (velocityY > 0) {
                    candidateY = other.getY() - size;
                } else if (velocityY < 0) {
                    candidateY = other.getY() + other.getSize();
                }
                velocityY = 0;
                break;
            }
        }
        
        y = candidateY;
    }
    
    // Helper method for collision checking
    private boolean isColliding(int newX, int newY, GameObject obj) {
        return newX < obj.getX() + obj.getSize() && newX + size > obj.getX() &&
               newY < obj.getY() + obj.getSize() && newY + size > obj.getY();
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
    }
}
