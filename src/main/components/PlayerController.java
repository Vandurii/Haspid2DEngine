package main.components;

import main.components.stateMachine.StateMachine;
import main.haspid.Direction;
import main.haspid.KeyListener;
import main.haspid.Window;
import main.physics.Physics2D;
import main.components.physicsComponent.RigidBody;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    private float walkSpeed = 1.9f;
    private float jumpBoost = 1f;
    private float jumpImpulse = 3f;
    private float slowDownForce = 0.05f;
    private Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private boolean onGround = false;
    private transient float groundDebounce = 0f;
    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody rigidBody;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 32f;//0.25f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;

    private transient Physics2D physics;
    private transient KeyListener keyboard;

    public PlayerController(){
        this.keyboard = KeyListener.getInstance();
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
    }

    @Override
    public void start(){
        rigidBody = getParent().getComponent(RigidBody.class);
        if(rigidBody != null) {
            rigidBody.setGravityScale(0f);
            stateMachine = getParent().getComponent(StateMachine.class);
        }
    }

    @Override
    public void update(float dt) {
        if(rigidBody == null) return;
        if(keyboard.isKeyPressed(GLFW_KEY_UP)){
            move(Direction.Up);
        }else if(keyboard.isKeyPressed(GLFW_KEY_DOWN)){
            move(Direction.Down);
        }else if(keyboard.isKeyPressed(GLFW_KEY_RIGHT)){
            move(Direction.Right);
        }else if(keyboard.isKeyPressed(GLFW_KEY_LEFT)){
            move(Direction.Left);
        }else{
            acceleration.x = 0;
            if(velocity.x > 0){
                velocity.x = Math.max(0, velocity.x - slowDownForce);
            }else{
                velocity.x = Math.min(0, velocity.x + slowDownForce);
            }

            if(velocity.x == 0f){
                //stateMachine.trigger("stopRunning");
            }
        }

        acceleration.y = physics.getGravity().y * 0.7f;
        velocity.x += acceleration.x ;
        velocity.y += acceleration.y ;
        velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rigidBody.setVelocity(velocity);
        rigidBody.setAngularVelocity(0);
    }

    public void move(Direction direction){
        Vector2f scale = getParent().getTransform().getScale();

        switch (direction){
            case Up -> {

            }
            case Down -> {

            }
            case Right -> {
                scale.x = playerWidth;
                acceleration.x = walkSpeed;

                if(velocity.x < 0){
                    //stateMachine.trigger("switchDirection");
                    velocity.x += slowDownForce;
                }else{
                    //stateMachine.trigger("startRunning");
                }
            }
            case Left -> {
                scale.x = -playerWidth;
                acceleration.x = -walkSpeed;

                if(velocity.x < 0){
                    //stateMachine.trigger("switchDirection");
                    velocity.x -= slowDownForce;
                }else{
                    //stateMachine.trigger("startRunning");
                }
            }
        }
    }
}
