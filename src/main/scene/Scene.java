package main.scene;

import main.haspid.Camera;
import main.haspid.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning;
    private static List<GameObject> sceneObjectList;

    public Scene(){
        sceneObjectList = new ArrayList<>();
    }

    public abstract void update(float dt);

    public abstract void init();

    public void start(){
        for(GameObject gameObject: sceneObjectList){
            gameObject.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject){
        sceneObjectList.add(gameObject);
        if(!isRunning) gameObject.start();
    }
}
