package main.haspid;

import main.editor.ViewPort;
import org.joml.Vector2d;
import org.joml.Vector4d;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {

    private static Camera cam;
    private static ViewPort viewPort;

    private static double x, y, scroll;
    private static boolean isMouseDragged;
    private static boolean[] buttonPressed;
    private static MouseListener instance;

    private Vector2d endFrameCursorPos;
    private Vector2d startFrameCursorPos;
    private Vector2d endFrameCursorPosMMode;
    private Vector2d startFrameCursorPosMMode;

    private MouseListener(){
        viewPort = ViewPort.getInstance();
        buttonPressed = new boolean[3];
        cam = Window.getInstance().getCurrentScene().getCamera();
    }

    public static void cursorPositionCallback(long window, double x, double y){
        if(instance == null) getInstance();
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

        scroll = (float) yOffset;
    }

    public static void resetInstance(){
        instance = new MouseListener();
    }

    public void startFrame(){
       startFrameCursorPos = getViewPortPos();
       startFrameCursorPosMMode = getMouseListenerPos();
    }

    public void endFrame(){
        endFrameCursorPos = startFrameCursorPos;
        endFrameCursorPosMMode = getMouseListenerPos();
        scroll = 0;
    }

    public boolean isCursorInsideViewPort(){
        return x > viewPort.getWindowStartFromX() + viewPort.getViewPortStartFromX() && x < viewPort.getWindowStartFromX() + viewPort.getWindowWidth() - viewPort.getViewPortStartFromX()
                &&  y > viewPort.getWindowStartFromY() + viewPort.getViewPortStartFromY() && y < viewPort.getWindowStartFromY() + viewPort.getWindowHeight() - viewPort.getViewPortStartFromY();
    }

    public boolean isButtonPressed(int buttonCode) {
        return buttonPressed[buttonCode];
    }

    public boolean isMouseDragging(){
        return isMouseDragged;
    }

    public Vector2d getDelta(){
        return new Vector2d(startFrameCursorPos.x - endFrameCursorPos.x, startFrameCursorPos.y - endFrameCursorPos.y);
    }

    public Vector2d getDeltaMMode(){
        return new Vector2d(startFrameCursorPosMMode.x - endFrameCursorPosMMode.x, startFrameCursorPosMMode.y - endFrameCursorPosMMode.y);
    }

    public double getScroll() {
        return scroll;
    }

    public double getViewPortXProjection(){
        return (getWorldX() - cam.getPosition().x) / currentZoomValue;
    }

    public double getViewPortYProjection(){
        return (getWorldY() - cam.getPosition().y) / currentZoomValue;
    }

    public Vector2d getViewPortProjectionPos(){
        return  new Vector2d(getViewPortXProjection(), getViewPortYProjection());
    }

    public double getViewPortX(){
        return getViewPortXProjection() * windowsScale.x;
    }

    public double getViewPortY(){
        return getViewPortYProjection() * windowsScale.y;
    }

    public Vector2d getViewPortPos(){
        return new Vector2d(getViewPortX(), getViewPortY());
    }

    public double getWorldX(){
        double xPosViewPort = x - viewPort.getWindowStartFromX();

        double currentX = ((xPosViewPort - viewPort.getViewPortStartFromX()) / viewPort.getViewPortWidth()) * 2 - 1;
        Vector4d vec4 = new Vector4d(currentX, 0 , 0, 1);
        vec4 = vec4.mul(cam.getInverseUProjection()).mul(cam.getInverseUView());

        return vec4.x;
    }

    public double getWorldY(){
        double yPosViewPort = y - viewPort.getWindowStartFromY();

        double currentY = ((viewPort.getViewPortHeight() - yPosViewPort + viewPort.getViewPortStartFromY()) / viewPort.getViewPortHeight()) * 2f - 1f;
        Vector4d vec4 = new Vector4d(0, currentY , 0, 1);
        vec4 = vec4.mul((cam.getInverseUProjection())).mul(cam.getInverseUView());

        return vec4.y;
    }

    public Vector2d getWorld(){
        return  new Vector2d(getWorldX(), getWorldY());
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

    public Vector2d getMouseListenerPos(){
        return new Vector2d(getX(), getY());
    }

    public static MouseListener getInstance(){
        if(instance == null) instance = new MouseListener();

        return instance;
    }
}
