import java.awt.*;

public class BlackHole extends GameObject {

    public BlackHole(double x, double y, double vx, double vy) {
        super(x, y, vx , vy, Color.white);
        setRadius(10);
        setMass(12500000);
        setDRAG(0.00001);
    }

    public void update() {
        super.update();
    }

    public void draw(Graphics2D g) {
        //g.setColor(getColor());
        g.setColor(Color.white);
        //g.fillOval((int) getPos().x - (int) getRadius(), (int) getPos().y - (int) getRadius(), 2 * (int) getRadius(), 2 * (int) getRadius());
        g.setStroke(new BasicStroke(2));
        g.drawOval(((int) getPos().x - (int) getRadius()) - (int) Controller.camX, ((int) getPos().y - (int) getRadius()) - (int) Controller.camY, 2 * (int) getRadius(), 2 * (int) getRadius());
    }

    public static BlackHole createBHole(){
        // Random velocity.
        double vx = (Math.random() * (10 - (-10))) + (-10);
        double vy = (Math.random() * (10 - (-10))) + (-10);

        // Random non overlapping position.
        double x = (Math.random() * (View.offsetMaxX - 20 - View.offsetMinX)) + 20 + View.offsetMinX;
        double y = (Math.random() * (View.offsetMaxY - 20 - View.offsetMinY)) + 20 + View.offsetMinY;
        System.out.println(x + "<- x y ->" + y);

        // Return Asteroid object.
        return new BlackHole(x, y, vx, vy);
    }

    public double getGForce(Vector2D pos, double m1) {
        double distance = getPos().dist(pos);
        double force = (distance > getRadius()) ? (getMass() * m1) / (Math.pow(distance, 2)) : (getMass() * m1) / (Math.pow(getRadius(), 2));
        //System.out.println("Distance: " + (int) distance + " Acceleration: " + (int) (force / m1));
        return force;
    }

    public void hit(GameObject otherObject) {
    }
}
