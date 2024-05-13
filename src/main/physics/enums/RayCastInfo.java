package main.physics.enums;

import main.haspid.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RayCastInfo implements RayCastCallback {
    private boolean hit;
    private float fraction;
    private Vector2f point;
    private Vector2f normal;
    private Fixture fixture;
    private GameObject hitObject;
    private GameObject requestingObject;

    public RayCastInfo(GameObject gameObject){
        this.point = new Vector2f();
        this.normal = new Vector2f();
        this.requestingObject = gameObject;
    }

    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        if(fixture.m_userData == requestingObject) return 1;

        this.fixture = fixture;
        this.hit = fraction != 0;
        this.fraction = fraction;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.hitObject = (GameObject) fixture.m_userData;

        return fraction;
    }
}
