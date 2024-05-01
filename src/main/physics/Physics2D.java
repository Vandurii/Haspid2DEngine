package main.physics;

import main.Helper;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.physics.components.BoxCollider;
import main.physics.components.CircleCollider;
import main.physics.components.RigidBody;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

import java.awt.event.HierarchyListener;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10);
    private World world = new World(gravity);
    private float physicsTime = 0f;
    private float physicsTimeStep = 1f / 60f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void update(float dt){
        physicsTime += dt;
        if(physicsTime >= 0f){
            physicsTime -= physicsTimeStep;
            world.step(physicsTime, velocityIterations, positionIterations); //todo
        }
    }

    public void add(GameObject gameObject){
        RigidBody rigidBody = gameObject.getComponent(RigidBody.class);
        if(Helper.isNotNull(rigidBody) && Helper.isNull(rigidBody.getRawBody())){
            Transform transform = gameObject.getTransform();

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.getRotation());
            bodyDef.position = new Vec2(transform.getPosition().x, transform.getPosition().y);
            bodyDef.angularDamping = rigidBody.getAngularDamping();
            bodyDef.linearDamping = rigidBody.getLinearDamping();
            bodyDef.fixedRotation = rigidBody.isFixedRotation();
            bodyDef.bullet = rigidBody.isContinousCollision();

            switch (rigidBody.getBodyType()){
                case Kinematic -> bodyDef.type = BodyType.KINEMATIC;
                case Static -> bodyDef.type = BodyType.STATIC;
                case Dynamic -> bodyDef.type = BodyType.DYNAMIC;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider = gameObject.getComponent(CircleCollider.class);
            BoxCollider boxCollider = gameObject.getComponent(BoxCollider.class);

            if(Helper.isNotNull(circleCollider)) shape.setRadius(circleCollider.getRadius());
            if(Helper.isNotNull(boxCollider)){
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = boxCollider.getOrigin();
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = world.createBody(bodyDef);
            rigidBody.setRawBody(body);
            body.createFixture(shape, rigidBody.getMass());
        }
    }

    public Vec2 getGravity() {
        return gravity;
    }

    public void setGravity(Vec2 gravity) {
        this.gravity = gravity;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public float getPhysicsTime() {
        return physicsTime;
    }

    public void setPhysicsTime(float physicsTime) {
        this.physicsTime = physicsTime;
    }

    public float getPhysicsTimeStep() {
        return physicsTimeStep;
    }

    public void setPhysicsTimeStep(float physicsTimeStep) {
        this.physicsTimeStep = physicsTimeStep;
    }

    public int getVelocityIterations() {
        return velocityIterations;
    }

    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public int getPositionIterations() {
        return positionIterations;
    }

    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }
}
