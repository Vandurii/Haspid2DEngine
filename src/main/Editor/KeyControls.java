package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.GameObject;
import main.haspid.KeyListener;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.RenderBatch;
import main.renderer.Renderer;
import main.scene.EditorScene;
import main.scene.GameScene;
import main.scene.Scene;
import main.util.AssetPool;

import java.util.List;

import static main.Configuration.keyDebounceC;
import static main.Configuration.zoom;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private float keyDebounce;
    private KeyListener keyboard;
    private float resetDebounce;
    private EditorScene editorScene;
    private MouseControls mouseControls;
    private CameraControl cameraControl;

    public KeyControls(MouseControls mouseControls, CameraControl cameraControl, EditorScene editorScene){
        this.editorScene = editorScene;
        this.keyDebounce = keyDebounceC;
        this.resetDebounce = keyDebounce;
        this.mouseControls = mouseControls;
        this.cameraControl = cameraControl;
        this.keyboard = KeyListener.getInstance();
    }

    @Override
    public void update(float dt) {
        List<GameObject> activeObjectList = mouseControls.getActiveGameObject();
        Scene scene = Window.getInstance().getCurrentScene();
        Gizmo gizmo = mouseControls.getGizmo();

        if(keyDebounce < 0) {
            if (keyboard.isKeyPressed(GLFW_KEY_P)) {
                printInfo();
            } else if (keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
                if(mouseControls.getCursorObject() != null){
                    mouseControls.clearCursor();
                }else {
                   mouseControls.clearActiveObjectList();
                }
            } else if (keyboard.isKeyPressed(GLFW_KEY_L)) {
                System.out.println("*** start ***");
                List<RenderBatch> rList = Renderer.getInstance().getRenderBatchList();
                for (RenderBatch rb : rList) {
                    System.out.println(rb.getzIndex() + ": " +rb.getFreeSlots().stream().toList());
                }
            }else if(keyboard.isKeyPressed(GLFW_KEY_5)){
                gizmo.setGizmoIndex(0);
            }else if(keyboard.isKeyPressed(GLFW_KEY_6)){
                gizmo.setGizmoIndex(1);
            }else if(keyboard.isKeyPressed(GLFW_KEY_7)){
                gizmo.setGizmoIndex(2);
            }else if(keyboard.isKeyPressed(GLFW_KEY_DELETE) && activeObjectList != null){
                removeObject(activeObjectList, scene);
            } else if(keyboard.isKeyPressed(GLFW_KEY_R)){
                    DebugDraw.sleep();
                    cameraControl.reset();
            }else if(keyboard.isKeyPressed(GLFW_KEY_1)){
                System.out.println("u have pressed 1");
            }else if(keyboard.isKeyPressed(GLFW_KEY_C)){
                System.out.println("*** start ***");
                if(activeObjectList.size() == 1){
                    for(Component c: activeObjectList.get(0).getAllComponent()){
                        System.out.println(c);
                    }
                }
            }else if(mouseControls.getMouseListener().isButtonPressed(GLFW_MOUSE_BUTTON_2) && !mouseControls.isHoldingObjectOccupied()){
                if(keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
                    mouseControls.scanForObject(true);
                }else{
                    mouseControls.scanForObject(false);
                }
            }

            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }

    public void removeObject(List<GameObject> activeObject, Scene scene){
        for(GameObject active: activeObject) {
            scene.removeFromScene(active);
        }
        mouseControls.clearActiveObjectList();
    }

    public void printInfo(){
        for(RenderBatch rb: Renderer.getInstance().getRenderBatchList()){
            System.out.println("index: " + rb.getzIndex());
            rb.printPointsValues();
        }
        AssetPool.printResourcesInAssetPool();
        Scene.printSceneObjects();
    }
}
