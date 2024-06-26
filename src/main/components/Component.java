package main.components;

import imgui.ImGui;
import imgui.type.ImInt;
import main.editor.JImGui;
import main.haspid.Console;
import main.haspid.GameObject;
import main.haspid.Log;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class Component {

    private int componentID;
    private static int ID_COUNTER = -1;
    private transient GameObject parentObject;
    private static List<Class> supportedVectors = List.of(Vector2d.class, Vector2d[].class, Vector3f.class, Vector4f.class);
    private static List<Class> supportedNumbers = List.of(int.class, double.class, float.class);

    public Component(){
        this.componentID = ++ID_COUNTER;
    }

    public void init(){}

    public abstract void update(float dt);

    public void updateIDCounter(){
        ID_COUNTER = componentID;
    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2d hitNormal){
    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2d hitNormal){
    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2d hitNormal){
    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2d hitNormal){
    }

    public Component copy(){
        System.out.println("Im not implemented: copy:" + getClass().getSimpleName());
        return null;
    };

    public void dearGui() {
        dearGui(null);
    }

    public void dearGui(Object object){
        Object obj = object;
        if(object == null) obj = this;

        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field f: fields){
            try {
                // don't display fields that are static or transient
                boolean isTransient = Modifier.isTransient(f.getModifiers());
                boolean isStatic = Modifier.isStatic(f.getModifiers());
                if(isTransient || isStatic) continue;

                // allow private fields to be changed
                boolean isPrivate = Modifier.isPrivate(f.getModifiers());
                if(isPrivate) f.setAccessible(true);

                Class clazz = f.getType();
                Object value = f.get(obj);
                String name = f.getName();

                if (supportedNumbers.contains(clazz)) {
                    f.set(obj, JImGui.drawValue(f.getName(), value));
                }else if(clazz == boolean.class){
                    boolean imBoolean = (boolean) value;
                    if(ImGui.checkbox(name,imBoolean)) f.set(obj, !imBoolean);
                }else if(clazz.isEnum()){
                    String[] enumValues = getEnumValues(clazz);
                    String enumName = ((Enum<?>) value).name();
                    ImInt index = new ImInt(getIndexOf(enumName, enumValues));

                    if(ImGui.combo("##" + f.getName(), index, enumValues, enumValues.length)){
                        f.set(obj, clazz.getEnumConstants()[index.get()]);
                    }
                }else if(clazz == String.class){
                    f.set(obj, JImGui.drawValue("Name: ", name));
                }else if(supportedVectors.contains(clazz)){
                    JImGui.drawValue(name, value);
                }else{
                    System.out.println("Can't load value from this class: " + clazz);
                }

                if(isPrivate) f.setAccessible(false);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    public static void resetCounter(){
        ID_COUNTER = -1;
    }

    public <T extends Enum<T>> String[] getEnumValues(Class<T> enumObject){
        String[] enumValues = new String[enumObject.getEnumConstants().length];

        for(int i = 0; i < enumValues.length; i++){
            enumValues[i] = enumObject.getEnumConstants()[i].name();
        }

        return enumValues;
    }

    public int getIndexOf(String enumName, String[] enumValues){
        for(int i = 0; i < enumValues.length; i++){
            if(enumValues[i].equals(enumName)) return i;
        }

        return -1;
    }

    public GameObject getParent(){
        return parentObject;
    }

    public void setParent(GameObject gameObject){
        this.parentObject = gameObject;
    }

    public static List<Class> getSupportedVectors() {
        return supportedVectors;
    }
}
