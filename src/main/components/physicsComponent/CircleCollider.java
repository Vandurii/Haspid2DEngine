package main.components.physicsComponent;

import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;

public class CircleCollider extends Collider {
    private double radius;
    private transient double lastRadius;

    public CircleCollider(double radius){
        this.radius = radius;
    }

    @Override
    public void updateColliderLines(){
        Vector2d pos = getParent().getTransform().getPosition();
        Vector2d offset = getOffset();
        Vector2d center = new Vector2d(pos.x + offset.x, pos.y + offset.y);
        DebugDraw.addCircle(center, radius, colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
    }

    @Override
    public boolean resize() {
        if(lastRadius != radius){
            lastRadius = radius;
            return true;
        }

        return false;
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
