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
    private boolean die;
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
        AssetPool.getSound(powerUpAppears);
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);
    }

    @Override
    public void update(float dt) {
        rigidBody.setVelocityX(currentSpeed);
        coolDown -= dt;
        Vector2d camPos = Window.getInstance().getCurrentScene().getCamera().getPosition();

        if(getParent().getTransform().getPosition().x < camPos.x || getParent().getTransform().getPosition().x > camPos.x + uProjectionDimension.x){
            System.out.println("now");
            Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
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
        }
    }

    public boolean isDie(){
        return die;
    }
}
