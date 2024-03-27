package main.haspid;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;
    private static double x, y, lastX, lastY, scroll;
    private static boolean[] buttonPressed;
    private static boolean isMouseDragged;

    private MouseListener(){
        buttonPressed = new boolean[3];
    }

    public static void cursorPositionCallback(long window, double x, double y){
        if(instance == null) getInstance();
        lastX = MouseListener.x;
        lastY = MouseListener.y;
        MouseListener.x = x;
        MouseListener.y = y;

        // If mouse is moving and at least one button is pressed then mouse is dragging.
        for(boolean val: buttonPressed){
            if(val){
                isMouseDragged = true;
                break;
            }
        }
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if(instance == null) getInstance();
        if(action == GLFW_PRESS){
            buttonPressed[button] = true;
        }else if(action == GLFW_RELEASE){
            buttonPressed[button] = false;

            // If button is released and no more buttons are pressed then mouse is not dragging, if at least one button is pressed then return.
            for(boolean val: buttonPressed){
                if(val) return;
            }
            isMouseDragged = false;
        }
    }

    public static void scrollCallback(long window, double xOffset, double yOffset){
        if(instance == null) getInstance();
        scroll = xOffset;
    }

    public static MouseListener getInstance(){
        if(instance == null) instance = new MouseListener();

        return instance;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        MouseListener.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        MouseListener.y = y;
    }

    public double getLastX() {
        return lastX;
    }

    public void setLastX(double lastX) {
        MouseListener.lastX = lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public void setLastY(double lastY) {
        MouseListener.lastY = lastY;
    }

    public double getScroll() {
        return scroll;
    }

    public boolean isButtonPressed(int buttonCode) {
        return buttonPressed[buttonCode];
    }

    public boolean isIsMouseDragged(){
        return isMouseDragged;
    }
}
