package main.components.physicsComponent;

import main.haspid.Scene;
import main.haspid.Transform;
import main.haspid.Window;
import main.physics.Physics2D;
import main.renderer.DebugDraw;
import main.renderer.Line2D;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.Garbage;
import static main.renderer.DrawMode.Dynamic;

public class BoxCollider extends Collider {
    private Vector2d center;
    private Vector2d halfSize;
    private transient Scene scene;
    private transient Physics2D physics;
    private transient boolean resetFixtureNextFrame;

    public BoxCollider(Vector2d halfSize){
        this.halfSize = halfSize;
        this.center = new Vector2d();
        this.scene = Window.getInstance().getCurrentScene();
    }

    @Override
    public void init(){
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
    }

    @Override
    public void update(float dt){
        Transform newTransform = getParent().getTransform();
        center = new Vector2d(newTransform.getPosition()).add(getOffset());

        if(getParent().isDirty()){
            List<Line2D> lineList = getParent().getAllCompThisType(Line2D.class);

            for(Line2D line: lineList){
                line.markToRemove(true);
                Window.getInstance().getCurrentScene().removeComponentSafe(getParent(), line);
            }

            DebugDraw.notify(Garbage, colliderID);
            DebugDraw.addBox(center, new Vector2d(halfSize.x * 2, halfSize.y * 2), newTransform.getRotation(), colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
        }

        if(resetFixtureNextFrame) resetFixture();
    }

    public BoxCollider copy(){
        BoxCollider boxCollider = new BoxCollider(halfSize);
        boxCollider.setOffset(getOffset());
        boxCollider.setCenter(center);

        return boxCollider;
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

    @Override
    public void dearGui(){
        super.dearGui();
        dearGui(this);
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof BoxCollider box)) return false;

        return this.center == box.getCenter() && this.halfSize.x == box.halfSize.x && this.halfSize.y == box.halfSize.y;
    }

    public Vector2d getHalfSize(){
        return halfSize;
    }

    public void setHalfSize(Vector2d halfSize){
        this.halfSize = new Vector2d(halfSize);
    }

    public void setCenter(Vector2d center) {
        this.center = new Vector2d(center);
    }

    public Vector2d getCenter() {
        return center;
    }
}
