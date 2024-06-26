package main.haspid;

import imgui.ImGui;
import main.components.SpriteRenderer;
import main.editor.InactiveInEditor;
import main.editor.JImGui;
import main.components.Component;
import main.renderer.RenderBatch;
import main.util.Texture;
import org.joml.Vector2d;

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
            transform.copyTo(lastTransform);
        }

        // update all component from this object
        for(Component component: componentList){

            // skip component if it shouldn't be active in editor mode
           if((component instanceof InactiveInEditor) && Window.getInstance().getCurrentScene().isInEditMode()){
               continue;
           }
             component.update(dt);
        }

        dirty = false;
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

    public void updateDearGui(){
        if(ImGui.collapsingHeader("Name")) setName();

        for(Component c: componentList){
            if(ImGui.collapsingHeader(c.getClass().getSimpleName())) c.dearGui();
        }
    }

    public boolean isSerializable(){
        return isSerializable;
    }

    public boolean isTriggerable(){
        return isTriggerable;
    }

    public void addComponent(Component c){
        Console.addLog(new Log(Log.LogType.INFO, "Added component: " + c.getClass().getSimpleName() + " to: " + getName()));
        c.setParent(this);
        componentList.add(c);
    }

    public void removeComponent(Component component){
        boolean removed = componentList.remove(component);
        if(!removed) throw new IllegalStateException("Object doesn't extend the component: " + component);
    }

    public void removeComponent(Class component){
      componentList.remove( getComponent(component));
    }

    public void destroyAllComponents(){
        componentList.clear();
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
        name = (String) JImGui.drawValue("Name: ", name);
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

    public void grown(boolean grownX, boolean grownY, int percentage){
        Vector2d scale = transform.getScale();
        double x = scale.x;
        double y = scale.y;

        if(grownX){
            x += (x / 100) * percentage;
        }

        if(grownY){
            y += (y / 100) * percentage;
        }

        transform.setScale(new Vector2d(x, y));
    }

    public void shrink(boolean grownX, boolean grownY, int percentage){
        Vector2d scale = transform.getScale();
        double x = scale.x;
        double y = scale.y;

        if(grownX){
            x -= (x / 100) * percentage;
        }

        if(grownY){
            y -= (y / 100) * percentage;
        }

        transform.setScale(new Vector2d(x, y));
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

    public void setSprite(SpriteRenderer newSpriteRender){
        Texture newTexture = newSpriteRender.getTexture();
        Vector2d[] newTexCords = newSpriteRender.getSpriteCords();

        SpriteRenderer originalSpriteRender = getComponent(SpriteRenderer.class);

        // Change texture slot when the new sprite comes from another file.
        if(originalSpriteRender == null) return;
        if(!newTexture.getFilePath().equals(originalSpriteRender.getTexture().getFilePath())){
            originalSpriteRender.setTexture(newTexture);
            RenderBatch.initTextureInfo(originalSpriteRender);
        }else {
            originalSpriteRender.setTexture(newTexture);
        }
        originalSpriteRender.setSpriteCords(newTexCords);
        originalSpriteRender.setDirty();
    }
}
