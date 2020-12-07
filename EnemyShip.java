import java.awt.*;
import java.awt.geom.AffineTransform;

public class EnemyShip extends Enemy {

    // Polygon points
    private int[] XP = new int[]{-2, 0, 2, 0, -2};
    private int[] YP = new int[]{2, -2, 2, 0, 2};
    private int[] XPTHRUST = new int[]{1, 0, -1};
    private int[] YPTHRUST = new int[]{1, 4, 1};

    public EnemyShip(KeyController ctrl, double x, double y, double vx, double vy) {
        super(ctrl, x, y, vx, vy, new Color(252, 0, 8), 8, 300, 0.0002, 1, 750);
    }

    public void update() {
        super.update();
    }

    public void draw(Graphics2D g) {
        AffineTransform at = g.getTransform();
        g.translate(getPos().x, getPos().y);
        g.translate(-Controller.camX, -Controller.camY);
        double rot = super.getDirection().angle() + Math.PI / 2;
        g.rotate(rot);
        g.scale(5, 5);
        g.setColor(getColor());
        g.fillPolygon(XP, YP, XP.length);
        if (super.getIsThrusting()) {
            g.setColor(Color.orange);
            g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
        }
        g.setTransform(at);
    }
}
