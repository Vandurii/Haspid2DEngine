package main.haspid;

import main.Editor.ViewPort;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;
    private static float x, y, lastX, lastY, scroll;
    private Vector2f startFrameCursorPos;
    private Vector2f endFrameCursorPos;
    private static boolean[] buttonPressed;
    private static boolean isMouseDragged;
    private static ViewPort viewPort;

    private MouseListener(){
        viewPort = ViewPort.getInstance();
        buttonPressed = new boolean[3];
    }

    public static void cursorPositionCallback(long window, double x, double y){
        if(instance == null) getInstance();
        lastX = MouseListener.x;
        lastY = MouseListener.y;
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

    public float getScreenX(){
        return getWorldX() * windowsScale.x;
    }

    public float getScreenY(){
        return getWorldY() * windowsScale.y;
    }

    public float getWorldX(){
        Camera cam = Window.getInstance().getCurrentScene().getCamera();
        ViewPort viewPort = ViewPort.getInstance();

        float xPosViewPort = (float) (x - viewPort.getWindowStartFromX());

        float currentX = ((xPosViewPort - viewPort.getViewPortStartFromX()) / viewPort.getViewPortWidth()) * 2f - 1f;
        Vector4f vec4 = new Vector4f(currentX, 0 , 0, 1);
        vec4 = vec4.mul(cam.getInverseUProjection()).mul(cam.getInverseUView());

        return (vec4.x - cam.getPosition().x) / zoom;
    }

    public float getWorldY(){
        Camera cam = Window.getInstance().getCurrentScene().getCamera();
        ViewPort viewPort = ViewPort.getInstance();
        float yPosViewPort = (float)(y - viewPort.getWindowStartFromY());

        float currentY = ((viewPort.getViewPortHeight() - yPosViewPort + viewPort.getViewPortStartFromY()) / viewPort.getViewPortHeight()) * 2f - 1f;
        Vector4f vec4 = new Vector4f(0, currentY , 0, 1);
        vec4 = vec4.mul((cam.getInverseUProjection())).mul(cam.getInverseUView());

        return (vec4.y - cam.getPosition().y) / zoom;
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

    public void startFrame(){
       startFrameCursorPos = getScreenPos();
    }

    public void endFrame(){
        endFrameCursorPos = startFrameCursorPos;
        scroll = 0;
    }

    public Vector2f getDelta(){
        return new Vector2f(startFrameCursorPos.x - endFrameCursorPos.x, startFrameCursorPos.y - endFrameCursorPos.y);
    }

    public float getScroll() {
        return scroll;
    }

    public boolean isButtonPressed(int buttonCode) {
        return buttonPressed[buttonCode];
    }

    public boolean isIsMouseDragged(){
        return isMouseDragged;
    }

    public Vector2f getWorldPos(){
        return  new Vector2f(getWorldX(), getWorldY());
    }

    public Vector2f getScreenPos(){
        return new Vector2f(getScreenX(), getScreenY());
    }

    public boolean isCursorInsideViewPort(){
        return x > viewPort.getWindowStartFromX() + viewPort.getViewPortStartFromX() && x < viewPort.getWindowStartFromX() + viewPort.getWindowWidth() - viewPort.getViewPortStartFromX()
        &&  y > viewPort.getWindowStartFromY() + viewPort.getViewPortStartFromY() && y < viewPort.getWindowStartFromY() + viewPort.getWindowHeight() - viewPort.getViewPortStartFromY();
    }
}
