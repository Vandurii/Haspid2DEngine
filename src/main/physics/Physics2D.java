package main.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

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
