public class EnemyController implements KeyController {
    Action action;
    EnemyShip enemy;

    @Override
    public Action action() {
        if(getPlayer() != null && getEnemy() != null) {
            double distanceToPlayer = getEnemy().getPos().dist(getPlayer().getPos());
            if(distanceToPlayer > 400) {
                action.thrust = 1;
            }
            else {
                action.thrust = 0;
            }

            Vector2D pPosVec = new Vector2D(getPlayer().getPos());
            pPosVec = pPosVec.subtract(getEnemy().getPos());
            if (getEnemy().getPos().dot(pPosVec.normalise(), getEnemy().getDirection().normalise()) != 1) {
                double cross = (getEnemy().getDirection().normalise().x * pPosVec.normalise().y) - (getEnemy().getDirection().normalise().y * pPosVec.normalise().x);

                if(cross == 0.0f) {
                    action.turn = 0;
                }
                if(cross < 0.0f) {
                    action.turn = -1;
                }
                if(cross > 0.0f) {
                    action.turn = +1;
                }
                if((cross > -0.1f && cross < 0.1f) && distanceToPlayer < 500) {
                    action.shoot = true;
                }
            }
        }
        return action;
    }

    public EnemyController() {
        this.action = new Action();
    }

    @Override
    public void getEnemy(GameObject enemy) {
        this.enemy = (EnemyShip) enemy;
    }

    public static Player getPlayer() {
        for(GameObject object : Controller.gameObjects.get(0)) {
            if(object instanceof Player) {
                return (Player) object;
            }
        }
        return null;
    }
    public EnemyShip getEnemy() {
        for(GameObject object : Controller.gameObjects.get(0)) {
            if(object == enemy) {
                return (EnemyShip) enemy;
            }
        }
        return null;
    }
}
