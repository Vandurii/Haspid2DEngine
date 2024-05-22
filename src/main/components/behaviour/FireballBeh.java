package main.components.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.RigidBody;
import main.haspid.GameObject;
import main.haspid.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

public class FireballBeh extends Component {
    private double speed;
    private double gravity;
    private double minContact;
    private double fireBallLifeTime;
    private PlayerController playerController;
    private RigidBody rigidBody;

    public FireballBeh(PlayerController playerController){
        this.speed = 50;
        this.gravity = 1;
        this.minContact = 0.4;
        this.fireBallLifeTime = 1;
        this.playerController = playerController;
    }

    @Override
    public void start(){
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);

        Vector2d scale = playerController.getParent().getTransform().getScale();
        speed = scale.x > 0 ? speed : -speed;
        rigidBody.setVelocityX(speed);
    }


    @Override
    public void update(float dt) {
        if((fireBallLifeTime -= dt) < 0){
            destroy();
        }
    }

    public void destroy(){
        playerController.removeFireball(getParent());
        Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        FireballBeh fireballBeh = gameObject.getComponent(FireballBeh.class);
        if(fireballBeh != null) return;

        if( Math.abs(contactNormal.x) > minContact) destroy();;
    }
}
