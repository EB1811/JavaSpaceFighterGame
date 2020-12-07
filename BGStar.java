import java.awt.*;

public class BGStar {
    private Vector2D pos;
    private Color color;
    private double radius;

    public BGStar(double x, double y, Color color, double rad) {
        pos = new Vector2D(x, y);
        this.color = color;
        this.radius = rad;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval(((int) pos.x - (int) radius) - (int) Controller.camX, ((int) pos.y - (int) radius) - (int) Controller.camY, (int) radius, (int) radius);
    }

}
