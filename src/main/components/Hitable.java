package main.components;

import main.components.Component;
import main.components.PlayerController;
import main.haspid.GameObject;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.bump;

public class Hitable extends Component {
    private double speed;
    private double distance;
    private transient double maxPosY;
    private transient double startPosY;
    private transient boolean active;

    public Hitable(){
        this.speed = 3;
        this.distance = 0.4;
    }

    @Override
    public void start(){
        this.startPosY = getParent().getTransform().getPosition().y;
        this.maxPosY = startPosY + distance;
    }

    @Override
    public void update(float dt) {
        Vector2d pos = getParent().getTransform().getPosition();

        if(active){
            if(pos.y < maxPosY){
                pos.y += speed * dt;
            }else if(pos.y > startPosY){
                pos.y = startPosY;
                active = false;
            }
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if(playerController != null && contactNormal.y < -0.8f){
            active = true;
            AssetPool.getSound(bump).play();
            playerHit(playerController);
        }
    }

    public void playerHit(PlayerController playerController){};
}
