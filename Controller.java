import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

public class Controller {

    // Sleep time between two frames.
    public static double currentTime;
    //public static double newTime;
    public static double T = 0;
    public static double DELAY = 1 / 120.0;
    public static double DT;

    // Camera
    public static double camX, camY;

    // Game Logic.
    public Player player;
    public Keys keyController;
    public static int level;

    // Array holding all the game objects.
    public static List<List<GameObject>> gameObjects = new ArrayList<List<GameObject>>();
    public static List<BGStar> bgStars = new ArrayList<BGStar>();
    public static ListIterator<List<GameObject>> iterator;
    public static ListIterator<GameObject> innerIterator;
    GameObject next = null;

    public Controller(){
        // Set initial level to 0 ( Level 1 = number 10);
        level = 30;

        // Player controls.
        keyController = new Keys();

        // Create level.
        createLevel();
    }

    public static void main(String[] args){
        Controller controller = new Controller();
        View view = new View(controller);
        new JEasyFrame(view, "Basic Controller").addKeyListener(controller.keyController);

        currentTime = System.currentTimeMillis() / 1000.0;
        double DEBUGtime = System.currentTimeMillis();
        double lastUpdate = System.currentTimeMillis();
        int tick = 0;
        int gTick = 0;
        int maxTicks = 1000 / 60;

        while (true) {
            /*
            // FPS limiter.
            while(System.currentTimeMillis() < lastUpdate + maxTicks) {
                try {
                    Thread.sleep(0);
                }
                catch (InterruptedException e) {
                    System.out.println("Major Error.");
                }

            }
            lastUpdate = System.currentTimeMillis();
            */

            // Update physics.
            double newTime = System.currentTimeMillis() / 1000.0;
            double frameTime = newTime - currentTime;
            currentTime = newTime;

            while(frameTime > 0.0) {
                double deltaTime = Math.min(frameTime, DELAY);

                DT = deltaTime;

                iterator = gameObjects.listIterator();
                controller.update();

                frameTime -= deltaTime;
                T += deltaTime;

                gTick++;
            }

            // DEBUG.
            tick++;
            if(System.currentTimeMillis() - DEBUGtime >= 1000) {
                DEBUGtime += 1000;
                System.out.println(tick + " Ticks, GTicks " + gTick);
                tick = 0;
            }
            gTick = 0;
            //System.out.println(T);

            // Update graphics.
            view.repaint();

            try {
                Thread.sleep(3);
            }
                catch (InterruptedException e) {
                System.out.println("Major Error.");
            }
        }
    }

    public void update() {
        // Setting camera.
        camX = player.getPos().x - View.VIEWPORT_SIZE_X / 2;
        camY = player.getPos().y - View.VIEWPORT_SIZE_Y / 2;
        if(camX > View.offsetMaxX) {
            camX = View.offsetMaxX;
        }
        else if(camX < View.offsetMinX) {
            camX = View.offsetMinX;
        }
        if(camY > View.offsetMaxY) {
            camY = View.offsetMaxY;
        }
        else if(camY < View.offsetMinY) {
            camY = View.offsetMinY;
        }

        // Check for collisions.
        // Check between elements in each list.
        for(int i = 0; i < Controller.gameObjects.size(); i++) {
            for(int j = 0; j < Controller.gameObjects.get(i).size(); j++) {
                for(int k = j; k < Controller.gameObjects.get(i).size(); k++) {
                    //DEBUG System.out.println(i + ", " + j + " Comparing " + i + ", " + k);
                    Controller.gameObjects.get(i).get(j).collisionHandling(Controller.gameObjects.get(i).get(k));
                }
            }
        }
        // Check between each element between lists.
        for(int i = 0; i < Controller.gameObjects.get(0).size(); i++) {
            for(int j = 0; j < Controller.gameObjects.get(1).size(); j++) {
                //DEBUG System.out.println(0 + ", " + i + " Comparing " + 1 + ", " + j);
                Controller.gameObjects.get(0).get(i).collisionHandling(Controller.gameObjects.get(1).get(j));
            }
        }

        // Update position.
        while(iterator.hasNext()) {
            List<GameObject> gObjectsList = iterator.next();
            innerIterator = gObjectsList.listIterator();

            while(innerIterator.hasNext()) {
                next = innerIterator.next();
                if(next != null) {
                    synchronized (Controller.class) {
                        if(next.isDead()) {
                            innerIterator.remove();

                            // CONSTRAINT: SPAWNING ASTEROIDS FROM LARGE ASTEROIDS.
                            if(next instanceof Asteroid) {
                                if(((Asteroid) next).getExpload()) {
                                    Asteroid.spawnAsteroid((Asteroid) next);
                                }
                            }
                        }
                        else {
                            next.update();
                        }
                    }
                }
            }
        }

        // If no asteroids or enemy ships in game, create new level. Reset if player has died.
        //DEBUG System.out.println(gameObjects.get(0).size());
        if(gameObjects.get(0).size() <= 1) {
            createLevel();
        }
        if(player.isDead()) {

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Major Error.");
            }
            level = 0;
            player.setScore(0);
            createLevel();
        }
    }

    // Procedural Level Generator.
    public void createLevel() {
        // Level control.
        level += 10;

        // Map size;
        View.offsetMaxX = (1.25 + (level / 20.0)) * View.quadrantX;
        View.offsetMaxY = (1.25 + (level / 20.0)) * View.quadrantY;

        // Add arrays to multidimensional array.
        if(!gameObjects.isEmpty()) {
            gameObjects.clear();
        }
        gameObjects.add(0, new ArrayList<GameObject>());
        gameObjects.add(1, new ArrayList<GameObject>());

        // Background stars.
        if(!bgStars.isEmpty()) {
            bgStars.clear();
        }
        for(int i = 0; i < (160 * Math.pow(View.offsetMaxX / View.quadrantX, 2)); i++) {
            double x = (Math.random() * ((View.offsetMaxX + 1920) - 5 - View.offsetMinX)) + 5 + View.offsetMinX;
            double y = (Math.random() * ((View.offsetMaxY + 1080) - 5 - View.offsetMinY)) + 5 + View.offsetMinY;
            bgStars.add(new BGStar(x, y, Color.white, 5));
        }

        // Create player.
        player = new Player(keyController, 2000, 2000, 0, 0);
        gameObjects.get(0).add(player);

        // Generate level based on random values.
        int rPercentage = (int) (Math.random() * (100 - 1)) + 1;
        System.out.println(rPercentage + " - " + level);

        if(rPercentage > (100 - level)) {
            System.out.println("Hard");

            // Asteroids.
            for(int i = 1; i < 20 + (level / 5.0) + (level * (rPercentage / 100.0)); i++) {
                Asteroid asteroid = Asteroid.createRanAsteroid();
                gameObjects.get(0).add(asteroid);
            }
            // Enemy Ships.
            for(int i = 1; i < 1.5 + ((level / 5.0) * (rPercentage / 100.0)); i++) {
                Enemy enemy = Enemy.createEnemy(new EnemyController());
                gameObjects.get(0).add(enemy);
            }
            // Gravitational Objects.
            for(int i = 1; i < 0 + Math.log10(level / 5.0) * ((rPercentage / 100.0) + 1.1); i++) {
                BlackHole bHole = BlackHole.createBHole();
                gameObjects.get(1).add(bHole);
            }
        }
        else if(rPercentage > (40 - level)) {
            System.out.println("Normal");

            // Asteroids.
            for(int i = 1; i < 15 + (level / 5.0) + (level * (rPercentage / 100.0)); i++) {
                Asteroid asteroid = Asteroid.createRanAsteroid();
                gameObjects.get(0).add(asteroid);
            }
            // Enemy Ships.
            for(int i = 1; i < 1 + ((level / 5.0) * (rPercentage / 100.0)); i++) {
                Enemy enemy = Enemy.createEnemy(new EnemyController());
                gameObjects.get(0).add(enemy);
            }
            // Gravitational Objects.
            for(int i = 1; i < 0 + Math.log10(level / 5.0) * ((rPercentage / 100.0) + 0.75); i++) {
                BlackHole bHole = BlackHole.createBHole();
                gameObjects.get(1).add(bHole);
            }
        }
        else if(rPercentage > 0) {
            System.out.println("Easy");

            // Asteroids.
            for(int i = 1; i < 10 + (level / 5.0) + (level * (rPercentage / 100.0)); i++) {
                Asteroid asteroid = Asteroid.createRanAsteroid();
                gameObjects.get(0).add(asteroid);
            }
            // Enemy Ships.
            for(int i = 1; i < 0.5 + ((level / 5.0) * (rPercentage / 100.0)); i++) {
                Enemy enemy = Enemy.createEnemy(new EnemyController());
                gameObjects.get(0).add(enemy);
            }
            // Gravitational Objects.
            for(int i = 1; i < 0 + ((level / 25.0) * (rPercentage / 100.0)); i++) {
                BlackHole bHole = BlackHole.createBHole();
                gameObjects.get(1).add(bHole);
            }
        }


    }

    public int countAsteroids() {
        int count = 0;
        for(GameObject object : gameObjects.get(0)) {
            // Count amount of asteroids.
            if(object instanceof Asteroid) {
                count++;
            }
        }
        return count;
    }
    public int countEnemies() {
        int count = 0;
        for(GameObject object : gameObjects.get(0)) {
            // Count amount of asteroids.
            if(object instanceof Enemy) {
                count++;
            }
        }
        return count;
    }

}
