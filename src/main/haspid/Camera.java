package main.haspid;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

import static main.Configuration.*;

public class Camera {
    private Matrix4f uProjection, uView;
    private Vector2f position;

    public Camera(Vector2f position){
        this.position = position;
        this.uProjection = new Matrix4f();
        this.uView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection(){
        uProjection.identity();
        uProjection.ortho(0f, uViewX, 0f, uViewY, 0f, uViewZ);
    }

    public Matrix4f getUView(){
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f);
        Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
        uView.identity();
        uView.lookAt(new Vector3f(position.x, position.y, 20f), cameraFront.add(position.x, position.y, 0f), cameraUp);

        return uView;
    }

    public Matrix4f getUProjection(){
        return uProjection;
    }

    public Vector2f getPosition(){
        return position;
    }
}
