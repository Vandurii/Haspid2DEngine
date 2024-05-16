package main.components.physicsComponent;

import main.editor.JImGui;
import main.haspid.Transform;
import main.renderer.DebugDraw;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static main.Configuration.colliderIndex;
import static main.Configuration.colliderColor;

public class BoxCollider extends Collider {
    private Vector2d halfSize;
    private Vector2d origin; // todo why i need it?
    private Vector2d center;

    public BoxCollider(Vector2d halfSize){
        this.halfSize = halfSize;
        this.origin = new Vector2d();
        this.center = new Vector2d();
    }

    @Override
    public void update(float dt){
        Transform t = getParent().getTransform();
        center = new Vector2d(t.getPosition()).add(getOffset());
        DebugDraw.drawBoxes2D(colliderIndex, center, new Vector2d(halfSize.x * 2, halfSize.y * 2), t.getRotation(), colliderColor, 1 );
    }

    public BoxCollider copy(){
        BoxCollider boxCollider = new BoxCollider(halfSize);
        boxCollider.setOffset(getOffset());
        boxCollider.setOrigin(origin);
        boxCollider.setCenter(center);

        return boxCollider;
    }

    @Override
    public void dearGui(){
        super.dearGui();
        dearGui(this);
    }

    public Vector2d getHalfSize(){
        return halfSize;
    }

    public void setHalfSize(Vector2d halfSize){
        this.halfSize = new Vector2d(halfSize);
    }

    public Vector2d getOrigin(){
        return origin;
    }

    public void setOrigin(Vector2d origin) {
        this.origin = new Vector2d(origin);
    }

    public void setCenter(Vector2d center) {
        this.center = new Vector2d(center);
    }
}
