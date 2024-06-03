package main.components.physicsComponent;

import main.haspid.Transform;
import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;

public class BoxCollider extends Collider {
    private transient Vector2d pos;
    private transient Vector2d scale;
    private transient Vector2d lastScale;
    private transient Transform transform;

    @Override
    public void init(){
        // call super method so that it initialize scene and physic
        super.init();

        this.transform = getParent().getTransform();
        this.scale = transform.getScale();
        this.pos = transform.getPosition();
        this.transform = getParent().getTransform();
        this.lastScale = new Vector2d(scale.x, scale.y);
    }

    @Override
    public void updateColliderLines() {
        this.transform = getParent().getTransform();
        this.scale = transform.getScale();
        this.pos = transform.getPosition();

        DebugDraw.addBox(pos, scale, transform.getRotation(), colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
    }

    @Override
    public boolean resize() {
        if(lastScale.x != scale.x || lastScale.y != scale.y){
            lastScale = new Vector2d(scale.x, scale.y);
            System.out.println("reset fixture " + getParent().getName() );
            resetFixture();
            return true;
        }

        return false;
    }

    @Override
    public BoxCollider copy(){
        return new BoxCollider();
    }

    @Override
    public void dearGui(){
        super.dearGui();
        dearGui(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoxCollider box)) return false;

        return this.pos.x == box.pos.x && this.pos.y == box.pos.y && this.scale.x == box.scale.x && this.scale.y == box.scale.y;
    }
}
