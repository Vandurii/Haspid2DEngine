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
        DebugDraw.addCircle(pos, radius, colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
    }

    @Override
    public boolean resize() {
        if(lastRadius != radius){
            lastRadius = radius;
            resetFixture();
            return true;
        }

        return false;
    }

    @Override
    public CircleCollider copy(){
        return new CircleCollider(radius);
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
