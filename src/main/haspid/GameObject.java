package main.haspid;

import main.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private List<Component> componentList;
    private String name;

    public GameObject(String name){
        componentList = new ArrayList<>();
        this.name = name;
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
}
