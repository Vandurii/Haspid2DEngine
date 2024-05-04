package main.components;

import imgui.ImGui;
import main.Editor.JImGui;
import main.haspid.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.MarshalledObject;

public abstract class Component {

    private int componentID;
    private static int ID_COUNTER = -1;
    private transient GameObject parentObject;

    public Component(){
        componentID = ++ID_COUNTER;
    }

    public void start(){}

    public abstract void update(float dt);

    public void updateIDCounter(){
        if(ID_COUNTER > componentID)throw new IllegalStateException("IDCounter is higher then this component ID! Check if you did create any component before the scene was loaded.");
        ID_COUNTER = componentID;
    }

    public static void resetCounter(){
        ID_COUNTER = -1;
    }

    public void dearGui() {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field f: fields){
            try {
                boolean isPrivate = Modifier.isPrivate(f.getModifiers());
                if(isPrivate) f.setAccessible(true);

                Class clazz = f.getType();
                Object value = f.get(this);
                String name = f.getName();

                if (clazz == int.class) {
                    f.set(this, JImGui.drawValue(f.getName(), (int)value));
                }else if(clazz == float.class){
                    f.set(this, JImGui.drawValue(f.getName(), (float)value));
                }else if(clazz == boolean.class){
                    boolean imBoolean = (boolean) value;
                    if(ImGui.checkbox(name,imBoolean)) f.set(this, !imBoolean);
                }else{
                    JImGui.drawValue(name, value);
                }

                if(isPrivate) f.setAccessible(false);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    public GameObject getParent(){
        return parentObject;
    }

    public int getComponentID(){
        return componentID;
    }

    public int getIDCounter(){
        return ID_COUNTER;
    }

    public void setParent(GameObject gameObject){

        this.parentObject = gameObject;
    }
}
