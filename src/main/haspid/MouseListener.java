package main.haspid;

import main.Editor.ViewPort;
import org.joml.Vector4f;

import static main.Configuration.windowHeight;
import static main.Configuration.windowWidth;
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

    public double getOrthoX(){
        Camera cam = Window.getInstance().getCurrentScene().getCamera();

        float currentX = (((float) x - ViewPort.startFromX) / ViewPort.viewPortWidth) * 2f - 1f;
        Vector4f vec4 = new Vector4f(currentX, 0 , 0, 1);
        vec4 = vec4.mul(cam.getInverseUProjection()).mul(cam.getInverseUView());

        return vec4.x;
    }

    public double getOrthoY(){
        Camera cam = Window.getInstance().getCurrentScene().getCamera();

        float currentY = ((ViewPort.viewPortHeight - (float) y + ViewPort.startFromY) / ViewPort.viewPortHeight) * 2f - 1f;
        Vector4f vec4 = new Vector4f(0, currentY , 0, 1);
        vec4 = vec4.mul(cam.getInverseUProjection()).mul(cam.getInverseUView());

        return vec4.y;
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
