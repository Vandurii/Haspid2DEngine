package main.editor.editorControl;

import imgui.ImGui;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.physicsComponent.BoxCollider;
import main.haspid.*;
import main.editor.EditorScene;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.haspid.Direction.*;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    private KeyListener keyboard;
    private EditorScene editorScene;
    private MouseControls mouseControls;
    private MouseListener mouseListener;
    private CameraControl cameraControl;

    private double shortCooldown;
    private double longCooldown;
    private double resetShortDebounce;
    private double resetLongCollDown;

    public KeyControls(MouseControls mouseControls, CameraControl cameraControl, EditorScene editorScene){
        this.editorScene = editorScene;
        this.mouseControls = mouseControls;
        this.cameraControl = cameraControl;
        this.keyboard = KeyListener.getInstance();
        this.mouseListener = mouseControls.getMouseListener();

        this.resetShortDebounce = keyShortCooldown;
        this.resetLongCollDown = keyLongCooldown;
    }

    @Override
    public void update(float dt) {
        List<GameObject> activeObjectList = MouseControls.getAllActiveObjects();
        Gizmo gizmo = mouseControls.getGizmo();

        if(shortCooldown < 0) {

            if (keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
                if(mouseControls.getDraggingObject() != null){
                    mouseControls.removeDraggingObject();
                }else {
                   MouseControls.unselectActiveObjects();
                }
            }else if(keyboard.isKeyPressed(GLFW_KEY_1)){
                gizmo.setGizmoToolIndex(0);
            }else if(keyboard.isKeyPressed(GLFW_KEY_2)){
                gizmo.setGizmoToolIndex(1);
            }else if(keyboard.isKeyPressed(GLFW_KEY_3)){
                gizmo.setGizmoToolIndex(2);
            }else if(keyboard.isKeyPressed(GLFW_KEY_DELETE)){
                removeObject(activeObjectList);
            }else if(keyboard.isKeyPressed(GLFW_KEY_R)){
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
                if(longCooldown < 0) {
                    copyObject();
                    longCooldown = resetLongCollDown;
                }
            }else if(keyboard.isKeyPressed(GLFW_KEY_Y)){
                if(!mouseControls.isDistanceMapLoaded()){
                    mouseControls.initObjDistanceFromCursor();
                }
                mouseControls.trackMouseMultiple();
            }else if(keyboard.isKeyPressed(GLFW_KEY_6)){
            }

            ///todo
             if(!keyboard.isKeyPressed(GLFW_KEY_Y)) {
                 mouseControls.resetObjDistanceFromCursor();
             }

            shortCooldown = resetShortDebounce;
             longCooldown -= dt;
        }
        shortCooldown -= dt;
    }

    public void move(Direction direction){
        double xAxis = 0;
        double yAxis = 0;
        double unit = Math.max(gridSize, (gridSize * (int)currentZoomValue));
        switch (direction){
            case Up -> yAxis = -unit;
            case Down -> yAxis = unit;
            case Right -> xAxis = -unit;
            case Left -> xAxis = unit;
        }

        for(GameObject g: MouseControls.getAllActiveObjects()){
            g.getTransform().setPosition(g.getTransform().getPosition().sub(xAxis, yAxis));
        }
    }

    public void copyObject(){
        List<GameObject> cloneList = new ArrayList<>();
        List<GameObject> activeObjects = MouseControls.getAllActiveObjects();

        for(GameObject gameObject: activeObjects){
            GameObject copy = gameObject.copy();
            editorScene.addObjectToSceneSafe(copy);
            cloneList.add(copy);
        }

        mouseControls.setObjectActive(cloneList);
    }

    public void removeObject(List<GameObject> activeObject){
        for(GameObject active: activeObject) {
            editorScene.removeFromSceneSafe(active);
        }

        MouseControls.unselectActiveObjects();
    }
}
