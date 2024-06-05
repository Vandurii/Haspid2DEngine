package main.components;

import main.haspid.GameObject;
import main.haspid.Window;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.breakBlock;
import static main.Configuration.bump;

public class Hitable extends Component {
    private double speed;
    private double distance;
    private transient double maxPosY;
    private transient double startPosY;
    private transient boolean active;
    private transient PlayerController playerController;

    public Hitable(){
        this.speed = 3;
        this.distance = 0.4;
    }

    @Override
    public void init(){
        this.startPosY = getParent().getTransform().getPosition().y;
        this.maxPosY = startPosY + distance;
    }

    @Override
    public Hitable copy(){
        Hitable hitable = new Hitable();
        hitable.setDistance(distance);
        hitable.setSpeed(speed);

        return hitable;
    }

    @Override
    public void update(float dt) {
        Vector2d pos = getParent().getTransform().getPosition();

        if(active){
            if(playerController != null && playerController.getPlayerState() == PlayerController.PlayerState.small) {
                if (pos.y < maxPosY) {
                    pos.y += speed * dt;
                } else if (pos.y > startPosY) {
                    pos.y = startPosY;
                    active = false;
                }
            }else if(playerController != null){
                AssetPool.getSound(breakBlock).play();;
                Window.getInstance().getCurrentScene().removeFromSceneSafe(getParent());
            }
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        System.out.println("now");
        playerController = gameObject.getComponent(PlayerController.class);
        if(playerController != null && contactNormal.y < -0.8f){
            active = true;
            AssetPool.getSound(bump).play();
            playerHit(playerController);
        }
    }

    public void playerHit(PlayerController playerController){};

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
