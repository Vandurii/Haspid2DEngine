package main.physics.components;

import main.components.Component;
import main.haspid.Window;
import main.physics.enums.BodyType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

public class RigidBody extends Component {

    private float mass = 0;
    private boolean isSensor;
    private float friction = 1.f;
    private float angularVelocity;
    private float gravityScale = 1f;
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private transient Body rawBody = null;
    private boolean fixedRotation = false;
    private boolean continousCollision = true;
    private Vector2f velocity = new Vector2f();
    private BodyType bodyType = BodyType.Dynamic;

    private Window window;

    public RigidBody(){
        this.window = Window.getInstance();
    }

    @Override
    public void update(float dt) {
        if(Window.getInstance().getCurrentScene().isInEditMode()) return;

        if(rawBody != null){
            getParent().getTransform().setPosition(rawBody.getPosition().x, rawBody.getPosition().y);
            getParent().getTransform().setRotation((float) Math.toDegrees(rawBody.getAngle()));
        }
    }

    public void addVelocity(Vector2f velocity){
        if(rawBody != null) rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
    }

    public void addImpulse(Vector2f impulse){
        if(rawBody != null) rawBody.applyLinearImpulse(new Vec2(impulse.x, impulse.y), rawBody.getWorldCenter());
    }

    public boolean isSensor(){
        return isSensor;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if(rawBody != null) rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if(rawBody != null) rawBody.setAngularVelocity(angularVelocity);
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if(rawBody != null) rawBody.setGravityScale(gravityScale);
    }

    public void setSensor(boolean value){
        isSensor = value;
     //todo   if(rawBody != null) window.getCurrentScene().getPhysics().setSensor(this);
    }

    public float getFriction(){
        return friction;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
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

    public boolean isContinousCollision() {
        return continousCollision;
    }

    public void setContinousCollision(boolean continousCollision) {
        this.continousCollision = continousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public float getAngularVelocity(){
        return angularVelocity;
    }

    public float getGravityScale(){
        return gravityScale;
    }
}
