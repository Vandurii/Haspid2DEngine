package main.components.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.StateMachine;
import main.haspid.GameObject;
import main.haspid.Window;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.powerUpAppears;

public class GoombaBeh extends Component {
    private double speed;
    private double gravity;
    private double minContact;
    private transient RigidBody rigidBody;

    public GoombaBeh(){
        this.speed = 20;
        this.gravity = 100;
        this.minContact = 0.7;
    }

    @Override
    public void start(){
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
        PlayerController playerController = gameObject.getComponent(PlayerController.class);

        if (playerController != null && !playerController.isHurt()) {
            if (Math.abs(contactNormal.x) > 0.8) {
                playerController.powerDown();
            }else if (contactNormal.y > 0.3 && Math.abs(contactNormal.x) < 0.5) {
                getParent().getComponent(StateMachine.class).switchAnimation("squashed");
                Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
            }
        }

        if(playerController != null) return;
        if( Math.abs(contactNormal.x) > minContact) speed *= -1;
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2d contactNormal) {
    }
}
