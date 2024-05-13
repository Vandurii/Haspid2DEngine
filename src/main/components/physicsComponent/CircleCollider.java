package main.components.physicsComponent;

import main.renderer.DebugDraw;
import org.joml.Vector2f;

import static main.Configuration.colliderIndex;

public class CircleCollider extends Collider {
    private float radius;

    public CircleCollider(float radius){
        this.radius = radius;
    }

    @Override
    public void update(float dt){
        Vector2f pos = getParent().getTransform().getPosition();
        Vector2f offset = getOffset();
        Vector2f center = new Vector2f(pos.x + offset.x, pos.y + offset.y);
        DebugDraw.drawCircle2D(center, radius, colliderIndex);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
