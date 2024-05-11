package main.Editor;

import main.components.Component;
import main.haspid.*;
import main.renderer.DebugDraw;
import main.renderer.RenderBatch;
import main.renderer.Renderer;
import main.scene.EditorScene;
import main.scene.Scene;
import main.util.AssetPool;

import java.util.List;

import static main.Configuration.keyDebounceC;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private float keyDebounce;
    private KeyListener keyboard;
    private float resetDebounce;
    private EditorScene editorScene;
    private MouseControls mouseControls;
    private MouseListener mouseListener;
    private CameraControl cameraControl;

    public KeyControls(MouseControls mouseControls, CameraControl cameraControl, EditorScene editorScene){
        this.editorScene = editorScene;
        this.keyDebounce = keyDebounceC;
        this.resetDebounce = keyDebounce;
        this.mouseControls = mouseControls;
        this.cameraControl = cameraControl;
        this.keyboard = KeyListener.getInstance();
        this.mouseListener = mouseControls.getMouseListener();
    }

    @Override
    public void update(float dt) {
        List<GameObject> activeObjectList = mouseControls.getAllActiveObjects();
        Gizmo gizmo = mouseControls.getGizmo();

        if(keyDebounce < 0) {

            if (keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
                if(mouseControls.getDraggingObject() != null){
                    mouseControls.removeDraggingObject();
                }else {
                   mouseControls.unselectActiveObjects();
                }
            }else if(keyboard.isKeyPressed(GLFW_KEY_1)){
                gizmo.setGizmoIndex(0);
            }else if(keyboard.isKeyPressed(GLFW_KEY_2)){
                gizmo.setGizmoIndex(1);
            }else if(keyboard.isKeyPressed(GLFW_KEY_3)){
                gizmo.setGizmoIndex(2);
            }else if(keyboard.isKeyPressed(GLFW_KEY_DELETE)){
                removeObject(activeObjectList);
            }else if(keyboard.isKeyPressed(GLFW_KEY_R)){
                    DebugDraw.sleep();
                    cameraControl.reset();
            }else if(keyboard.isKeyPressed(GLFW_KEY_Q)){
                gizmo.destroy();
            }

            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }

    public void removeObject(List<GameObject> activeObject){
        for(GameObject active: activeObject) {
            editorScene.removeFromScene(active);
        }
        mouseControls.unselectActiveObjects();
    }
}
