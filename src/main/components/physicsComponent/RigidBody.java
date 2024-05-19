package main.components.physicsComponent;

import main.components.Component;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.haspid.Window;
import main.physics.Physics2D;
import main.physics.BodyType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class RigidBody extends Component {

    private transient Window window;
    private transient Physics2D physics;

    private double mass;
    private double friction;
    private boolean isSensor;
    private Vector2d velocity;
    private BodyType bodyType;
    private double gravityScale;
    private double linearDamping;
    private double angularDamping;
    private double angularVelocity;
    private boolean fixedRotation;
    private transient Body rawBody;
    private boolean continuousCollision;

    private boolean updateRotation;

    public RigidBody(){
        this.friction = 0.1;
        this.gravityScale = 1;
        this.linearDamping = 0.9;
        this.angularDamping = 0.8;
        this.velocity = new Vector2d();
        this.continuousCollision = true;
        this.bodyType = BodyType.Dynamic;
        this.window = Window.getInstance();
        this.physics = window.getCurrentScene().getPhysics();
    }

    @Override
    public void update(float dt) {
        if(window.getCurrentScene().isInEditMode()) return;

        if(rawBody != null){
            if(bodyType == BodyType.Dynamic || bodyType == BodyType.Kinematic) {
                getParent().getTransform().setPosition(rawBody.getPosition().x, rawBody.getPosition().y);
                if (updateRotation) getParent().getTransform().setRotation((float) Math.toDegrees(rawBody.getAngle()));
            }else{
                Transform t = getParent().getTransform();
                rawBody.setTransform(new Vec2((float) t.getPosition().x, (float) t.getPosition().y), (float)t.getRotation());
            }
        }
    }

    public RigidBody copy(){
        RigidBody rigidBody = new RigidBody();
        rigidBody.setMass(mass);
        rigidBody.setSensor(isSensor);
        rigidBody.setFriction(friction);
        rigidBody.setVelocity(velocity);
        rigidBody.setBodyType(bodyType);
        rigidBody.setGravityScale(gravityScale);
        rigidBody.setLinearDamping(linearDamping);
        rigidBody.setFixedRotation(fixedRotation);
        rigidBody.setAngularDamping(angularDamping);
        rigidBody.setAngularVelocity(angularVelocity);
        rigidBody.setContinuousCollision(continuousCollision);

        return rigidBody;
    }

    public void addVelocity(Vector2d velocity){
        if(rawBody != null) rawBody.applyForceToCenter(new Vec2((float) velocity.x, (float) velocity.y));
    }

    public void addImpulse(Vector2d impulse){
        if(rawBody != null) rawBody.applyLinearImpulse(new Vec2((float) impulse.x, (float) impulse.y), rawBody.getWorldCenter());
    }

    public boolean isSensor(){
        return isSensor;
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity.set(velocity);
        if(rawBody != null) rawBody.setLinearVelocity(new Vec2((float) velocity.x, (float) velocity.y));
    }

    public void setVelocityX(double velocityX) {
        this.velocity.set(velocityX, velocity.y);
        if(rawBody != null) rawBody.setLinearVelocity(new Vec2((float) velocityX, (float) velocity.y));
    }

    public void setVelocityY(double velocityY) {
        this.velocity.set(velocity.x, velocityY);
        if(rawBody != null) rawBody.setLinearVelocity(new Vec2((float) velocity.x, (float) velocityY));
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
        if(rawBody != null) rawBody.setAngularVelocity((float) angularVelocity);
    }

    public void setGravityScale(double gravityScale) {
        this.gravityScale = gravityScale;
        if(rawBody != null) rawBody.setGravityScale((float) gravityScale);
    }

    public void setSensor(boolean isSensor){
        this.isSensor = isSensor;
        if(rawBody != null) physics.setSensor(this, isSensor);
    }

    public double getFriction(){
        return friction;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public double getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(double angularDamping) {
        this.angularDamping = angularDamping;
    }

    public double getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(double linearDamping) {
        this.linearDamping = linearDamping;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public double getAngularVelocity(){
        return angularVelocity;
    }

    public double getGravityScale(){
        return gravityScale;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public void setUpdateRotation(boolean updateRotation){
        this.updateRotation = updateRotation;
    }
}
