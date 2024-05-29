package main.haspid;

import imgui.ImGui;
import main.editor.InactiveInEditor;
import main.editor.JImGui;
import main.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private int gameObjectID;
    private transient boolean dirty;
    private List<Component> componentList;
    private transient Transform transform;
    private transient boolean isTriggerable;
    private transient boolean isSerializable;
    private transient Transform lastTransform;

    private static int ID_COUNTER;

    public GameObject(String name){
        this.name = name;
        this.dirty = true;
        this.isTriggerable = true;
        this.isSerializable = true;
        this.gameObjectID = ++ID_COUNTER;
        this.transform = new Transform();
        this.lastTransform = new Transform();
        this.componentList = new ArrayList<>();
    }

    public void init(){
        Console.addLog(new Log(Log.LogType.INFO, String.format("%s ID: %s", this, this.gameObjectID )));

        // init lastTransform
        lastTransform = transform.copy();

        // start all component from this object
        for(Component component: componentList){
            component.init();
        }
    }

    public void update(float dt){
        // set to dirty if the position has changed
        if (!lastTransform.equals(transform)) {
            dirty = true;
        }

        // update all component from this object
        for(Component component: componentList){

            // skip component if it shouldn't be active in editor mode
           if((component instanceof InactiveInEditor) && Window.getInstance().getCurrentScene().isInEditMode()){
               continue;
           }
             component.update(dt);
        }

        // update last transform
        transform.copy(lastTransform);
        dirty = false;
    }

    public void updateDearGui(){
        if(ImGui.collapsingHeader("Name")) setName();

        for(Component c: componentList){
            if(ImGui.collapsingHeader(c.getClass().getSimpleName())) c.dearGui();
        }
    }

    public GameObject copy(){
        GameObject clone = new GameObject(name);
        if(!isSerializable) clone.setNonSerializable();
        if(!isTriggerable) clone.setNonTriggerable();

        for(Component component: componentList){
            Component componentClone = component.copy();
            if(componentClone != null){
                clone.addComponent(componentClone);
            }
        }
        clone.setTransformFromItself();

        return clone;
    }

    public void addComponent(Component c){
        Console.addLog(new Log(Log.LogType.INFO, "Added component: " + c.getClass().getSimpleName() + " to: " + getName()));
        c.setParent(this);
        componentList.add(c);
    }

    public void removeComponent(Component component){

   //     System.out.println("");

       boolean removed = componentList.remove(component);
       if(!removed) throw new IllegalStateException("Object doesn't extend the component: " + component);
    }

    public <T> void removeComponent(Class<T> component){
        for(int i = 0; i < componentList.size(); i++){
            Component c = componentList.get(i);
            if(component.isAssignableFrom(c.getClass())){
                componentList.remove(c);
                return;
            }
        }
    }

    public void destroyAllComponents(){
        componentList.clear();
    }

    public boolean isSerializable(){
        return isSerializable;
    }

    public boolean isTriggerable(){
        return isTriggerable;
    }

    public <T extends Component> T getComponent(Class<T> component){
        for(Component c: componentList){
            if(component.isAssignableFrom(c.getClass())) return component.cast(c);
        }

        return null;
    }

    public <T extends Component> List<T> getAllCompThisType(Class<T> component){
        List<T> compList = new ArrayList<>();

        for(Component c: componentList){
            if(component.isAssignableFrom(c.getClass())){
                compList.add(component.cast(c));
            }
        }

        Console.addLog(new Log(Log.LogType.INFO, "getAllComponentThisType:: got " + compList.size()));
        return compList;
    }

    public List<Component> getAllComponent(){
        return componentList;
    }

    public void setName(){
        name = (String) JImGui.drawValue("Name: ", name, this.hashCode() + "");
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTransformFromItself(){
        this.transform = getComponent(Transform.class);
    }

    public void setNonSerializable(){
        isSerializable = false;
    }

    public void setNonTriggerable(){
        isTriggerable = false;
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

    public boolean isDirty(){
        return  dirty;
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public Transform getLastTransform(){
        return lastTransform;
    }
}
