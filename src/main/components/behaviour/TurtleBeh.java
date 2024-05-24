package main.components.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.StateMachine;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.haspid.Window;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.*;

public class TurtleBeh extends Component {
    private transient boolean die;
    private double speed;
    private double gravity;
    private double minContact;
    private double coolDown;
    private double resetCollDown;
    private double currentSpeed;
    private transient RigidBody rigidBody;

    public TurtleBeh(){
        this.gravity = 100;
        this.minContact = 0.7;
        this.resetCollDown = 0.5;
        this.coolDown = resetCollDown;
        this.speed = -20;
        this.currentSpeed = speed;
    }

    @Override
    public void start(){
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);
    }

    @Override
    public TurtleBeh copy(){
        TurtleBeh turtleBeh = new TurtleBeh();
        turtleBeh.setSpeed(speed);
        turtleBeh.setGravity(gravity);
        turtleBeh.setMinContact(minContact);
        turtleBeh.setCoolDown(coolDown);
        turtleBeh.setResetCollDown(resetCollDown);
        turtleBeh.setCurrentSpeed(currentSpeed);

        return turtleBeh;
    }

    @Override
    public void update(float dt) {
        rigidBody.setVelocityX(currentSpeed);
        coolDown -= dt;
        Vector2d camPos = Window.getInstance().getCurrentScene().getCamera().getPosition();

        if(die && (getParent().getTransform().getPosition().x < camPos.x || getParent().getTransform().getPosition().x > camPos.x + uProjectionDimension.x)){
            die();
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
                if(!die) {
                    getParent().getComponent(StateMachine.class).switchAnimation("squashed");
                    BoxCollider boxCollider = getParent().getComponent(BoxCollider.class);
                    boxCollider.setHalfSize(new Vector2d(objectHalfSize, objectHalfSize));
                    boxCollider.resetFixture();
                    currentSpeed = 0;
                    die = true;
                }

                if(die){
                    if(coolDown < 0){
                        if(currentSpeed == 0){
                            currentSpeed = speed * 2;
                        }else{
                            currentSpeed = 0;
                        }
                        coolDown = resetCollDown;
                    }
                }
            }
        }

        if(playerController != null) return;
        if( Math.abs(contactNormal.x) > minContact){
            Vector2d scale = getParent().getTransform().getScale();
            scale.x = -scale.x;
            currentSpeed *= -1;
            if(isDie()){
                AssetPool.getSound(bump).play();
            }
        }
    }

    public boolean isDie(){
        return die;
    }

    public void die(){
        Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
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

    public void setCoolDown(double coolDown) {
        this.coolDown = coolDown;
    }

    public void setResetCollDown(double resetCollDown) {
        this.resetCollDown = resetCollDown;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }
}
