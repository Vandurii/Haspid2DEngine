package main.components;

import main.haspid.GameObject;

public abstract class Component {

    private transient GameObject parentObject;

    public void setParent(GameObject gameObject){
        this.parentObject = gameObject;
    }


    public void start(){}

    public void update(float dt){}

    public void dearGui(){
    }

    public GameObject getParent(){
        return parentObject;
    }


}
