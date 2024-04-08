package main.components;

import imgui.ImGui;
import main.haspid.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.MarshalledObject;

public abstract class Component {

    private int componentID;
    private transient GameObject parentObject;
    private static int ID_COUNTER = -1;

    public Component(){
        componentID = ++ID_COUNTER;
    }

    public void start(){}

    public abstract void update(float dt);

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
                    int[] imInt = {(int)value};
                    if(ImGui.dragInt(name, imInt)) f.set(this, imInt[0]);
                }else if(clazz == float.class){
                    float[] imFloat = {(float)value};
                    if(ImGui.dragFloat(name, imFloat)) f.set(this, imFloat[0]);
                }else if(clazz == boolean.class){
                    boolean imBoolean = (boolean) value;
                    if(ImGui.checkbox(name,imBoolean)) f.set(this, !imBoolean);
                }else if(clazz == Vector3f.class){
                    Vector3f vec3 = (Vector3f) value;
                    float[] imVec3 = {vec3.x, vec3.y, vec3.z};
                    if(ImGui.dragFloat3(name, imVec3)) vec3.set(imVec3);
                }else if(clazz == Vector4f.class){
                    Vector4f vec4 = (Vector4f) value;
                    float[] imVec4 = {vec4.x, vec4.y, vec4.z, vec4.w};
                    if(ImGui.dragFloat4(name, imVec4)) vec4.set(imVec4);
                }

                f.setAccessible(false);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    public GameObject getParent(){
        return parentObject;
    }

    public void setParent(GameObject gameObject){
        this.parentObject = gameObject;
    }

    public int getComponentID(){
        return componentID;
    }

    public int getIDCounter(){
        return ID_COUNTER;
    }

    public void updateIDCounter(){
        if(ID_COUNTER >= componentID) throw new IllegalStateException("IDCounter is higher then this component ID! Check if you did create any component before the scene was loaded.");
        ID_COUNTER = componentID;
    }

    public static void resetCounter(){
        ID_COUNTER = -1;
    }
}
