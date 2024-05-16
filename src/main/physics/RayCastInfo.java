package main.physics;

import main.haspid.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class RayCastInfo implements RayCastCallback {
    private boolean hit;
    private float fraction;
    private Vector2d point;
    private Vector2d normal;
    private Fixture fixture;
    private GameObject hitObject;
    private GameObject requestingObject;

    public RayCastInfo(GameObject gameObject){
        this.point = new Vector2d();
        this.normal = new Vector2d();
        this.requestingObject = gameObject;
    }

    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        if(fixture.m_userData == requestingObject) return 1;

        this.fixture = fixture;
        this.hit = fraction != 0;
        this.fraction = fraction;
        this.point = new Vector2d(point.x, point.y);
        this.normal = new Vector2d(normal.x, normal.y);
        this.hitObject = (GameObject) fixture.m_userData;

        return fraction;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public float getFraction() {
        return fraction;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
    }

    public Vector2d getPoint() {
        return point;
    }

    public void setPoint(Vector2d point) {
        this.point = point;
    }

    public Vector2d getNormal() {
        return normal;
    }

    public void setNormal(Vector2d normal) {
        this.normal = normal;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public GameObject getHitObject() {
        return hitObject;
    }

    public void setHitObject(GameObject hitObject) {
        this.hitObject = hitObject;
    }

    public GameObject getRequestingObject() {
        return requestingObject;
    }

    public void setRequestingObject(GameObject requestingObject) {
        this.requestingObject = requestingObject;
    }
}
