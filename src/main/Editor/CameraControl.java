package main.Editor;

import main.Configuration;
import main.components.Component;
import main.haspid.Camera;
import main.haspid.KeyListener;
import main.haspid.MouseListener;
import main.renderer.DebugDraw;
import org.joml.Vector2f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class CameraControl extends Component {
    private Camera camera;
    private MouseListener mouse;
    private float debounce;
    private float resetDebounce;
    private boolean resetMode;

    public CameraControl(Camera camera){
        this.debounce = Configuration.debounceForCamera;
        this.resetDebounce = debounce;
        this.camera = camera;
        this.mouse = MouseListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if(debounce < 0) {
            mouse.startFrame();
            // update camera
            if (mouse.isIsMouseDragged() && mouse.isCursorInsideViewPort()) {
                Vector2f delta = mouse.getDelta();

                float valueX = (delta.x * (dt * cameraSensivity * zoom));
                float valueY = (delta.y * (dt * cameraSensivity * zoom ));
                camera.addToBuffer( valueX, valueY);
            }

            float value = mouse.getScroll();
            // zoom with keyboard
            if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_1)){
                value = -zoomForKeys;
            } else if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_2)) {
                value = zoomForKeys;
            }

            // zoom with scroll
            if(value != 0) camera.zoom(value);

            // reset position and zoom
            if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_R)){
                DebugDraw.sleep();
                resetMode = true;
            }

            // reset camera to start position if reset mode is true
            if(resetMode){
                float xPos = camera.getPosition().x * resetCameraSpeed;
                float yPos = camera.getPosition().y * resetCameraSpeed;

                if(camera.getPosition().x != 0 && camera.getPosition().y != 0) {
                    camera.getPosition().sub(xPos, yPos);
                    if (Math.abs(camera.getPosition().x) <= skipCamera && Math.abs(camera.getPosition().y) <= skipCamera) camera.resetPosition();
                }else{
                    if(zoom != 1)camera.decreasingZoom();
                }
                if(camera.getPosition().x == 0 && camera.getPosition().y == 0 && zoom == 1){
                    resetMode = false;
                    DebugDraw.yield();
                }
            }

            // reset cooldown, reset values
            debounce = resetDebounce;
            mouse.endFrame();
        }
        debounce -= dt;
    }
}
