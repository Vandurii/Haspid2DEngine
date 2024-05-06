package main.physics.components;

import main.components.Component;
import main.haspid.Window;
import main.physics.enums.BodyType;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

public class RigidBody extends Component {
    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic;

    private boolean fixedRotation = false;
    private boolean continousCollision = true;

    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if(Window.getInstance().getCurrentScene().isInEditMode()) return;

        if(rawBody != null){
            getParent().getTransform().setPosition(rawBody.getPosition().x, rawBody.getPosition().y);
            getParent().getTransform().setRotation(rawBody.getAngle());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
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
}
