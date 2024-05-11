package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.renderer.DebugDraw;
import main.scene.EditorScene;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    private Window window;
    private MouseListener mouse;
    private EditorScene editorScene;
    private static GameObject draggingObject;
    private List<GameObject> activeObjectList;

    private Gizmo gizmo;
    private float xBuffer, yBuffer;
    private float debounce = 0.05f;
    private float resetDebounce = debounce;

    private GameObject selected;
    private Vector2f distance;
    private Vector2f center;
    private Vector2f startDragging;
    private Vector2f endDragging;
    private static boolean wasDraggedLastFrame;
    private Vector2f startDraggingView;
    private Vector2f startPix;
    private boolean selectorActive;

    public MouseControls(EditorScene editorScene, MouseListener mouse, Gizmo gizmo) {
        this.gizmo = gizmo;
        this.mouse = mouse;
        this.editorScene = editorScene;
        this.window = Window.getInstance();
        activeObjectList = new ArrayList<>();
    }

    @Override
    public void update(float dt) {
        //Mouse Event

        if (draggingObject != null) {
            trackMouse(dt);
        }

        if (gizmo.isHot() && activeObjectList.size() == 1 && mouse.isMouseDragging() && mouse.isCursorInsideViewPort()) {
            gizmoAction();
        }

        if (draggingObject != null && !mouse.isMouseDragging() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)) {
            removeDraggingObject();
        }

        if(draggingObject == null && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && !gizmo.isHot()){
            unselectActiveObjects();
        }

        //Mouse + Key Event
        KeyListener keyboard = KeyListener.getInstance();

        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2) && !hasDraggingObject() && !selectorActive){
            if(keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
                scanForObject(true);
            }else{
                scanForObject(false);
            }
        }

        if(!gizmo.isGizmoActive() && !keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) selectorUpdate();
    }

    public void highLightObject(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        spriteRenderer.setColor(mouseHoveColor);
    }

    public void trackMouse(float dt){
        float objectX = (int)(mouse.getWorldX()/ gridSize) * gridSize + draggingObject.getTransform().getScale().x / 2;
        float objectY = (int)(mouse.getWorldY() / gridSize) * gridSize + draggingObject.getTransform().getScale().y / 2;
        draggingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && debounce < 0){
            place();
        }
        debounce -= dt;
    }

    public void place(){
        float scan  = window.getIdBuffer().readIDFromPixel((int) mouse.getViewPortX() , (int) mouse.getViewPortY());
        if((scan == 0 || scan == draggingObject.getGameObjectID())) {
            GameObject objectClone = new GameObject(draggingObject.getName());

            for(Component c: draggingObject.getAllComponent()){
                Component compClone = c.copy();
                if(compClone != null) objectClone.addComponent(compClone);
            }
            objectClone.setTransformFromItself();

            draggingObject.getTransform().increaseZIndex();
            draggingObject = objectClone;

            editorScene.addGameObjectToScene(draggingObject);
            debounce = resetDebounce;
        }
    }

    public void pickupObject(GameObject holdingObject){
        editorScene.addGameObjectToScene(holdingObject);
        draggingObject = holdingObject;
    }

    public void gizmoAction(){
        int index = gizmo.getGizmoIndex();
        GameObject activeGameObject = activeObjectList.get(0);
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
                float xPos = (int) ((mouse.getWorldX() - (scale.x / 2)) / gridSize) * gridSize + scale.x / 2;
                float yPos = (int) ((mouse.getWorldY() - (scale.y / 2)) / gridSize) * gridSize + scale.y / 2;

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
                unselectActiveObjects();
                editorScene.clearActiveObjectList();
            }

            highLightObject(active);
            if(!activeObjectList.contains(active)) setObjectActive(active);
            if(!editorScene.getActiveGameObjectList().contains(active)) editorScene.addObjectToActiveList(active); // todo
        }

        return id;
    }

    public void selectorUpdate(){
        if(selected != null) editorScene.removeFromScene(selected);
        if(mouse.isMouseDragging() && !wasDraggedLastFrame && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)){
            selectorActive = true;
            startDragging = mouse.getWorld();
            startDraggingView = mouse.getViewPortPos();
            startPix = mouse.getMouseListenerPos();
            unselectActiveObjects();
        }else if(!mouse.isMouseDragging() && wasDraggedLastFrame){
            selectorActive = false;
            if(distance != null) {
                int startFromX = (int) startDraggingView.x;
                int startFromY = (int) startDraggingView.y;

                int width = (int)(mouse.getMouseListenerPos().x - startPix.x);
                int height = ((int)(mouse.getMouseListenerPos().y - startPix.y)) * -1;

                int endX = (int) mouse.getViewPortX();
                int endY = (int) mouse.getViewPortY();

                if(width < 0){
                    startFromX = endX;
                    width *= -1;
                }

                if(height < 0) {
                    startFromY = endY;
                    height *= -1;
                }

               HashSet<Integer> idSet = window.getIdBuffer().readIDFromPixel(startFromX, startFromY, width  + 2, height + 2);
                for(int id: idSet){
                   GameObject gameObject = editorScene.getGameObjectFromID(id);
                   if(gameObject != null) {
                       highLightObject(gameObject);
                       setObjectActive(gameObject);
                       editorScene.addObjectToActiveList(gameObject);
                   }
                }
            }
            startDragging = null;
            startDraggingView = null;
        }else if(wasDraggedLastFrame){
            endDragging = mouse.getWorld();
            if(startDragging != null && mouse.isMouseDragging() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)){
                distance = new Vector2f(endDragging.x - startDragging.x, endDragging.y - startDragging.y);
                center = new Vector2f(startDragging.x + (distance.x/ 2f), startDragging.y + (distance.y / 2f));
                DebugDraw.drawBoxes2D(selectorIndex, center, distance, 0, new Vector3f(0, 0, 0), 1);

                if(selected != null) Window.getInstance().getCurrentScene().removeFromScene(selected);
                selected = new GameObject("Selector");
                selected.setNonSerializable();
                selected.addComponent(new Transform(center, distance, 0, 100));
                selected.setTransformFromItself();
                selected.addComponent(new SpriteRenderer(mouseRectColor));
                editorScene.addGameObjectToScene(selected);

            }
        }

        wasDraggedLastFrame = mouse.isMouseDragging() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2);
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

    public void setObjectActive(GameObject active){
        activeObjectList.add(active);
    }

    public void unselectActiveObjects(){
        for(GameObject active: activeObjectList){
            SpriteRenderer spriteRenderer = active.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null) spriteRenderer.resetColor();
        }

        activeObjectList.clear();
        editorScene.clearActiveObjectList();
    }

    public void removeDraggingObject(){
        editorScene.removeFromScene(draggingObject);
        draggingObject = null;
    }

    public boolean hasDraggingObject(){
        return draggingObject != null;
    }

    public boolean hasActiveObject(){
        return !activeObjectList.isEmpty();
    }

    public boolean hasDraggingOrActiveObject(){
        return hasActiveObject() || hasDraggingObject();
    }

    public List<GameObject> getAllActiveObjects() {
        return activeObjectList;
    }

    public GameObject getDraggingObject(){
        return draggingObject;
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
