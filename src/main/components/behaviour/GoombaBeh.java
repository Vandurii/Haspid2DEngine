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
    private double corpseTime;
    private transient boolean die;
    private transient RigidBody rigidBody;

    public GoombaBeh(){
        this.speed = 20;
        this.gravity = 100;
        this.minContact = 0.7;
        this.corpseTime = 1;
    }

    @Override
    public void init(){
        AssetPool.getSound(powerUpAppears);
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);
    }

    @Override
    public GoombaBeh copy(){
        GoombaBeh goombaBeh = new GoombaBeh();
        goombaBeh.setSpeed(speed);
        goombaBeh.setGravity(gravity);
        goombaBeh.setMinContact(minContact);

        return goombaBeh;
    }

    @Override
    public void update(float dt) {
        if(!die) {
            rigidBody.setVelocityX(speed);
        }else if((corpseTime -= dt) < 0){
            destroyCorpse();
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        // die from fireball
        FireballBeh fireballBeh = gameObject.getComponent(FireballBeh.class);
        if(fireballBeh != null){
            die();
        }

        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if (playerController != null && !playerController.isHurt()) {
            if (Math.abs(contactNormal.x) > 0.8) {
                playerController.powerDown();
            }else if (contactNormal.y > 0.3 && Math.abs(contactNormal.x) < 0.5) {
                getParent().getComponent(StateMachine.class).switchAnimation("squashed");
                die();
            }
        }

        TurtleBeh turtleBeh = gameObject.getComponent(TurtleBeh.class);
        if(turtleBeh != null && turtleBeh.isDie()){
            die();
        }

        if(playerController != null) return;
        if( Math.abs(contactNormal.x) > minContact) speed *= -1;
    }

    public void die(){
        die = true;
    }

    public void destroyCorpse(){
        Window.getInstance().getCurrentScene().removeFromSceneSafe(getParent());
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public void setMinContact(double minContact) {
        this.minContact = minContact;
    }
}
