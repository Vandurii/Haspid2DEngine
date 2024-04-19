package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.scene.Scene;
import org.joml.Vector2f;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class MouseControls extends Component {
    private static MouseControls instance;
    private static MouseListener mouse;
    private static GameObject holdingObject;
    private static GameObject activeGameObject;
    private static Scene scene;
    private static Gizmo gizmo;
    private float xBuffer, yBuffer;
    private Window window;

    private MouseControls() {
        scene = Window.getInstance().getCurrentScene();
        gizmo = Gizmo.getInstance();
        mouse = MouseListener.getInstance();
        window = Window.getInstance();
    }

    public static MouseControls getInstance(){
        if(instance == null) instance = new MouseControls();

        return instance;
    }

    @Override
    public void update(float dt) {

        if(holdingObject != null){
            trackMouse();
        }else if(gizmo.isHot() && activeGameObject != null && mouse.isMouseDragged()){
            gizmoAction();
        }else if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2) && !mouse.isMouseDragged()){
            scanForObject();
        }
    }

    public void trackMouse(){
        int objectX = (int)(mouse.getWorldX()/ gridSize) * gridSize;
        int objectY = (int)(mouse.getWorldY() / gridSize) * gridSize;
        holdingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
            place();
        }
    }

    public void place(){
       // if(holdingObject != null)System.out.println(holdingObject.getGameObjectID());
        float scan  = window.getIdBuffer().readIDFromPixel((int) mouse.getViewPortX() , (int) mouse.getViewPortY());
        if(scan == 0 ) {
            Transform t = holdingObject.getTransform();
            SpriteRenderer spriteRenderer = holdingObject.getComponent(SpriteRenderer.class);

            GameObject objectClone = new GameObject(holdingObject.getName(), new Transform(t.getPosition(), t.getScale()), holdingObject.getzIndex());
            objectClone.addComponent(new SpriteRenderer(spriteRenderer.getSprite()));

            holdingObject = objectClone;
            scene.addGameObjectToScene(objectClone);
        }
    }

    public void pickupObject(GameObject holdingObject){
        scene.addGameObjectToScene(holdingObject);
        this.holdingObject = holdingObject;
    }

    public void gizmoAction(){
        int index = gizmo.getGizmoIndex();

        Vector2f delta = mouse.getDelta();
        float x  = (delta.x * zoom);
        float y  = (delta.y * zoom);
        Vector2f value = addToBuffer(x, y);

        if(index == 0){
            activeGameObject.getTransform().getScale().x -= value.x;
            activeGameObject.getTransform().getScale().y -= value.x;
        }else if(index == 1){
            activeGameObject.getTransform().getPosition().x -= value.x;
            activeGameObject.getTransform().getPosition().y -= value.y;
        }else if(index == 2){
            activeGameObject.getTransform().getScale().x -= value.x;
            activeGameObject.getTransform().getScale().y -= value.y;
        }
    }

    public void scanForObject(){
        int x = (int) mouse.getViewPortX();
        int y = (int) mouse.getViewPortY();
        int id = (int) window.getIdBuffer().readIDFromPixel(x, y);

        GameObject active = scene.getGameObjectFromID(id);
        if(active != null && active.isTriggerable()) activeGameObject = active;
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
        scene.removeFromScene(holdingObject);
        holdingObject = null;
    }

    public boolean isHoldingObjectOccupied(){
        return holdingObject != null;
    }

    public boolean isActiveObjectOccupied(){
        return activeGameObject != null;
    }

    public boolean isMouseListenerOccupied(){
        return isActiveObjectOccupied() || isHoldingObjectOccupied();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        MouseControls.activeGameObject = activeGameObject;
    }

    public GameObject getCursorObject(){
        return holdingObject;
    }
}
