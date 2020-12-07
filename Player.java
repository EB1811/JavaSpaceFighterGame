import java.awt.*;
import java.awt.geom.AffineTransform;

public class Player extends GameObject {

    // rotation velocity in radians per second
    public static final double STEER_RATE = 1 * Math.PI;
    // acceleration when thrust is applied
    public static final double MAX_ACC = 300;

    // GAME logic variables.
    private int life = 3;
    private static int score = 0;

    // Direction thrust is applied.
    private Vector2D direction;
    private double magAcceleration = 0;
    private KeyController ctrl;
    private boolean thrusting = false;

    // Variable holding bullet object.
    private Bullet bullet;
    private double time;
    private double rateOfFire = 500;

    // Polygon points
    private int[] XP = new int[]{-2, 0, 2, 0, -2};
    private int[] YP = new int[]{2, -2, 2, 0, 2};
    private int[] XPTHRUST = new int[]{1, 0, -1};
    private int[] YPTHRUST = new int[]{1, 4, 1};

    public Player(KeyController ctrl, double x, double y, double vx, double vy) {
        super(x, y, vx, vy, new Color(3, 252, 207));
        time = System.currentTimeMillis();
        setRadius(8);
        setMass(1);
        setDRAG(0.0002);
        direction = new Vector2D(0, -1);
        this.ctrl = ctrl;
    }

    public void update() {
        Action action = ctrl.action();

        // Check if still alive.
        if(life < 1) {
            setDead(true);
        }

        // Direction calculation.
        direction.rotate(STEER_RATE * action.turn * Controller.DT);

        // Thrust calculation.
        thrusting = action.thrust > 0;
        if(thrusting == true) {
            // Accelerate up to a maximum.
            magAcceleration = (magAcceleration < MAX_ACC) ?  magAcceleration + 10 : MAX_ACC;
        }
        else if(!thrusting) {
            magAcceleration = 0;
        }
        //DEBUG
        //System.out.println("Acceleration: " + magAcceleration + " Speed: " + (int) getVel().mag());
        //System.out.println("Position X: " + (int) getPos().x + " Y: " + (int) getPos().y);
        //System.out.println(magAcceleration);
        //System.out.println((int) getVel().mag());
        Vector2D newVel = getVel().addScaled(direction, (magAcceleration * Controller.DT * action.thrust));
        setVel(newVel);

        // Update coordinates.
        super.update();

        // Shoot Bullet.
        if(action.shoot && Math.abs(time - System.currentTimeMillis()) > rateOfFire) {
            bullet = new Bullet(this, getPos().x + (getRadius() * 2 * direction.x), getPos().y + (getRadius() * 2 * direction.y), (direction.x * 2000), (direction.y * 2000));
            Controller.innerIterator.add(bullet);
            action.shoot = false;
            time = System.currentTimeMillis();
        }
    }

    public void draw(Graphics2D g) {
        AffineTransform at = g.getTransform();
        g.translate(getPos().x, getPos().y);
        g.translate(-Controller.camX, -Controller.camY);
        double rot = direction.angle() + Math.PI / 2;
        g.rotate(rot);
        g.scale(5, 5);
        g.setColor(getColor());
        g.fillPolygon(XP, YP, XP.length);
        if (thrusting) {
            g.setColor(Color.orange);
            g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
        }
        g.setTransform(at);
    }

    public double getGForce(Vector2D pos, double m1) { return 0; }

    public void hit(GameObject otherObject) {
        if(otherObject instanceof Bullet) {
            life -= 1;
        }
        else {
            setDead(true);
        }
    }

    public Vector2D getDirection() {
        return direction;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public int getLife() {
        return life;
    }
}
