package main.components.physicsComponent;

import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.colliderZIndex;

public class CircleCollider extends Collider {
    private double radius;

    public CircleCollider(double radius){
        this.radius = radius;
    }

    @Override
    public void update(float dt){
        Vector2d pos = getParent().getTransform().getPosition();
        Vector2d offset = getOffset();
        Vector2d center = new Vector2d(pos.x + offset.x, pos.y + offset.y);
        DebugDraw.drawCircle2D(center, radius, colliderZIndex);
    }

    @Override
    public CircleCollider copy(){
        CircleCollider circleCollider = new CircleCollider(radius);
        circleCollider.setOffset(getOffset());

        return circleCollider;
    }

    @Override
    public void dearGui(){
        super.dearGui();
        dearGui(this);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
