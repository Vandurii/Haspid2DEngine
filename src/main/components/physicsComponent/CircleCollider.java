package main.components.physicsComponent;

import main.haspid.Transform;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.Line2D;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;

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

        if(getParent().isDirty()){
            List<Line2D> lineList = getParent().getAllCompThisType(Line2D.class);

            for(Line2D line: lineList){
                Window.getInstance().getCurrentScene().removeComponentSafe(getParent(), line);
            }

            DebugDraw.addCircle(center, radius, colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
        }

        if(resetFixtureNextFrame) resetFixture();
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
