package main.Editor;

import main.components.Component;
import main.haspid.*;
import main.renderer.DebugDraw;
import main.scene.EditorScene;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.gridSize;
import static main.Configuration.keyDebounceC;
import static main.haspid.Direction.*;
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
            }else if(keyboard.isKeyPressed(GLFW_KEY_UP)){
                move(Up);
            }else if(keyboard.isKeyPressed(GLFW_KEY_DOWN)){
                move(Down);
            }else if(keyboard.isKeyPressed(GLFW_KEY_RIGHT)){
                move(Right);
            }else if(keyboard.isKeyPressed(GLFW_KEY_LEFT)){
                move(Left);
            }else if(keyboard.isKeyPressed(GLFW_KEY_C)){
                copyObject();
            }else if(keyboard.isKeyPressed(GLFW_KEY_Y)){
                if(!mouseControls.isDistanceMapLoaded()){
                    mouseControls.initObjDistanceFromCursor();
                }
                mouseControls.trackMouseMultiple();
            }else if(!keyboard.isKeyPressed(GLFW_KEY_Y)){
                mouseControls.resetObjDistanceFromCursor();
            }

            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }

    public void move(Direction direction){
        int xAxis = 0;
        int yAxis = 0;
        int unit = gridSize;
        switch (direction){
            case Up -> yAxis = -unit;
            case Down -> yAxis = unit;
            case Right -> xAxis = -unit;
            case Left -> xAxis = unit;
        }

        for(GameObject g: mouseControls.getAllActiveObjects()){
            g.getTransform().setPosition(g.getTransform().getPosition().sub(xAxis, yAxis));
        }
    }

    public void copyObject(){
        List<GameObject> cloneList = new ArrayList<>();
        List<GameObject> activeObjects = mouseControls.getAllActiveObjects();

        for(GameObject gameObject: activeObjects){
            GameObject copy = gameObject.copy();
            editorScene.addGameObjectToScene(copy);
            cloneList.add(copy);
        }

        mouseControls.setObjectActive(cloneList);
    }

    public void removeObject(List<GameObject> activeObject){
        for(GameObject active: activeObject) {
            editorScene.removeFromScene(active);
        }
        mouseControls.unselectActiveObjects();
    }
}
