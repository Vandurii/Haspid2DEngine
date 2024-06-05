package main.games.mario.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.StateMachine;
import main.editor.editorControl.MouseControls;
import main.haspid.GameObject;
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
    public void init(){
        this.rigidBody = getParent().getComponent(RigidBody.class);
        rigidBody.setGravityScale(gravity);
    }

    @Override
    public TurtleBeh copy(){
        System.out.println("copy run");
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
        double contactX = formatDouble(contactNormal.x);
        double contactY = formatDouble(contactNormal.y);
        System.out.println(gameObject);

        System.out.println(String.format("x: " + contactX + "y: " + contactY));

        // die from fireball
        FireballBeh fireballBeh = gameObject.getComponent(FireballBeh.class);
        if(fireballBeh != null){
            die();
        }
        PlayerController playerController = gameObject.getComponent(PlayerController.class);

        if (playerController != null && !playerController.isHurt()) {
            if (contactX > contactY) {
                playerController.powerDown();
            }else if (contactY > contactX) {
                if(!die) {
                    getParent().getComponent(StateMachine.class).switchAnimation("squashed");
                    getParent().shrink(false, true, 40);
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
        if( contactX > minContact){
            getParent().getComponent(StateMachine.class).rotateCornets();
            currentSpeed *= -1;
            if(isDie()){
                AssetPool.getSound(bump).play();
            }
        }
    }

    public double formatDouble(double value){
        return Math.abs(Math.floor(value * 100) / 100);
    }

    public boolean isDie(){
        return die;
    }

    public void die(){
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
