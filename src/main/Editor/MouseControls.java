package main.Editor;

import main.Helper;
import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.scene.EditorScene;
import org.joml.Vector2f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class MouseControls extends Component {
    private Window window;
    private MouseListener mouse;
    private EditorScene editorScene;
    private static GameObject holdingObject;
    private static GameObject activeGameObject;

    private Gizmo gizmo;
    private float xBuffer, yBuffer;
    private float debounce = 0.05f;
    private float resetDebounce = debounce;

    public MouseControls(EditorScene editorScene, MouseListener mouse, Gizmo gizmo) {
        this.gizmo = gizmo;
        this.mouse = mouse;
        this.editorScene = editorScene;
        this.window = Window.getInstance();
    }

    @Override
    public void update(float dt) {

        if(holdingObject != null){
            trackMouse(dt);
        }else if(gizmo.isHot() && activeGameObject != null && mouse.isMouseDragged()){
            gizmoAction();
        }else if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2) && !mouse.isMouseDragged()){
            scanForObject();
        }

        if ((Helper.isNotNull(activeGameObject) || Helper.isNotNull(holdingObject)) && mouse.isCursorInsideViewPort()) {
            if (Helper.isNotNull(getCursorObject()) && !mouse.isMouseDragged() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)) {
                clearCursor();
            }

            if(Helper.isNull(getCursorObject()) && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && !gizmo.isHot() && !mouse.isMouseDragged()){
                setActiveGameObject(null);
            }
        }
    }

    public void trackMouse(float dt){
        int objectX = (int)(mouse.getWorldX()/ gridSize) * gridSize;
        int objectY = (int)(mouse.getWorldY() / gridSize) * gridSize;
        holdingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
            place(dt);
        }
    }

    public void place(float dt){
        float scan  = window.getIdBuffer().readIDFromPixel((int) mouse.getViewPortX() , (int) mouse.getViewPortY());
        if((scan == 0 || scan == holdingObject.getGameObjectID()) && debounce < 0 ) {

            Transform t = holdingObject.getTransform();
            SpriteRenderer spriteRenderer = holdingObject.getComponent(SpriteRenderer.class);

            GameObject objectClone = new GameObject(holdingObject.getName());
            objectClone.addComponent(new SpriteRenderer(spriteRenderer.getTexture(), spriteRenderer.getWidth(), spriteRenderer.getHeight(), spriteRenderer.getSpriteCords()));
            objectClone.addComponent(t.copy());
            objectClone.setTransformFromItself();

            t.setZIndex(t.getZIndex() + 1);

            holdingObject = objectClone;
            editorScene.addGameObjectToScene(holdingObject);
            debounce = resetDebounce;
        }

        debounce -= dt;
    }

    public void pickupObject(GameObject holdingObject){
        editorScene.addGameObjectToScene(holdingObject);
        this.holdingObject = holdingObject;
    }

    public void gizmoAction(){
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
                int xPos = (int) ((mouse.getWorldX() - (scale.x / 2)) / gridSize) * gridSize;
                int yPos = (int) ((mouse.getWorldY() - (scale.y / 2)) / gridSize) * gridSize;

                if (gizmo.isXAxisHot()) transform.setPosition(xPos, transform.getPosition().y);
                if (gizmo.isYAxisHot()) transform.setPosition(transform.getPosition().x, yPos);
            } else if (index == 2) {
                if (gizmo.isXAxisHot() && (scale.x - value.x) > 0) scale.x -= value.x;
                if (gizmo.isYAxisHot() && (scale.y - value.y) > 0) scale.y -= value.y;
            }
        }
    }

    public int scanForObject(){
        int x = (int) mouse.getViewPortX();
        int y = (int) mouse.getViewPortY();
        int id = (int) window.getIdBuffer().readIDFromPixel(x, y);

        GameObject active = editorScene.getGameObjectFromID(id);
        if(active != null && active.isTriggerable()){
            activeGameObject = active;
            editorScene.setActiveGameObject(active);
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

    public void clearCursor(){
        editorScene.removeFromScene(holdingObject);
        holdingObject = null;
    }

    public boolean isHoldingObjectOccupied(){
        return holdingObject != null;
    }

    public boolean isActiveObjectOccupied(){
        return activeGameObject != null;
    }

    public boolean isMouseOccupied(){
        return isActiveObjectOccupied() || isHoldingObjectOccupied();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        MouseControls.activeGameObject = activeGameObject;
        ((EditorScene)Window.getInstance().getCurrentScene()).setActiveGameObject(null);
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
}
