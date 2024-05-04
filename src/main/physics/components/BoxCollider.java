package main.physics.components;

import main.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;

public class BoxCollider extends Collider{
    private Vector2f halfSize = new Vector2f(1f);
    private Vector2f origin = new Vector2f();
    private Vector2f center = new Vector2f();

    @Override
    public void update(float dt){
        center = new Vector2f(getParent().getTransform().getPosition()).add(getOffset());
        DebugDraw.drawBoxes2D(center, halfSize, getParent().getTransform().getRotation(), new Vector3f(1, 0, 0), 5);
    }

    public Vector2f getHalfSize(){
        return halfSize;
    }

    public Vector2f getOrigin(){
        return origin;
    }
}
2