package main.haspid;

import main.Editor.ViewPort;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;
    private static float x, y, scroll;
    private Vector2f startFrameCursorPos;
    private Vector2f endFrameCursorPos;
    private static boolean[] buttonPressed;
    private static boolean isMouseDragged;
    private static ViewPort viewPort;
    private static Camera cam;

    private MouseListener(){
        viewPort = ViewPort.getInstance();
        buttonPressed = new boolean[3];
        cam = Window.getInstance().getCurrentScene().getCamera();
    }

    public static void cursorPositionCallback(long window, double x, double y){
        if(instance == null) getInstance();
        MouseListener.x = (float) x;
        MouseListener.y = (float) y;

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

        scroll = (float) yOffset;
    }

    public static MouseListener getInstance(){
        if(instance == null) instance = new MouseListener();

        return instance;
    }

    public static void resetInstance(){
        instance = new MouseListener();
    }

    public void startFrame(){
       startFrameCursorPos = getViewPortPos();
    }

    public void endFrame(){
        endFrameCursorPos = startFrameCursorPos;
        scroll = 0;
    }

    public boolean isCursorInsideViewPort(){
        return x > viewPort.getWindowStartFromX() + viewPort.getViewPortStartFromX() && x < viewPort.getWindowStartFromX() + viewPort.getWindowWidth() - viewPort.getViewPortStartFromX()
                &&  y > viewPort.getWindowStartFromY() + viewPort.getViewPortStartFromY() && y < viewPort.getWindowStartFromY() + viewPort.getWindowHeight() - viewPort.getViewPortStartFromY();
    }

    public boolean isButtonPressed(int buttonCode) {
        return buttonPressed[buttonCode];
    }

    public boolean isMouseDragged(){
        return isMouseDragged;
    }

    public Vector2f getDelta(){
        return new Vector2f(startFrameCursorPos.x - endFrameCursorPos.x, startFrameCursorPos.y - endFrameCursorPos.y);
    }

    public float getScroll() {
        return scroll;
    }

    public float getViewPortXProjection(){
        return (getWorldX() - cam.getPosition().x) / zoom;
    }

    public float getViewPortYProjection(){
        return (getWorldY() - cam.getPosition().y) / zoom;
    }

    public Vector2f getViewPortProjectionPos(){
        return  new Vector2f(getViewPortXProjection(), getViewPortYProjection());
    }

    public float getViewPortX(){
        return getViewPortXProjection() * windowsScale.x;
    }

    public float getViewPortY(){
        return getViewPortYProjection() * windowsScale.y;
    }

    public Vector2f getViewPortPos(){
        return new Vector2f(getViewPortX(), getViewPortY());
    }

    public float getWorldX(){
        float xPosViewPort = x - viewPort.getWindowStartFromX();

        float currentX = ((xPosViewPort - viewPort.getViewPortStartFromX()) / viewPort.getViewPortWidth()) * 2f - 1f;
        Vector4f vec4 = new Vector4f(currentX, 0 , 0, 1);
        vec4 = vec4.mul(cam.getInverseUProjection()).mul(cam.getInverseUView());

        return vec4.x;
    }

    public float getWorldY(){
        float yPosViewPort = y - viewPort.getWindowStartFromY();

        float currentY = ((viewPort.getViewPortHeight() - yPosViewPort + viewPort.getViewPortStartFromY()) / viewPort.getViewPortHeight()) * 2f - 1f;
        Vector4f vec4 = new Vector4f(0, currentY , 0, 1);
        vec4 = vec4.mul((cam.getInverseUProjection())).mul(cam.getInverseUView());

        return vec4.y;
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        MouseListener.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        MouseListener.y = y;
    }

}
