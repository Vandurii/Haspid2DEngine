package main.components;

import main.components.physicsComponent.BoxCollider;
import main.components.stateMachine.StateMachine;
import main.editor.InactiveInEditor;
import main.haspid.*;
import main.physics.Physics2D;
import main.components.physicsComponent.RigidBody;
import main.physics.RayCastInfo;
import main.renderer.DebugDraw;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;
import org.joml.Vector3f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component implements InactiveInEditor {

    private boolean onGround;
    private double startVelYM;
    private double startVelXM;
    private double thresholdLS;
    private double frictionLS;
    private double startVelYLS;
    private double speedScalarLS;
    private double speedScalarM;
    private int resetGainIterations;
    private int gainHeightIterations;

    private Vector2d terminalVelocity;
    private transient Vector2d velocity;
    private transient double playerWidth;
    private transient RigidBody rigidBody;

    private transient Physics2D physics;
    private transient KeyListener keyboard;

    private double dieDistance;
    private double dieVelocity;
    private transient boolean top;
    private transient boolean die;
    private transient double maxDiePosY;
    private transient double dieStartPosY;

    private boolean isHurt;
    private double hurtTime;
    private double resetHurtTime;
    private double hurtThreshold;
    private double resetHurtThreshold;

    private transient boolean goingRight;
    private transient PlayerState playerState;
    private StateMachine stateMachine;

    public enum PlayerState{
        small,
        big,
        fire
    }

    public PlayerController(){
        // hurt
        this.resetHurtTime = 5;
        this.hurtTime = resetHurtTime;
        this.resetHurtThreshold = 0.05;
        this.hurtThreshold = resetHurtThreshold;

        // die
        this.dieDistance = 20;
        this.dieVelocity = 30;

        // movind left < > right
        this.startVelXM = 2;
        this.startVelYM = 120;
        this.speedScalarM = 1.5;
        this.playerWidth = objectHalfSize * 2;

        // in air
        this.resetGainIterations = 15;

        // friction
        this.thresholdLS = 0.4;
        this.frictionLS = 0.5;
        this.startVelYLS = -1;
        this.speedScalarLS = 1.1;

        this.velocity = new Vector2d();
        this.terminalVelocity = new Vector2d(20, 50);

        this.goingRight = true;
        this.playerState = PlayerState.small;

        this.keyboard = KeyListener.getInstance();
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
    }

    @Override
    public PlayerController copy(){
        PlayerController playerController = new PlayerController();
        playerController.setStartVelXM(startVelXM);
        playerController.setStartVelYM(startVelYM);
        playerController.setSpeedScalarM(speedScalarM);
        playerController.setPlayerWidth(playerWidth);

        playerController.setResetGainIterations(resetGainIterations);

        playerController.setThresholdLS(thresholdLS);
        playerController.setFrictionLS(frictionLS);
        playerController.setStartVelYLS(startVelYLS);
        playerController.setSpeedScalarLS(speedScalarLS);
        playerController.setTerminalVelocity(terminalVelocity);

        return playerController;
    }

    @Override
    public void start(){
        rigidBody = getParent().getComponent(RigidBody.class);
        stateMachine = getParent().getComponent(StateMachine.class);
        if(rigidBody != null) {
            rigidBody.setGravityScale(0f);
        }
    }

    @Override
    public void update(float dt) {
        if(rigidBody == null) return;
            if(isHurt) hurt(dt);

            if(die) {
                dieAnimation();
            }
            else{
                checkIfOnGround();
                if (keyboard.isKeyPressed(GLFW_KEY_UP)) {
                    move(Direction.Up);
                } else if (keyboard.isKeyPressed(GLFW_KEY_DOWN)) {
                    move(Direction.Down);
                } else if (keyboard.isKeyPressed(GLFW_KEY_RIGHT)) {
                    move(Direction.Right);
                } else if (keyboard.isKeyPressed(GLFW_KEY_LEFT)) {
                    move(Direction.Left);
                }

                loseSpeed();
                resolveAnimation();

                velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
                velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
                rigidBody.setVelocity(velocity);
                rigidBody.setAngularVelocity(0);
            }
    }

    public void move(Direction direction){
        Vector2d scale = getParent().getTransform().getScale();

        switch (direction){
            case Up -> {
                if(onGround){
                    gainHeightIterations = resetGainIterations;
                    velocity.y = startVelYM;
                    AssetPool.getSound(jumpSmall).play();
                }
            }
            case Down -> {

            }
            case Right -> {
                if(!goingRight){
                    goingRight = true;
                    stateMachine.switchAnimation("switch");
                }

                scale.x = playerWidth;
                if(velocity.x <= 0){
                    velocity.x = startVelXM;
                }else{
                    velocity.x *= speedScalarM;
                }
            }
            case Left -> {
                if(goingRight){
                    goingRight = false;
                    stateMachine.switchAnimation("switch");
                }

                scale.x = -playerWidth;
                if(velocity.x >= 0){
                    velocity.x = -startVelXM;
                }else{
                    velocity.x *= speedScalarM;
                }
            }
        }
    }

    public void resolveAnimation(){
        if(!stateMachine.getNextAnimationTitle().equals("switch")) {
            if (velocity.x == 0 && velocity.y == 0) {
                stateMachine.switchAnimation("idle");
            } else if (Math.abs(velocity.x) > 0 && velocity.y == 0) {
                stateMachine.switchAnimation("run");
            } else if (Math.abs(velocity.y) > 0) {
                stateMachine.switchAnimation("jump");
            }
        }
    }

    public void loseSpeed(){

        // Linear X
        if(velocity.x >= thresholdLS){
            velocity.x -= frictionLS;
        }else if(velocity.x <= -thresholdLS){
            velocity.x += frictionLS;
        }else{
            velocity.x = 0;
        }

        // Linear Y
        if(onGround && !gainingHeight()){
            velocity.y = 0;
        }else if(!gainingHeight()){
            if(velocity.y >= 0){
                velocity.y = startVelYLS;
            }else{
                velocity.y *= speedScalarLS;
            }
        }

        if(gainHeightIterations >= 0) gainHeightIterations--;
    }


    public boolean checkIfOnGround(){
        Transform t = getParent().getTransform();
        Vector2d pos = t.getPosition();
        Vector2d scale = t.getScale();
        double minusX = (scale.x / 2) - (scale.x / 10);
        double minusY = (scale.y / 2) + (scale.y / 10);

        Vector2d beginLeft = new Vector2d(pos.x - minusX, pos.y);
        Vector2d endLeft = new Vector2d(pos.x - minusX, pos.y - minusY);
        DebugDraw.addLine2D(10, beginLeft, endLeft, new Vector3f(0, 0, 1));
        RayCastInfo leftSideInfo = physics.rayCastInfo(getParent(), beginLeft, endLeft);

        Vector2d beginRight = new Vector2d(pos.x + minusX, pos.y);
        Vector2d endRight = new Vector2d(pos.x + minusX, pos.y - minusY);
        DebugDraw.addLine2D(10, beginRight, endRight, new Vector3f(0, 0, 1));
        RayCastInfo rightSideInfo = physics.rayCastInfo(getParent(), beginRight, endRight);

        return onGround = leftSideInfo.isHit() && leftSideInfo.getHitObject() != null || rightSideInfo.isHit() && rightSideInfo.getHitObject() != null;
    }

//    public void powerUP(){
//        if(playerState == PlayerState.fire) return;
//        AssetPool.getSound(powerUp).play();
//        increasePlayerState();
//
//        Window.getInstance().getCurrentScene().removeComponentRuntime(getParent(), stateMachine);
//
//        if(playerState == PlayerState.big) {
//            Vector2d scale = getParent().getTransform().getScale();
//            scale.set( new Vector2d(scale.x, scale.y * 2));
//            BoxCollider boxCollider = getParent().getComponent(BoxCollider.class);
//            boxCollider.setHalfSize(new Vector2d(objectHalfSize, objectHalfSize * 2));
//            boxCollider.resetFixture();
//            stateMachine = AssetPool.getStateMachine("bigMario");
//        }else if(playerState == PlayerState.fire){
//            stateMachine = AssetPool.getStateMachine("fireMario");
//        }
//
//        getParent().addComponent(stateMachine);
//    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2d contactNormal){
        double threshold = 0.8;

        if(Math.abs(contactNormal.x) > threshold){
            velocity.x = 0;
        }

        if(Math.abs(contactNormal.y) > threshold){
            velocity.y = 0;
            gainHeightIterations = 0;
        }
    }

    public boolean gainingHeight(){
        return gainHeightIterations >= 0;
    }

    public void powerUP(){
        if(playerState == PlayerState.fire) return;
        AssetPool.getSound(powerUp).play();
        Window.getInstance().getCurrentScene().removeComponentRuntime(getParent(), stateMachine);

        if(playerState == PlayerState.small){
            Vector2d scale = getParent().getTransform().getScale();
            scale.set( new Vector2d(scale.x, scale.y * 2));
            BoxCollider boxCollider = getParent().getComponent(BoxCollider.class);
            boxCollider.setHalfSize(new Vector2d(objectHalfSize, objectHalfSize * 2));
            boxCollider.resetFixture();
            stateMachine = AssetPool.getStateMachine("bigMario");
            playerState = PlayerState.big;
        } else if(playerState == PlayerState.big){
            stateMachine = AssetPool.getStateMachine("fireMario");
            playerState = PlayerState.fire;
        }

        getParent().addComponent(stateMachine);
    }

    public void powerDown(){
        Window.getInstance().getCurrentScene().removeComponentRuntime(getParent(), stateMachine);
        if(playerState == PlayerState.fire){
            playerState = PlayerState.big;
            stateMachine = AssetPool.getStateMachine("bigMario");
            getParent().addComponent(stateMachine);
            AssetPool.getSound(pipe).play();
        } else if(playerState == PlayerState.big){
            playerState = PlayerState.small;
            stateMachine = AssetPool.getStateMachine("smallMario");
            getParent().addComponent(stateMachine);
            AssetPool.getSound(pipe).play();

            Vector2d scale = getParent().getTransform().getScale();
            scale.set( new Vector2d(scale.x, scale.y / 2));
            BoxCollider boxCollider = getParent().getComponent(BoxCollider.class);
            boxCollider.setHalfSize(new Vector2d(objectHalfSize, objectHalfSize / 2));
            boxCollider.resetFixture();
        }else if(playerState == PlayerState.small){
            die();
        }

        isHurt = true;
        rigidBody.setSensor(true);
    }

    public void die(){
        AssetPool.getSound(marioDie).play();
        stateMachine = AssetPool.getStateMachine("dieMario");
        getParent().addComponent(stateMachine);

        die = true;


        dieStartPosY = getParent().getTransform().getPosition().y;
        maxDiePosY = dieStartPosY + dieDistance;
    }

    public void dieAnimation(){
        Vector2d pos = getParent().getTransform().getPosition();

        if (pos.y < maxDiePosY && !top) {
            rigidBody.setVelocity(new Vector2d(0, dieVelocity));
        } else if (!top) {
            rigidBody.setVelocity(new Vector2d(0, -dieVelocity));
            top = true;
        }else if(pos.y < dieStartPosY){
            Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
        }
    }

    public void hurt(float dt){
        SpriteRenderer spriteRenderer = getParent().getComponent(SpriteRenderer.class);

        if((hurtTime -= dt) < 0){
            isHurt = false;
            rigidBody.setSensor(false);
            hurtTime = resetHurtTime;
            spriteRenderer.getColor().w = 1;
        }else{
            if((hurtThreshold -= dt) < 0){
                hurtThreshold = resetHurtThreshold;
                if(spriteRenderer.getColor().w == 0){
                    spriteRenderer.getColor().w = 1;
                }else{
                    spriteRenderer.getColor().w = 0;
                }
            }
        }
    }

    public double getStartVelYM() {
        return startVelYM;
    }

    public void setStartVelYM(double startVelYM) {
        this.startVelYM = startVelYM;
    }

    public double getStartVelXM() {
        return startVelXM;
    }

    public void setStartVelXM(double startVelXM) {
        this.startVelXM = startVelXM;
    }

    public double getThresholdLS() {
        return thresholdLS;
    }

    public void setThresholdLS(double thresholdLS) {
        this.thresholdLS = thresholdLS;
    }

    public double getFrictionLS() {
        return frictionLS;
    }

    public void setFrictionLS(double frictionLS) {
        this.frictionLS = frictionLS;
    }

    public double getStartVelYLS() {
        return startVelYLS;
    }

    public void setStartVelYLS(double startVelYLS) {
        this.startVelYLS = startVelYLS;
    }

    public double getSpeedScalarLS() {
        return speedScalarLS;
    }

    public void setSpeedScalarLS(double speedScalarLS) {
        this.speedScalarLS = speedScalarLS;
    }

    public double getSpeedScalarM() {
        return speedScalarM;
    }

    public void setSpeedScalarM(double speedScalarM) {
        this.speedScalarM = speedScalarM;
    }

    public int getResetGainIterations() {
        return resetGainIterations;
    }

    public void setResetGainIterations(int resetGainIterations) {
        this.resetGainIterations = resetGainIterations;
    }

    public Vector2d getTerminalVelocity() {
        return terminalVelocity;
    }

    public void setTerminalVelocity(Vector2d terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
    }

    public double getPlayerWidth() {
        return playerWidth;
    }

    public void setPlayerWidth(double playerWidth) {
        this.playerWidth = playerWidth;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setSensor(boolean isSensor){
        rigidBody.setSensor(isSensor);
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public boolean isHurt(){
        return isHurt;
    }
}
