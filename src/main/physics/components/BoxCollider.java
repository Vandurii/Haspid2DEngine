package main.physics.components;

import main.haspid.Transform;
import main.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;

import static main.Configuration.colliderColor;

public class BoxCollider extends Collider{
    private Vector2f halfSize = new Vector2f(1f);
    private Vector2f origin = new Vector2f();
    private Vector2f center = new Vector2f(0, 0);

    @Override
    public void update(float dt){
        Transform t = getParent().getTransform();
        Vector2f pos = t.getPosition();
        Vector2f scale = t.getScale();

        center = new Vector2f(pos).add(getOffset());
        DebugDraw.drawBoxes2D(center, new Vector2f( scale.x + 1, scale.y + 1), t.getRotation(), colliderColor, 1 );
    }

    public Vector2f getHalfSize(){
        return halfSize;
    }

    public Vector2f getOrigin(){
        return origin;
    }
}
