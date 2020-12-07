import java.awt.*;

public class Bullet extends GameObject {

    private double time;
    private double timeToLive = 1500;
    private GameObject owner;

    public Bullet(GameObject owner, double x, double y, double vx, double vy) {
        super(x, y, vx , vy, new Color(255, 251, 76));
        this.owner = owner;
        time = System.currentTimeMillis();
        setRadius(4);
        setMass(1);
        setDRAG(0);
    }

    public void update() {
        super.update();
        if(Math.abs(time - System.currentTimeMillis()) > timeToLive) {
            setDead(true);
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(getColor());
        g.fillOval(((int) getPos().x - (int) getRadius()) - (int) Controller.camX, ((int) getPos().y - (int) getRadius()) - (int) Controller.camY, 2 * (int) getRadius(), 2 * (int) getRadius());
    }

    public double getGForce(Vector2D pos, double m1) { return 0; }

    public void hit(GameObject otherObject) {
        if(otherObject instanceof Asteroid && this.owner instanceof Player) {
            ((Player) this.owner).setScore(((Player) this.owner).getScore() + 1);
        }
        setDead(true);
    }

}
