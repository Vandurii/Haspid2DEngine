package main.games.mario.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.RigidBody;
import main.haspid.GameObject;
import main.haspid.Window;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.powerUpAppears;

public class MushroomBeh extends Component {
    private double speed;
    private double gravity;
    private double minContact;
    private transient boolean used;
    private transient RigidBody rigidBody;

    public MushroomBeh(){
        this.speed = 20;
        this.gravity = 100;
        this.minContact = 0.1;
    }

    @Override
    public void init(){
        AssetPool.getSound(powerUpAppears);
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);
    }

    @Override
    public void update(float dt) {
        rigidBody.setVelocityX(speed);
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
       if( Math.abs(contactNormal.x) > minContact) speed *= -1;
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2d contactNormal){
        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if(playerController != null){
            contact.setEnabled(false);

            if(!used){
                playerController.powerUP();
                Window.getInstance().getCurrentScene().removeFromSceneSafe(getParent());
                used = true;
            }
        }
    }
}
