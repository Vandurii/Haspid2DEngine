package main.physics.components;

import org.joml.Vector2f;

public class BoxCollider extends Collider{
    private Vector2f halfSize = new Vector2f(1f);
    private Vector2f origin = new Vector2f();

    public Vector2f getHalfSize(){
        return halfSize;
    }

    public Vector2f getOrigin(){
        return origin;
    }
}
