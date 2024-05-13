package main.components.physicsComponent;

import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.CircleCollider;
import main.components.physicsComponent.Collider;
import main.components.physicsComponent.RigidBody;
import main.haspid.GameObject;
import main.haspid.Window;
import main.physics.Physics2D;
import org.joml.Vector2f;

public class PillboxCollider extends Collider {
    private float width;
    private float height;

    private transient BoxCollider boxCollider;
    private transient CircleCollider topCircle;
    private transient CircleCollider bottomCircle;
    private transient boolean resetFixtureNextFrame;

    private transient Physics2D physics;

    public PillboxCollider(){
        this.boxCollider = new BoxCollider(new Vector2f(0));
        this.topCircle = new CircleCollider(0);
        this.bottomCircle = new CircleCollider(0);

        this.physics = Window.getInstance().getCurrentScene().getPhysics();
    }

    @Override
    public void update(float dt){
        if(boxCollider.getParent() == null || topCircle.getParent() == null || bottomCircle.getParent() == null ){
            GameObject parent = getParent();
            boxCollider.setParent(parent);
            topCircle.setParent(parent);
            bottomCircle.setParent(parent);
            recalculateColliders();
        }

        topCircle.update(dt);
        bottomCircle.update(dt);
        boxCollider.update(dt);

        if(resetFixtureNextFrame) resetFixture();
    }

    public void resetFixture(){
        if(physics.isLocked()){
            resetFixtureNextFrame = true;
        }else{
            resetFixtureNextFrame = false;
            RigidBody rigidBody = getParent().getComponent(RigidBody.class);
            if(rigidBody != null) physics.resetCollider(rigidBody, this);
        }
    }

    public void recalculateColliders(){
        Vector2f offset = getOffset();
        float circleRadius = width / 4f;
        float boxHeight = height - 2 * circleRadius;

        topCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset.x, offset.y + (boxHeight / 4f)));

        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(offset.x, offset.y - (boxHeight / 4f)));

        boxCollider.setHalfSize(new Vector2f(width / 2.f, boxHeight / 2f));
        boxCollider.setOffset(offset);
    }

    public void setWidth(float width){
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height){
        this.height = height;
        recalculateColliders();
        resetFixture();
    }

    public BoxCollider getBoxCollider() {
        return boxCollider;
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }
}
