import java.awt.*;

public abstract class GameObject {

    private Vector2D pos;
    private Vector2D vel;
    private Color color;
    private double radius;
    private boolean dead;
    private double DRAG;
    private double mass;

    public GameObject() {
    }

    public GameObject(double x, double y, double vx, double vy, Color color) {
        pos = new Vector2D(x, y);
        vel = new Vector2D(vx, vy);
        dead = false;
        this.color = color;
    }

    public void update() {
        // Gravity calculation. Only for objects with large gravities (black holes and planets) since small objects will have a tiny gravitational force.
        Vector2D tForce = new Vector2D(0, 0);
        if(!Controller.gameObjects.get(1).isEmpty()) {
            for(GameObject object  : Controller.gameObjects.get(1)) {
                if(object != this) {
                    Vector2D bHoldDirection = new Vector2D(object.getPos().x - getPos().x, object.getPos().y - getPos().y);
                    bHoldDirection = bHoldDirection.normalise();
                    vel.addScaled(bHoldDirection, (object.getGForce(getPos(), getMass())) / getMass() * Controller.DT);
                    tForce.addScaled(bHoldDirection, (object.getGForce(getPos(), getMass())) / getMass());

                    /* DEBUG
                    if(object instanceof BlackHole && this instanceof Player) {
                        System.out.println(getMass() + " <- to -> " + object.getMass() + " Force -> " + object.getGForce(getPos(), getMass()) / getMass());
                    }*/
                }
            }
            /* DEBUG
            if(this instanceof Player) {
                System.out.println("Total Force: " + tForce.mag());
            }*/
        }

        // Drag calculation. Based on drag equation.
        double dragMag = DRAG * vel.mag() * vel.mag() * Controller.DT;
        Vector2D dragDirection = vel.normalise();
        dragDirection.mult(-1);
        dragDirection.mult(dragMag);
        vel.add(dragDirection);

        // Update position.
        pos.addScaled(vel, Controller.DT);

        // Wrap.
        pos.set((pos.x + View.offsetMaxX + 1920) % (View.offsetMaxX + 1920), (pos.y + View.offsetMaxY + 1080) % (View.offsetMaxY + 1080));
    }

    public abstract void draw(Graphics2D g);
    public abstract void hit(GameObject object);
    public abstract double getGForce(Vector2D pos, double m1);

    // Checking for collisions.
    public boolean overlap(GameObject other) {
        // Overlap detections.
        double distance = pos.dist(other.getPos());
        // DEBUG: System.out.println(distance);

        if((distance - this.radius - other.getRadius()) < 0) {
            return true;
        }
        return false;
    }
    public void collisionHandling(GameObject other) {
        if(this != other) {
            if(this.overlap(other)) {
                System.out.println("Confirmed Collision");
                this.hit(other);
                other.hit(this);
            }
        }
    }

    // Variable getters and setters.
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public Vector2D getPos() {
        return pos;
    }
    public void setPos(Vector2D pos) {
        this.pos = pos;
    }
    public Vector2D getVel() {
        return vel;
    }
    public void setVel(Vector2D vel) {
        this.vel = vel;
    }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }
    public boolean isDead() {
        return dead;
    }
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    public double getMass() {
        return mass;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }
    public double getDRAG() {
        return DRAG;
    }
    public void setDRAG(double DRAG) {
        this.DRAG = DRAG;
    }

}
