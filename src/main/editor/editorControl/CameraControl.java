package main.editor.editorControl;

import main.Configuration;
import main.components.Component;
import main.haspid.Camera;
import main.haspid.MouseListener;
import main.renderer.DebugDraw;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class CameraControl extends Component {
    private Camera camera;
    private double debounce;
    private double resetDebounce;
    private MouseListener mouse;
    private boolean resetMode;
    private MouseControls mouseControls;

    public CameraControl(Camera camera, MouseControls mouseControls){
        this.camera = camera;
        this.mouseControls = mouseControls;
        this.mouse = MouseListener.getInstance();
        this.debounce = Configuration.debounceForCamera;
        this.resetDebounce = debounce;
    }

    @Override
    public void update(float dt) {
        if(debounce < 0  && !mouseControls.hasDraggingOrActiveObject()){
            // update camera
            if (mouse.isMouseDragging() && mouse.isCursorInsideViewPort() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                Vector2d delta = mouse.getDelta();

                double valueX = (delta.x * (dt * cameraSensivity * zoom));
                double valueY = (delta.y * (dt * cameraSensivity * zoom ));
                camera.addToBuffer( valueX, valueY);
            }

            double value = mouse.getScroll();
            // zoom with scroll
            if(value != 0) camera.zoom(value);

            // reset camera to start position if reset mode is true
            if(resetMode){
                double xPos = camera.getPosition().x * resetCameraSpeed;
                double yPos = camera.getPosition().y * resetCameraSpeed;

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

            // reset cooldown
            debounce = resetDebounce;
        }
        debounce -= dt;
    }

    public void reset(){
        resetMode = true;
    }
}
