package main.editor.editorControl;

import main.components.Component;
import main.editor.GridLines;
import main.haspid.Camera;
import main.haspid.MouseListener;
import main.renderer.DebugDraw;
import main.renderer.DebugDrawEvents;
import org.joml.Vector2d;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class CameraControl extends Component {
    private final Camera camera;
    private final MouseListener mouse;

    public CameraControl(Camera camera){
        this.camera = camera;
        this.mouse = MouseListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if( !MouseControls.hasDraggingOrActiveObject() && mouse.isCursorInsideViewPort()){
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
            if(EventController.resetMode){
                double xPos = camera.getPosition().x * resetCameraPosSpeed;
                double yPos = camera.getPosition().y * resetCameraPosSpeed;

                if(camera.getPosition().x != 0 && camera.getPosition().y != 0) {
                    System.out.println("case 1");
                    camera.getPosition().sub(xPos, yPos);
                    if (Math.abs(camera.getPosition().x) <= equalizeCameraBy && Math.abs(camera.getPosition().y) <= equalizeCameraBy) camera.resetPosition();
                }else{
                    System.out.println(currentZoomValue);
                    if(currentZoomValue != 1)camera.decreaseZoom();
                }
                if(camera.getPosition().x == 0 && camera.getPosition().y == 0 && currentZoomValue == 1){
                    EventController.resetMode = false;
                }
            }
        }
    }

    public void reset(){
        // reset only if there is not dragging or active object and the cursor is inside view port
        if( !MouseControls.hasDraggingOrActiveObject() && mouse.isCursorInsideViewPort()) {
            EventController.resetMode = true;
        }
    }
}
