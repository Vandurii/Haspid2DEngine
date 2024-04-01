package main.components;

import main.haspid.GameObject;

public abstract class Component {

    private GameObject parentObject;

    public void setParent(GameObject gameObject){
        this.parentObject = gameObject;
    }


    public void start(){}

    public abstract void update(float dt);
}
