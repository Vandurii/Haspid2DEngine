package main.components.physicsComponent;

import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.CircleCollider;
import main.components.physicsComponent.Collider;
import main.components.physicsComponent.RigidBody;
import main.editor.JImGui;
import main.haspid.GameObject;
import main.haspid.Window;
import main.physics.Physics2D;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static main.Configuration.pillboxHeight;
import static main.Configuration.pillboxWidth;

public class PillboxCollider extends Collider {
    private double width;
    private double height;

    private transient BoxCollider boxCollider;
    private transient CircleCollider topCircle;
    private transient CircleCollider bottomCircle;
    private transient boolean resetFixtureNextFrame;

    private transient Physics2D physics;

    public PillboxCollider(){
        this.width = pillboxWidth;
        this.height = pillboxHeight;
        this.boxCollider = new BoxCollider(new Vector2d(0));
        this.topCircle = new CircleCollider(0);
        this.bottomCircle = new CircleCollider(0);

        this.physics = Window.getInstance().getCurrentScene().getPhysics();
        recalculateColliders();
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

    @Override
    public void updateColliderLines() {

    }

    @Override
    public boolean resize() {
        return false;
    }

    @Override
    public void dearGui(){
        super.dearGui();
        float w = (float)JImGui.drawValue("width", width, this.hashCode() + "");
        float h = (float)JImGui.drawValue("height", height, this.hashCode() + "");
        setWidth(w);
        setHeight(h);
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
        Vector2d offset = getOffset();
        double circleRadius = width / 4f;
        double boxHeight = height - 2 * circleRadius;

        topCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2d(offset.x, offset.y + (boxHeight / 4f)));

        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2d(offset.x, offset.y - (boxHeight / 4f)));

        boxCollider.setHalfSize(new Vector2d(width / 2.f, boxHeight / 2f));
        boxCollider.setOffset(offset);
    }

    public void setWidth(double width){
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(double height){
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
