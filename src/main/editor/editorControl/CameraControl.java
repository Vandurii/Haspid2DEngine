package main.editor.editorControl;

import main.components.Component;
import main.haspid.Camera;
import main.haspid.MouseListener;
import org.joml.Vector2d;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class CameraControl extends Component {
    private boolean resetMode;
    private final Camera camera;
    private final MouseListener mouse;
    private final MouseControls mouseControls;

    public CameraControl(Camera camera, MouseControls mouseControls){
        this.camera = camera;
        this.mouseControls = mouseControls;
        this.mouse = MouseListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if( !mouseControls.hasDraggingOrActiveObject() && mouse.isCursorInsideViewPort()){
            // update camera
            if (mouse.isMouseDragging() && mouse.isCursorInsideViewPort() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                Vector2d delta = mouse.getDelta();

                double valueX = (delta.x * (dt * cameraSensitivity * currentZoomValue));
                double valueY = (delta.y * (dt * cameraSensitivity * currentZoomValue));
                camera.addToBuffer( valueX, valueY);
            }

            // zoom with scroll
            double value = mouse.getScroll();
            if(value != 0) camera.zoom(value);

            // reset camera to start position if reset mode is true
            if(resetMode){
                double xPos = camera.getPosition().x * resetCameraPosSpeed;
                double yPos = camera.getPosition().y * resetCameraPosSpeed;

                if(camera.getPosition().x != 0 && camera.getPosition().y != 0) {
                    camera.getPosition().sub(xPos, yPos);
                    if (Math.abs(camera.getPosition().x) <= equalizeCameraBy && Math.abs(camera.getPosition().y) <= equalizeCameraBy) camera.resetPosition();
                }else{
                    if(currentZoomValue != 1)camera.decreaseZoom();
                }
                if(camera.getPosition().x == 0 && camera.getPosition().y == 0 && currentZoomValue == 1){
                    resetMode = false;
                    //todo DebugDraw.enable();
                }
            }
        }
    }

    public void reset(){
        resetMode = true;
    }
}
