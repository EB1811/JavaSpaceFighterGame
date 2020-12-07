import java.awt.*;

public class Asteroid extends GameObject {

    private double time;
    private double timeToLive = 5000;
    private int size;
    private boolean expload;

    public Asteroid(double x, double y, double vx, double vy, int size) {
        super(x, y, vx , vy, new Color(72, 56, 55));
        time = System.currentTimeMillis();
        this.size = size;
        expload = false;
        setRadius(size);
        setMass(200);
        setDRAG(0.00001);
    }

    public void update() {
        super.update();

        // Explode large asteroids. TODO: create new asteroids on death.
        if(size > 30) {
            if(Math.abs(time - System.currentTimeMillis()) > timeToLive) {
                expload = true;
                setDead(true);

                System.out.println("dead");
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(getColor());
        g.fillOval(((int) getPos().x - (int) getRadius()) - (int) Controller.camX, ((int) getPos().y - (int) getRadius()) - (int) Controller.camY, 2 * (int) getRadius(), 2 * (int) getRadius());
    }

    public static Asteroid createRanAsteroid(){
        // Create random number between 1 and 99, and use that to determine stats of asteroid.
        int rPercentage = (int) (Math.random() * (99 - 1)) + 1;
        int rSize = 15;
        if(rPercentage > 80) {
            rSize = (int) (Math.random() * (40 - 31)) + 31;
        }
        else if(rPercentage > 50) {
            rSize = (int) (Math.random() * (30 - 21)) + 21;
        }
        else if(rPercentage > 0) {
            rSize = (int) (Math.random() * (21 - 10)) + 10;
        }

        // Random velocity.
        double vx = (Math.random() * (150 - (-150))) + (-150);
        double vy = (Math.random() * (150 - (-150))) + (-150);

        // Random non overlapping position.
        double x = (Math.random() * (View.offsetMaxX - rSize - View.offsetMinX)) + rSize + View.offsetMinX;
        double y = (Math.random() * (View.offsetMaxY - rSize - View.offsetMinY)) + rSize + View.offsetMinY;
        //DEBUG System.out.println(x + "<- x y ->" + y);

        // Return Asteroid object.
        return new Asteroid(x, y, vx, vy, rSize);
    }

    // Spawn smaller asteroids.
    public static void spawnAsteroid(Asteroid asteroid) {
        Controller.innerIterator.add(new Asteroid(asteroid.getPos().x + asteroid.getRadius(), asteroid.getPos().y + asteroid.getRadius(), (Math.random() * (150 - 1)) + 1, (Math.random() * (150 - 1)) + 1, asteroid.getSize() / 2));
        Controller.innerIterator.add(new Asteroid(asteroid.getPos().x - asteroid.getRadius(), asteroid.getPos().y - asteroid.getRadius(), (Math.random() * (1 - (-150))) + (-150), (Math.random() * (1 - (-150))) + (-150), asteroid.getSize() / 2));
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public boolean getExpload() { return expload; }

    public double getGForce(Vector2D pos, double m1) { return 0; }

    public void hit(GameObject otherObject) {
        setDead(true);
    }
}
