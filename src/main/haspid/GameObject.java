package main.haspid;

import main.components.Component;
import main.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private int gameObjectID;
    private String name;
    private Transform transform;

    private boolean isSerializable;
    private boolean isTriggerable;
    private List<Component> componentList;
    private static int ID_COUNTER;

    public GameObject(String name){
        this.componentList = new ArrayList<>();
        this.name = name;
        this.transform = new Transform();
        this.gameObjectID = ++ID_COUNTER;
        this.isSerializable = true;
        this.isTriggerable = true;
    }

    public GameObject(String name, Transform transform){
        this.componentList = new ArrayList<>();
        this.transform = transform;
        this.name = name;
        this.gameObjectID = ++ID_COUNTER;
        this.isSerializable = true;
        this.isTriggerable = true;
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

    public <T> void removeComponent(Class<T> component){

        for(int i = 0; i < componentList.size(); i++){
            Component c = componentList.get(i);
            if(component.isAssignableFrom(c.getClass())){
                System.out.println(componentList.size());
                componentList.remove(c);
                System.out.println("removed: " + component);
                System.out.println(componentList.size());
                return;
            }
        }
    }

    public void removeComponent(Component component){
        componentList.remove(component);
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

    public void destroyComponents(){
        componentList.clear();
    }

    public void setNonSerializable(){
        isSerializable = false;
    }

    public boolean isSerializable(){
        return isSerializable;
    }

    public void setNonTriggerable(){
        isTriggerable = false;
    }

    public boolean isTriggerable(){
        return isTriggerable;
    }

    public Transform getTransform(){
        return transform;
    }

    public String getName(){
        return name;
    }

    public int getGameObjectID(){
        return gameObjectID;
    }

    public int getIDCounter(){
        return ID_COUNTER;
    }
}
