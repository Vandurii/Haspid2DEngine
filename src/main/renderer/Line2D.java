package main.renderer;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private int zIndex;
    private Vector2d to;
    private int lifeTime;
    private float stroke;
    private Vector2d from;
    private Vector3f color;

    public Line2D(int zIndex, Vector2d from, Vector2d to, Vector3f color, int lifeTime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.zIndex = zIndex;
        this.lifeTime = lifeTime;
    }

    public int beginFrame(){
        lifeTime--;
        return lifeTime;
    }

    public Vector2d getFrom() {
        return from;
    }

    public Vector2d getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public float getStroke() {
        return stroke;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
    }

    public int getzIndex() {
        return zIndex;
    }
}
