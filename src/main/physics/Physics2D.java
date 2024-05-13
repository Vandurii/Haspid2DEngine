package main.physics;

import main.components.physicsComponent.*;
import main.haspid.GameObject;
import main.haspid.Transform;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import org.jbox2d.dynamics.BodyType;

public class Physics2D {
    private Vec2 gravity;
    private World world;

    private float physicsTime;
    private float physicsTimeStep;
    private int velocityIterations;
    private int positionIterations;

    public Physics2D(){
        this.positionIterations = 3;
        this.velocityIterations = 8;
        this.physicsTimeStep = 1f / 60f;

        this.gravity = new Vec2(0, -10);
        this.world = new World(gravity);
        this.world.setContactListener(new HContactListener());
    }


    public void update(float dt){
        physicsTime += dt;
        if(physicsTime >= 0f){
            physicsTime -= physicsTimeStep;
            world.step(physicsTime, velocityIterations, positionIterations); //todo
        }
    }

    public void add(GameObject gameObject){
        RigidBody rigidBody = gameObject.getComponent(RigidBody.class);
        if(rigidBody != null && rigidBody.getRawBody() != null){
            Transform transform = gameObject.getTransform();

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.getRotation());
            bodyDef.position.set(transform.getPosition().x, transform.getPosition().y);
            bodyDef.angularDamping = rigidBody.getAngularDamping();
            bodyDef.linearDamping = rigidBody.getLinearDamping();
            bodyDef.fixedRotation = rigidBody.isFixedRotation();
            bodyDef.bullet = rigidBody.isContinuousCollision();
            bodyDef.gravityScale = rigidBody.getGravityScale();
            bodyDef.angularVelocity = rigidBody.getAngularVelocity();
            bodyDef.userData = rigidBody.getParent();

            switch (rigidBody.getBodyType()){
                case Kinematic -> bodyDef.type = BodyType.KINEMATIC;
                case Static -> bodyDef.type = BodyType.STATIC;
                case Dynamic -> bodyDef.type = BodyType.DYNAMIC;
            }

            Body body = world.createBody(bodyDef);
            body.m_mass = rigidBody.getMass();
            rigidBody.setRawBody(body);

            CircleCollider circleCollider = gameObject.getComponent(CircleCollider.class);
            BoxCollider boxCollider = gameObject.getComponent(BoxCollider.class);
            PillboxCollider pillboxCollider = gameObject.getComponent(PillboxCollider.class);

            if(boxCollider != null) addBoxCollider(rigidBody, boxCollider);
            if(circleCollider != null) addCircleCollider(rigidBody, circleCollider);
            if(pillboxCollider != null) addPillboxCollider(rigidBody, pillboxCollider);
        }
    }

    public void addPillboxCollider(RigidBody rigidBody, PillboxCollider pillboxCollider){
        Body rawBody = rigidBody.getRawBody();

        if(rawBody != null) {
            addBoxCollider(rigidBody, pillboxCollider.getBoxCollider());
            addCircleCollider(rigidBody, pillboxCollider.getTopCircle());
            addCircleCollider(rigidBody, pillboxCollider.getBottomCircle());
        }
    }

    public void addCircleCollider(RigidBody rigidBody, CircleCollider circleCollider){
        Body rawBody = rigidBody.getRawBody();

        if(rawBody != null) {
            CircleShape shape = new CircleShape();
            shape.setRadius(circleCollider.getRadius());
            shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

            rawBody.createFixture(createFixtureDef(shape, rigidBody));
        }
    }

    public void addBoxCollider(RigidBody rigidBody, BoxCollider boxCollider){
        Body rawBody = rigidBody.getRawBody();

        if(rawBody != null) {
            PolygonShape shape = new PolygonShape();
            Vector2f halfSize = new Vector2f(boxCollider.getHalfSize());
            Vector2f offset = new Vector2f(boxCollider.getOffset());
            Vector2f origin = new Vector2f(boxCollider.getOrigin());
            shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

            rawBody.createFixture(createFixtureDef(shape, rigidBody));
        }
    }

    public FixtureDef createFixtureDef(Shape shape, RigidBody rigidBody){
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = rigidBody.getFriction();
        fixtureDef.userData = rigidBody.getParent();
        fixtureDef.isSensor = rigidBody.isSensor();

        return fixtureDef;
    }

    public void destroyGameObject(GameObject gameObject){
        RigidBody rb = gameObject.getComponent(RigidBody.class);
        if(rb != null && rb.getRawBody() != null){
            world.destroyBody(rb.getRawBody());
            rb.setRawBody(null);
        }
    }

    public RayCastInfo rayCastInfo(GameObject requestingObject, Vector2f point1, Vector2f point2){
        RayCastInfo callback = new RayCastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));

        return callback;
    }

    public void resetCollider(RigidBody rigidBody, Collider collider){
        Body body = rigidBody.getRawBody();

        if(body != null){
            int size = getFixtureListSize(body);
            for(int i = 0; i < size; i++){
                body.destroyFixture(body.getFixtureList());
            }

            if(collider instanceof BoxCollider boxCollider) {
                addBoxCollider(rigidBody, boxCollider);
            }else if(collider instanceof CircleCollider circleCollider){
                addCircleCollider(rigidBody, circleCollider);
            }else if(collider instanceof PillboxCollider pillboxCollider){
                addPillboxCollider(rigidBody, pillboxCollider);
            }

            body.resetMassData();
        }
    }

    public void setSensor(RigidBody rigidBody, boolean isSensor){
        Body rawBody = rigidBody.getRawBody();

        if(rawBody != null){
            Fixture fixture = rawBody.getFixtureList();
            while(fixture != null){
                fixture.m_isSensor = isSensor;
                fixture = fixture.m_next;
            }
        }
    }

    public int getFixtureListSize(Body body){
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while(fixture != null){
            size++;
            fixture = fixture.m_next;
        }

        return size;
    }

    public boolean isLocked(){
        return world.isLocked();
    }


    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
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
