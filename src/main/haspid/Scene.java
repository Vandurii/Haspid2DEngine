package main.haspid;

public abstract class Scene {

    protected Camera camera;

    public abstract void update(float dt);

    public abstract void init();

}
