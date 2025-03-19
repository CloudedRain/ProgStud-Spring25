import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SwingGame extends JPanel {
    private final int WINDOW_SIZE = 1000;
    private final Set<Integer> pressedKeys = new HashSet<>();

    // Lists for movable objects and static tiles
    private final ArrayList<PushableObject> pushableObjects = new ArrayList<>();
    private final ArrayList<GameTile> tiles = new ArrayList<>();

    private Player player;

    // Platformer physics constants
    private final double GRAVITY = 0.5;
    private final double FAST_FALL_MULTIPLIER = 2.0;
    private final double JUMP_VELOCITY = -10;
    private final int HORIZONTAL_SPEED = 5;

    // Grappling hook related fields
    private GrapplingHook hook = null;
    private Point mousePosition = new Point(0, 0);

    public SwingGame() {
        setFocusable(true);
        setBackground(Color.BLACK);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        // Update the mouse position for aiming the hook
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });

        // Create the player
        player = new Player(100, 100, 20, Color.WHITE, HORIZONTAL_SPEED);

        // Create pushable objects (affected by gravity)
        pushableObjects.add(new PushableObject(300, 300, 30, Color.RED));
        pushableObjects.add(new PushableObject(400, 250, 40, Color.BLUE));

        // Create static game tiles (platforms)
        tiles.add(new GameTile(200, 500, 200, Color.GRAY));
        tiles.add(new GameTile(600, 700, 300, Color.GRAY));

        // Game loop using a Swing Timer
        Timer timer = new Timer(16, e -> updatePosition());
        timer.start();
    }

    // --- Grappling Hook Implementation ---
    private class GrapplingHook {
        double startX, startY;  
        double tipX, tipY;      
        double tipVelX, tipVelY;
        double targetX, targetY;  
        boolean attached = false;
        final double hookSpeed = 15.0;
        double dirX, dirY;  
        double pullDx = 0;
        double pullDy = 0;

        public GrapplingHook(double startX, double startY, double mouseX, double mouseY) {
            this.startX = startX;
            this.startY = startY;
            this.tipX = startX;
            this.tipY = startY;

            double diffX = mouseX - startX;
            double diffY = mouseY - startY;
            double length = Math.sqrt(diffX * diffX + diffY * diffY);
            
            if (length == 0) {
                dirX = 0; 
                dirY = 0;
                tipVelX = 0;
                tipVelY = 0;
            } else {
                dirX = diffX / length;
                dirY = diffY / length;
                tipVelX = dirX * hookSpeed;
                tipVelY = dirY * hookSpeed;
            }
        }

        public void update() {
            if (!attached) {
                tipX += tipVelX;
                tipY += tipVelY;
                tipVelY += GRAVITY;  // Gravity affects the hook slightly

                if (tipX < 0 || tipX > WINDOW_SIZE || tipY < 0 || tipY > WINDOW_SIZE) {
                    hook = null;
                    return;
                }

                // Check collision with tiles and pushable objects
                for (GameTile tile : tiles) {
                    Point collisionPoint = getFirstCollisionPoint(tipX, tipY, tipX + tipVelX, tipY + tipVelY, tile);
                    if (collisionPoint != null) {
                        tipX = collisionPoint.x;
                        tipY = collisionPoint.y;
                        attached = true;
                        targetX = tipX;
                        targetY = tipY;
                        return;
                    }
                }

                for (PushableObject obj : pushableObjects) {
                    Point collisionPoint = getFirstCollisionPoint(tipX, tipY, tipX + tipVelX, tipY + tipVelY, obj);
                    if (collisionPoint != null) {
                        tipX = collisionPoint.x;
                        tipY = collisionPoint.y;
                        attached = true;
                        targetX = tipX;
                        targetY = tipY;
                        return;
                    }
                }
            }
        }
    }
    // --- End Grappling Hook Implementation ---

    // Helper method: basic AABB collision
    private boolean isColliding(int x, int y, int size, GameObject obj) {
        return x < obj.getX() + obj.getSize() && x + size > obj.getX() &&
               y < obj.getY() + obj.getSize() && y + size > obj.getY();
    }

    // When the player hits a pushable object horizontally, try to push it.
    private boolean pushObjectHorizontally(PushableObject obj, int dx) {
        int newObjX = obj.getX() + dx;
        if (newObjX < 0 || newObjX + obj.getSize() > WINDOW_SIZE) {
            return false;
        }
        for (GameTile tile : tiles) {
            if (isColliding(newObjX, obj.getY(), obj.getSize(), tile)) {
                return false;
            }
        }
        for (PushableObject other : pushableObjects) {
            if (other == obj) continue;
            if (isColliding(newObjX, obj.getY(), obj.getSize(), other)) {
                return false;
            }
        }
        obj.setX(newObjX);
        return true;
    }

    // Determine if the player is standing on any solid surface (tile or pushable object)
    private boolean isOnGround(Player player) {
        if (player.getY() + player.getSize() >= WINDOW_SIZE) {
            return true;
        }
        for (GameTile tile : tiles) {
            if (player.getY() + player.getSize() <= tile.getY() + 5 &&
                player.getY() + player.getSize() >= tile.getY() - 5 &&
                player.getX() + player.getSize() > tile.getX() &&
                player.getX() < tile.getX() + tile.getSize()) {
                return true;
            }
        }
        for (PushableObject obj : pushableObjects) {
            if (player.getY() + player.getSize() <= obj.getY() + 5 &&
                player.getY() + player.getSize() >= obj.getY() - 5 &&
                player.getX() + player.getSize() > obj.getX() &&
                player.getX() < obj.getX() + obj.getSize()) {
                return true;
            }
        }
        return false;
    }
    
    private Point getFirstCollisionPoint(double x1, double y1, double x2, double y2, GameObject obj) {
        double objLeft = obj.getX();
        double objRight = obj.getX() + obj.getSize();
        double objTop = obj.getY();
        double objBottom = obj.getY() + obj.getSize();

        ArrayList<Point> intersections = new ArrayList<>();

        // Check all 4 edges of the object
        checkLineIntersection(x1, y1, x2, y2, objLeft, objTop, objRight, objTop, intersections); // Top
        checkLineIntersection(x1, y1, x2, y2, objLeft, objBottom, objRight, objBottom, intersections); // Bottom
        checkLineIntersection(x1, y1, x2, y2, objLeft, objTop, objLeft, objBottom, intersections); // Left
        checkLineIntersection(x1, y1, x2, y2, objRight, objTop, objRight, objBottom, intersections); // Right

        if (intersections.isEmpty()) {
            return null;
        }

        // Find the closest intersection point to the starting position
        Point closest = intersections.get(0);
        double closestDist = Math.sqrt(Math.pow(closest.x - x1, 2) + Math.pow(closest.y - y1, 2));

        for (Point p : intersections) {
            double dist = Math.sqrt(Math.pow(p.x - x1, 2) + Math.pow(p.y - y1, 2));
            if (dist < closestDist) {
                closest = p;
                closestDist = dist;
            }
        }

        return closest;
    }

    
    private void checkLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4,
            double y4, ArrayList<Point> intersections) {
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denom == 0)
            return; // Lines are parallel

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            int intersectX = (int) (x1 + t * (x2 - x1));
            int intersectY = (int) (y1 + t * (y2 - y1));
            intersections.add(new Point(intersectX, intersectY));
        }
    }

    private void updatePosition() {
        // --- Grappling Hook Override ---
        // If space is pressed, immediately rethrow the grapple (override any existing one)
        if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
            int playerCenterX = player.getX() + player.getSize() / 2;
            int playerCenterY = player.getY() + player.getSize() / 2;
            hook = new GrapplingHook(playerCenterX, playerCenterY, mousePosition.x, mousePosition.y);
            // Remove space so that it doesn't continuously rethrow.
            pressedKeys.remove(KeyEvent.VK_SPACE);
        }

        // --- Player Horizontal Movement ---
        double dx = 0;
        if (pressedKeys.contains(KeyEvent.VK_A)) {
            dx = -HORIZONTAL_SPEED;
        } else if (pressedKeys.contains(KeyEvent.VK_D)) {
            dx = HORIZONTAL_SPEED;
        }
        int candidateX = player.getX() + (int) dx;
        int newX = candidateX;
        if (newX < 0) newX = 0;
        if (newX + player.getSize() > WINDOW_SIZE) newX = WINDOW_SIZE - player.getSize();
        for (GameTile tile : tiles) {
            if (isColliding(newX, player.getY(), player.getSize(), tile)) {
                newX = player.getX();
                break;
            }
        }
        for (PushableObject obj : pushableObjects) {
            if (isColliding(newX, player.getY(), player.getSize(), obj)) {
                if (!pushObjectHorizontally(obj, (int) dx)) {
                    newX = player.getX();
                }
            }
        }
        player.setX(newX);

        // --- Player Vertical Movement ---
        if (!(hook != null && hook.attached)) {
            if (pressedKeys.contains(KeyEvent.VK_W) && isOnGround(player)) {
                player.setVelocityY(JUMP_VELOCITY);
            }
            double currentGravity = GRAVITY;
            if (pressedKeys.contains(KeyEvent.VK_S)) {
                currentGravity *= FAST_FALL_MULTIPLIER;
            }
            player.setVelocityY(player.getVelocityY() + currentGravity);
            int candidateY = player.getY() + (int) player.getVelocityY();
            int newY = candidateY;
            if (newY < 0) newY = 0;
            if (newY + player.getSize() > WINDOW_SIZE) {
                newY = WINDOW_SIZE - player.getSize();
                player.setVelocityY(0);
            }
            for (GameTile tile : tiles) {
                if (isColliding(player.getX(), newY, player.getSize(), tile)) {
                    if (player.getVelocityY() > 0) {
                        newY = tile.getY() - player.getSize();
                    } else if (player.getVelocityY() < 0) {
                        newY = tile.getY() + tile.getSize();
                    }
                    player.setVelocityY(0);
                    break;
                }
            }
            for (PushableObject obj : pushableObjects) {
                if (isColliding(player.getX(), newY, player.getSize(), obj)) {
                    if (player.getVelocityY() > 0) {
                        newY = obj.getY() - player.getSize();
                    } else if (player.getVelocityY() < 0) {
                        newY = obj.getY() + obj.getSize();
                    }
                    player.setVelocityY(0);
                    break;
                }
            }
            player.setY(newY);
        } else {
            // --- Grapple Pulling ---
            int playerCenterX = player.getX() + player.getSize() / 2;
            int playerCenterY = player.getY() + player.getSize() / 2;
            double diffX = hook.targetX - playerCenterX;
            double diffY = hook.targetY - playerCenterY;
            double distance = Math.sqrt(diffX * diffX + diffY * diffY);
            if (distance < 10) {
                player.setVelocityY(hook.pullDy);
                hook = null;
            } 
            else {
                double pullSpeed = 10.0;
                hook.pullDx = (diffX / distance) * pullSpeed;
                hook.pullDy = (diffY / distance) * pullSpeed;
                int nextX = player.getX() + (int) hook.pullDx;
                int nextY = player.getY() + (int) hook.pullDy;
                boolean collision = false;
                for (GameTile tile : tiles) {
                    if (isColliding(nextX, nextY, player.getSize(), tile)) {
                        collision = true;
                        break;
                    }
                }
                for (PushableObject obj : pushableObjects) {
                    if (isColliding(nextX, nextY, player.getSize(), obj)) {
                        collision = true;
                        break;
                    }
                }
                if (collision) {
                    // If moving further would cause a collision, cancel the grapple.
                    hook = null;
                } 
                else {
                    player.setX(nextX);
                    player.setY(nextY);
                }
            }
        }

        // --- Update Pushable Objects ---
        for (PushableObject obj : pushableObjects) {
            obj.update(GRAVITY, WINDOW_SIZE, tiles, pushableObjects);
        }

        // --- Update Grappling Hook ---
        if (hook != null) {
            hook.update();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (PushableObject obj : pushableObjects) {
            obj.draw(g);
        }
        for (GameTile tile : tiles) {
            tile.draw(g);
        }
        if (hook != null) {
            int playerCenterX = player.getX() + player.getSize() / 2;
            int playerCenterY = player.getY() + player.getSize() / 2;
            int ropeEndX = hook.attached ? (int) hook.targetX : (int) hook.tipX;
            int ropeEndY = hook.attached ? (int) hook.targetY : (int) hook.tipY;
            g.setColor(Color.WHITE);
            g.drawLine(playerCenterX, playerCenterY, ropeEndX, ropeEndY);
            g.setColor(Color.GRAY);
            int hookRadius = 5;
            g.fillOval(ropeEndX - hookRadius, ropeEndY - hookRadius, hookRadius * 2, hookRadius * 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hook Platformer");
        SwingGame panel = new SwingGame();
        frame.add(panel);
        frame.setSize(panel.WINDOW_SIZE + 16, panel.WINDOW_SIZE + 39);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
