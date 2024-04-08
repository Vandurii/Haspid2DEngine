package main.haspid;

import main.components.Component;
import main.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private int gameObjectID;
    private String name;
    private Transform transform;
    private int zIndex;
    private List<Component> componentList;
    private static int ID_COUNTER;

    public GameObject(String name){
        this.componentList = new ArrayList<>();
        this.name = name;
        this.transform = new Transform();
        this.zIndex = 0;
        this.gameObjectID = ++ID_COUNTER;
    }

    public GameObject(String name, Transform transform, int zIndex){
        this.componentList = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;
        this.name = name;
        this.gameObjectID = ++ID_COUNTER;
    }

    public void addComponent(Component c){
        c.setParent(this);
        componentList.add(c);
    }

    public <T extends Component> T getComponent(Class<T> component){
        for(Component c: componentList){
            if(component.isAssignableFrom(c.getClass())) return component.cast(c);
        }

        return null;
    }

    public List<Component> getAllComponent(){
       return componentList;
    }

    public <T extends Component> boolean removeComponent(Class<T> component){
        for(int i = 0; i < componentList.size(); i++){
            Component c = componentList.get(i);
            if(component.isAssignableFrom(c.getClass())){
                componentList.remove(i);
                return true;
            }
        }

        return false;
    }

    public void start(){
        for(Component c: componentList){
            c.start();
        }
    }

    public void update(float dt){
        for(Component c: componentList){
            c.update(dt);
        }
    }

    public void dearGui(){
        for(Component c: componentList){
            c.dearGui();
        }
    }

    public Transform getTransform(){
        return transform;
    }

    public String getName(){
        return name;
    }

    public int getzIndex(){
        return zIndex;
    }

    public int getGameObjectID(){
        return gameObjectID;
    }

    public int getIDCounter(){
        return ID_COUNTER;
    }

}
