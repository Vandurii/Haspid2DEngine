package main.physics.components;

import main.haspid.Transform;
import main.renderer.DebugDraw;
import org.joml.Vector2f;

import static main.Configuration.colliderIndex;
import static main.Configuration.colliderColor;

public class BoxCollider extends Collider{
    private Vector2f halfSize;
    private Vector2f origin; // todo why i need it?
    private Vector2f center;

    public BoxCollider(Vector2f halfSize){
        this.halfSize = halfSize;
        this.origin = new Vector2f();
        this.center = new Vector2f();
    }

    @Override
    public void update(float dt){
        Transform t = getParent().getTransform();
        center = new Vector2f(t.getPosition()).add(getOffset());
        DebugDraw.drawBoxes2D(colliderIndex, center, new Vector2f(halfSize.x * 2, halfSize.y * 2), t.getRotation(), colliderColor, 1 );
    }

    public Vector2f getHalfSize(){
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize){
        this.halfSize = new Vector2f(halfSize);
    }

    public Vector2f getOrigin(){
        return origin;
    }
}
