package main.renderer;

import org.joml.Vector3f;

public class Line2D {
    private Vector3f from;
    private Vector3f to;
    private Vector3f color;
    private int lifeTime;

    public Line2D(Vector3f from, Vector3f to, Vector3f color, int lifeTime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifeTime = lifeTime;
    }

    public int beginFrame(){
        lifeTime--;
        return lifeTime;
    }

    public Vector3f getFrom() {
        return from;
    }

    public Vector3f getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }

    public int getLifeTime() {
        return lifeTime;
    }
}
