package main.haspid;

import org.joml.*;

import java.lang.Math;

import static main.Configuration.*;

public class Camera {
    private Vector2d position;
    private double xBuffer, yBuffer;
    private Matrix4f uProjection, uView;
    private Matrix4f inverseUProjection, inverseUView;

    public Camera(Vector2d position){
        this.position = position;
        this.uView = new Matrix4f();
        this.uProjection = new Matrix4f();
        this.inverseUView = new Matrix4f();
        this.inverseUProjection = new Matrix4f();

        adjustProjection();
    }

    public void adjustProjection(){
        uProjection.identity();
        uProjection.ortho(0f, (float) (uProjectionDimension.x * currentZoomValue), 0f, (float) (uProjectionDimension.y * currentZoomValue), 0f, (float) uProjectionDimension.z);
        uProjection.invert(inverseUProjection);
    }

    public void zoom(double value){
        double val = value * scrollSensitivity * currentZoomValue;
        if(currentZoomValue - val < minZoomValue || currentZoomValue - val > maxZoomValue) return;
        currentZoomValue -= val;
        adjustProjection();
    }

    public void decreaseZoom(){
        double defaultZoomVal = 1;

        double val = currentZoomValue < defaultZoomVal ? resetCameraZoomSpeed : -resetCameraZoomSpeed;
        currentZoomValue += val;

        // if abs value from 1 is less now then next frame then set default value
        double distance = currentZoomValue - defaultZoomVal;
        if((currentZoomValue + val - defaultZoomVal) > distance) currentZoomValue = defaultZoomVal;


        // if value is close to default then set default value
        if(Math.abs(Math.abs(currentZoomValue) - defaultZoomVal) < equalizeZoomBy){
            currentZoomValue = defaultZoomVal;
        }

        adjustProjection();
    }

    public void addToBuffer(double x, double y){
        xBuffer += x;
        yBuffer += y;

        if(Math.abs(xBuffer) >= gridSize){
            position.x -= xBuffer > 0 ? gridSize : gridSize * -1;
            resetXBuffer();
        }

        if(Math.abs(yBuffer) >= gridSize){
            position.y -= yBuffer > 0 ? gridSize : gridSize * -1;
            resetYBuffer();
        }
    }

    public void resetXBuffer(){
        xBuffer = 0;
    }

    public void resetYBuffer(){
        yBuffer = 0;
    }

    public void resetPosition(){
        position = new Vector2d();
    }

    public Matrix4f getUView(){
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f);
        Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
        uView.identity();
        uView.lookAt(new Vector3f((float) position.x, (float) position.y, 20f), cameraFront.add((float) position.x, (float) position.y, 0f), cameraUp);

        return uView;
    }

    public Matrix4f getInverseUProjection(){
        return inverseUProjection;
    }

    public Matrix4f getInverseUView(){
        uView.invert(inverseUView);
        return inverseUView;
    }

    public Matrix4f getUProjection(){
        return uProjection;
    }

    public Vector2d getPosition(){
        return position;
    }

    public void setPositionX(float position){
        this.position.x = position;
    }

    public void setPositionY(float position){
        this.position.y = position;
    }

    public void setPositionX(Vector2d position){
        this.position = new Vector2d(position.x, position.y);
    }

    public void setPosition(double x, double y){
        position.x = x;
        position.y = y;
    }
}
