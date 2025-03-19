import java.awt.Color;
import java.awt.Graphics;

public class GameTile extends GameObject {
    public GameTile(int x, int y, int size, Color color) {
        super(x, y, size, color);
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
    }
}
