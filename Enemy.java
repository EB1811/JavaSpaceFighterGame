import java.awt.*;

public abstract class Enemy extends GameObject {

    // rotation velocity in radians per second
    private double STEER_RATE = 1 * Math.PI;
    // acceleration when thrust is applied
    private double MAX_ACC;

    // Life.
    private int life;

    // Direction thrust is applied.
    private Vector2D direction;
    private double magAcceleration = 0;
    private KeyController ctrl;
    private boolean thrusting = false;

    // Variable holding bullet object.
    private Bullet bullet;
    private double time;
    private double rateOfFire;

    public Enemy(KeyController ctrl, double x, double y, double vx, double vy, Color color, int radius, double acc, double drag, int life, double rateOfFire) {
        super(x, y, vx, vy, color);
        setRadius(radius);
        setMass(1);
        setDRAG(drag);
        direction = new Vector2D(0, -1);
        this.MAX_ACC = acc;
        this.life = life;
        this.rateOfFire = rateOfFire;
        this.ctrl = ctrl;
    }

    public void update() {
        ctrl.getEnemy(this);
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

        // Add new vel.
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

    public static Enemy createEnemy(KeyController ctrl){
        // Random non overlapping position.
        double x = (Math.random() * (View.offsetMaxX - 8 - View.offsetMinX)) + 8 + View.offsetMinX;
        double y = (Math.random() * (View.offsetMaxY - 8 - View.offsetMinY)) + 8 + View.offsetMinY;
        //DEBUG System.out.println(x + "<- x y ->" + y);

        // Return Asteroid object.
        return new EnemyShip(ctrl, x, y, 0, 0);
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
    public boolean getIsThrusting() {
        return thrusting;
    }
}
