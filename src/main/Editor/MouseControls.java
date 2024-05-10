package main.Editor;

import main.Helper;
import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.scene.EditorScene;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class MouseControls extends Component {
    private Window window;
    private MouseListener mouse;
    private EditorScene editorScene;
    private static GameObject holdingObject;
    private List<GameObject> activeGameObjectList;

    private Gizmo gizmo;
    private float xBuffer, yBuffer;
    private float debounce = 0.05f;
    private float resetDebounce = debounce;

    public MouseControls(EditorScene editorScene, MouseListener mouse, Gizmo gizmo) {
        this.gizmo = gizmo;
        this.mouse = mouse;
        this.editorScene = editorScene;
        this.window = Window.getInstance();
        activeGameObjectList = new ArrayList<>();
    }

    @Override
    public void update(float dt) {

        if(holdingObject != null){
            trackMouse(dt);
        }else if(gizmo.isHot() && activeGameObjectList.size() == 1 && mouse.isMouseDragged()){
            gizmoAction();
        }else if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
            //System.out.println(String.format("x:%.1f  y:%.1f", MouseListener.getInstance().getWorldX(), MouseListener.getInstance().getWorldY()));
        }

        if ((!activeGameObjectList.isEmpty() || Helper.isNotNull(holdingObject)) && mouse.isCursorInsideViewPort()) {
            if (Helper.isNotNull(getCursorObject()) && !mouse.isMouseDragged() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)) {
                clearCursor();
            }

            if(Helper.isNull(getCursorObject()) && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && !gizmo.isHot() && !mouse.isMouseDragged()){
               clearActiveObjectList();
            }
        }
    }

    public void highLightObject(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        spriteRenderer.setColor(mouseHoveColor);
    }

    public void trackMouse(float dt){
        float objectX = (int)(mouse.getWorldX()/ gridSize) * gridSize + holdingObject.getTransform().getScale().x / 2;
        float objectY = (int)(mouse.getWorldY() / gridSize) * gridSize + holdingObject.getTransform().getScale().y / 2;
        holdingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && debounce < 0){
            place();
        }
        debounce -= dt;
    }

    public void place(){
        float scan  = window.getIdBuffer().readIDFromPixel((int) mouse.getViewPortX() , (int) mouse.getViewPortY());
        if((scan == 0 || scan == holdingObject.getGameObjectID())) {
            GameObject objectClone = new GameObject(holdingObject.getName());

            for(Component c: holdingObject.getAllComponent()){
                Component compClone = c.copy();
                if(compClone != null) objectClone.addComponent(compClone);
            }
            objectClone.setTransformFromItself();

            holdingObject.getTransform().increaseZIndex();
            holdingObject = objectClone;

            editorScene.addGameObjectToScene(holdingObject);
            debounce = resetDebounce;
        }
    }

    public void pickupObject(GameObject holdingObject){
        editorScene.addGameObjectToScene(holdingObject);
        this.holdingObject = holdingObject;
    }

    public void gizmoAction(){
        GameObject activeGameObject = activeGameObjectList.get(0);
        int index = gizmo.getGizmoIndex();
        Transform transform = activeGameObject.getTransform();
        Vector2f scale = transform.getScale();

        Vector2f delta = mouse.getDelta();
        float x  = (delta.x * zoom);
        float y  = (delta.y * zoom);
        Vector2f value = addToBuffer(x, y);

        if(mouse.isCursorInsideViewPort()) {
            if (index == 0) {
                float val = Math.abs(value.x) > Math.abs(value.y) ? value.x : value.y;
                if (scale.x - val > 0 && scale.y - val > 0) {
                    scale.x -= val;
                    scale.y -= val;
                }
            } else if (index == 1 && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                float xPos = (int) ((mouse.getWorldX() - (scale.x / 2)) / gridSize) * gridSize + activeGameObject.getTransform().getScale().x / 2;
                float yPos = (int) ((mouse.getWorldY() - (scale.y / 2)) / gridSize) * gridSize + activeGameObject.getTransform().getScale().y / 2;

                if (gizmo.isXAxisHot()) transform.setPosition(xPos, transform.getPosition().y);
                if (gizmo.isYAxisHot()) transform.setPosition(transform.getPosition().x, yPos);
            } else if (index == 2) {
                if (gizmo.isXAxisHot() && (scale.x - value.x) > 0) scale.x -= value.x;
                if (gizmo.isYAxisHot() && (scale.y - value.y) > 0) scale.y -= value.y;
            }
        }
    }

    public int scanForObject(boolean multipleMode){
        int x = (int) mouse.getViewPortX();
        int y = (int) mouse.getViewPortY();
        int id = (int) window.getIdBuffer().readIDFromPixel(x, y);

        GameObject active = editorScene.getGameObjectFromID(id);
        if(active != null && active.isTriggerable()){

            if(!multipleMode){
                clearActiveObjectList();
                editorScene.clearActiveObjectList();
            }

            highLightObject(active);
            if(!activeGameObjectList.contains(active)) addObjectToActiveList(active);
            if(!editorScene.getActiveGameObjectList().contains(active)) editorScene.addObjectToActiveList(active); // todo
        }

        return id;
    }

    public Vector2f addToBuffer(float x, float y){
        xBuffer += x;
        yBuffer += y;

        float xv = 0;
        float yv = 0;
        if(Math.abs(xBuffer) >= gridSize){
            xv = xBuffer > 0 ? gridSize * -1: gridSize;
            resetXBuffer();
        }

        if(Math.abs(yBuffer) >= gridSize){
            yv = yBuffer > 0 ? gridSize * -1 : gridSize;
            resetYBuffer();
        }

        return new Vector2f(xv, yv);
    }

    public void resetXBuffer(){
        xBuffer = 0;
    }

    public void resetYBuffer(){
        yBuffer = 0;
    }

    public void clearActiveObjectList(){
        for(GameObject active: activeGameObjectList){
            SpriteRenderer spriteRenderer = active.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null) spriteRenderer.resetColor();
        }

        activeGameObjectList.clear();
        editorScene.clearActiveObjectList();
    }

    public void clearCursor(){
        editorScene.removeFromScene(holdingObject);
        holdingObject = null;
    }

    public boolean isHoldingObjectOccupied(){
        return holdingObject != null;
    }

    public boolean isActiveObjectOccupied(){
        return !activeGameObjectList.isEmpty();
    }

    public boolean isMouseOccupied(){
        return isActiveObjectOccupied() || isHoldingObjectOccupied();
    }

    public List<GameObject> getActiveGameObject() {
        return activeGameObjectList;
    }

    public void addObjectToActiveList(GameObject active){
        activeGameObjectList.add(active);
      //  ((EditorScene)Window.getInstance().getCurrentScene()).clearActiveObjectList(); // todo
    }

    public GameObject getCursorObject(){
        return holdingObject;
    }

    public Gizmo getGizmo() {
        return gizmo;
    }

    public void setGizmo(Gizmo gizmo) {
        this.gizmo = gizmo;
    }

    public MouseListener getMouseListener(){
        return mouse;
    }
}
