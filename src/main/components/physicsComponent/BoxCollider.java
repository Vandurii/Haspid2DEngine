package main.components.physicsComponent;

import main.haspid.Transform;
import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;

public class BoxCollider extends Collider {
    private Vector2d center;
    private Vector2d halfSize;
    private transient Vector2d lastHalfSize;

    public BoxCollider(Vector2d halfSize){
        this.halfSize = halfSize;
        this.center = new Vector2d();
        this.lastHalfSize = new Vector2d(halfSize.x, halfSize.y);
    }

    @Override
    public void init(){
        // call super method so that it initialize scene and physic
        super.init();
        this.lastHalfSize = new Vector2d(halfSize.x, halfSize.y);
    }

    @Override
    public void updateColliderLines() {
        Transform t = getParent().getTransform();
        center = new Vector2d(t.getPosition()).add(getOffset());
        DebugDraw.addBox(center, new Vector2d(halfSize.x * 2, halfSize.y * 2), t.getRotation(), colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
    }

    @Override
    public boolean resize() {
        if(lastHalfSize.x != halfSize.x || lastHalfSize.y != halfSize.y){
            lastHalfSize = new Vector2d(halfSize.x, lastHalfSize.y);
            resetFixture();
            return true;
        }

        return false;
    }

    @Override
    public BoxCollider copy(){
        BoxCollider boxCollider = new BoxCollider(halfSize);
        boxCollider.setOffset(getOffset());
        boxCollider.setCenter(center);

        return boxCollider;
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
