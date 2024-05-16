package main.haspid;

import org.joml.*;

import java.lang.Math;

import static main.Configuration.*;

public class Camera {
    private Matrix4f uProjection, uView;
    private Vector2d position;
    private Matrix4f inverseUProjection, inverseUView;
    private double xBuffer, yBuffer;

    public Camera(Vector2d position){
        this.position = position;
        this.uProjection = new Matrix4f();
        this.uView = new Matrix4f();
        this.inverseUProjection = new Matrix4f();
        this.inverseUView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection(){
        uProjection.identity();
        uProjection.ortho(0f, (float) (uProjectionDimension.x * zoom), 0f, (float) (uProjectionDimension.y * zoom), 0f, (float) uProjectionDimension.z);

        uProjection.invert(inverseUProjection);
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

    public void zoom(double value){
        double val = value * scrollSensivity * zoom;
        if(zoom - val < minZoomValue || zoom - val > maxZoomValue) return;
        zoom -= val;
        adjustProjection();
    }

    public void decreasingZoom(){
        if(Math.abs(zoom) > 1.2){
            zoom -= 0.1f;
        }else{
            zoom = 1;
        }
        adjustProjection();
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
        this.position = position;
    }

    public void setPosition(double x, double y){
        position.x = x;
        position.y = y;
    }
}
