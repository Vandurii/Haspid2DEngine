package main.components;

import imgui.ImGui;
import imgui.type.ImInt;
import main.Editor.JImGui;
import main.haspid.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private int componentID;
    private static int ID_COUNTER = -1;
    private transient GameObject parentObject;

    public Component(){
        componentID = ++ID_COUNTER;
    }

    public void start(){}

    public abstract void update(float dt);

    public  Component copy(){
        System.out.println("Im not overrided");
        return null;
    };

    public void updateIDCounter(){
        ID_COUNTER = componentID;
    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    public void dearGui() {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field f: fields){
            try {
                boolean isTransient = Modifier.isTransient(f.getModifiers());
                if(isTransient) continue;
                boolean isPrivate = Modifier.isPrivate(f.getModifiers());
                if(isPrivate) f.setAccessible(true);

                Class clazz = f.getType();
                Object value = f.get(this);
                String name = f.getName();

                if (clazz == int.class || clazz == float.class) {
                    f.set(this, JImGui.drawValue(f.getName(), value));
                }else if(clazz == boolean.class){
                    boolean imBoolean = (boolean) value;
                    if(ImGui.checkbox(name,imBoolean)) f.set(this, !imBoolean);
                }else if(clazz.isEnum()){
                    String[] enumValues = getEnumValues(clazz);
                    String enumName = ((Enum<?>) value).name();
                    ImInt index = new ImInt(getIndexOf(enumName, enumValues));

                    if(ImGui.combo(f.getName(), index, enumValues, enumValues.length)){
                        f.set(this, clazz.getEnumConstants()[index.get()]);
                    }

                }else{
                    JImGui.drawValue(name, value);
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
