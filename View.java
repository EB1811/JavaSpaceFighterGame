import javax.swing.*;
import java.awt.*;

public class View extends JComponent {
    // Background colour and window size.
    public static final Color BG_COLOR = Color.black;
    public static final Dimension FRAME_SIZE = new Dimension(1920,1080);
    public static final double VIEWPORT_SIZE_X = 1920;
    public static final double VIEWPORT_SIZE_Y = 1080;

    // Map size.
    public static double quadrantX = 1920;
    public static double quadrantY = 1920;
    public static double offsetMaxX = 7680 - VIEWPORT_SIZE_X;
    public static double offsetMaxY = 7680 - VIEWPORT_SIZE_Y;
    public static double offsetMinX = 0;
    public static double offsetMinY = 0;

    private Controller controller;

    public View(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;

        // Paint the background.
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        int playerLife = (controller.player != null) ? controller.player.getLife() : 0;
        int playerScore = (controller.player != null) ? controller.player.getScore() : 0;
        String UI = "Level: " + Integer.toString(Controller.level / 5) + "   Score: " + Integer.toString(playerScore) + "   Life: " + Integer.toString(playerLife) + "   Asteroids Left: " + Integer.toString(controller.countAsteroids()) + "   Enemies Left: " + Integer.toString(controller.countEnemies());
        g.drawString(UI, 10, 1050);

        for(BGStar star : Controller.bgStars) {
            star.draw(g);
        }

        // Draw all game objects.
        synchronized (Controller.class) {
            for(java.util.List<GameObject> list : Controller.gameObjects) {
                for (GameObject object : list) {
                    object.draw(g);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return FRAME_SIZE;
    }
}
