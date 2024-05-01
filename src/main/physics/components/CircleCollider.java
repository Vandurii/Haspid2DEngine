package main.physics.components;

public class CircleCollider extends Collider{
    private float radius = 1;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
