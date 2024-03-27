package main.haspid;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static KeyListener instance;
    private static boolean[] keyPressed;

    private KeyListener(){
        keyPressed = new boolean[350];
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods){
        if(action == GLFW_PRESS){
            keyPressed[key] = true;
        }else if(action == GLFW_RELEASE){
            keyPressed[key] = false;
        }
    }

    public boolean isKeyPressed(int keyCode){
        return keyPressed[keyCode];
    }

    public static KeyListener getInstance(){
        if(instance == null) instance = new KeyListener();

        return instance;
    }
}
