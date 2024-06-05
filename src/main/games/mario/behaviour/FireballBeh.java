package main.games.mario.behaviour;

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
    private boolean goingRight;
    private RigidBody rigidBody;
    private double fireBallLifeTime;
    private PlayerController playerController;

    public FireballBeh(PlayerController playerController, boolean goingRight){
        this.speed = 100;
        this.gravity = 1;
        this.minContact = 0.4;
        this.fireBallLifeTime = 1;
        this.goingRight = goingRight;
        this.playerController = playerController;
    }

    @Override
    public void init(){
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);

        speed = goingRight ? speed : -speed;
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
        Window.getInstance().getCurrentScene().removeFromSceneSafe(getParent());
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        FireballBeh fireballBeh = gameObject.getComponent(FireballBeh.class);
        if(fireballBeh != null) return;

        if( Math.abs(contactNormal.x) > minContact) destroy();;
    }
}
