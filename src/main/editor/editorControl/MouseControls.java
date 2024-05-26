package main.editor.editorControl;

import main.Configuration;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.physicsComponent.BoxCollider;
import main.editor.EditorMenuBar;
import main.haspid.*;
import main.renderer.DebDraw;
import main.renderer.DebugDraw;
import main.editor.EditorScene;
import main.renderer.DebugDrawEvents;
import main.renderer.DrawMode;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.*;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;
import static main.renderer.DrawMode.Dynamic;
import static main.renderer.DrawMode.Static;
import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    private Window window;
    private MouseListener mouse;
    private EditorScene editorScene;
    private static GameObject draggingObject;
    private static List<GameObject> activeObjectList;

    private Gizmo gizmo;
    private double xBuffer, yBuffer;


    private double placeObjectCooldown;
    private double resetPlaceObjectCooldown;

    private boolean draggingWindow;
    private Vector2d startDraggingWindow;

    private double controlDebounce;
    private double resetControlDebounce = 0.00;

    private Vector2d distance;
    private Vector2d center;
    private GameObject selector;
    private Vector2d endDragging;
    private boolean selectorActive;
    private Vector2d startDraggingWMode;
    private Vector2d startDraggingVMode;
    private Vector2d startDraggingMMode;
    private static boolean wasDraggedLastFrame;
    private static HashMap<Vector2d, GameObject> objDistanceFromCursorMap;

    public MouseControls(EditorScene editorScene, MouseListener mouse, Gizmo gizmo) {
        this.resetPlaceObjectCooldown = Configuration.placeObjectCooldown;
        this.placeObjectCooldown = resetPlaceObjectCooldown;

        this.gizmo = gizmo;
        this.mouse = mouse;
        this.editorScene = editorScene;
        this.window = Window.getInstance();
        this.activeObjectList = new ArrayList<>();
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

        if(draggingObject == null && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && !gizmo.isHot() &&  mouse.isCursorInsideViewPort()){
            unselectActiveObjects();
        }

        if (mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && !editorScene.getMenuBar().getMaximizedMode() && (isCursorInsideMenuBar() || draggingWindow)) {
            dragWindow();
            controlDebounce = resetControlDebounce;
        }else{
            draggingWindow = false;
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

        if(!gizmo.isGizmoActive() && !keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && draggingObject == null){
            selectorUpdate();
        }

        if (mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && keyboard.isKeyPressed(GLFW_KEY_P)) {
            //isSquareOccupied();
        }

        controlDebounce -= dt;
    }

    public void dragWindow(){
        int[] posX = new int[1];
        int[] posY = new int[1];
        long glfw = window.getGlfwWindow();
        glfwGetWindowPos(glfw, posX, posY);

        if(!draggingWindow){
            draggingWindow = true;
            startDraggingWindow = mouse.getMouseListenerPos();
        }

        int deltaX = (int) (posX[0] + (mouse.getX() - startDraggingWindow.x));
        int deltaY = (int) (posY[0] + (mouse.getY() - startDraggingWindow.y));
        glfwSetWindowPos(glfw, deltaX, deltaY);
    }

    public boolean isCursorInsideMenuBar(){
        EditorMenuBar editorMenuBar = editorScene.getMenuBar();
        float width = editorMenuBar.getWindowSize().x;
        float height = editorMenuBar.getWindowSize().y;
        float startX = editorMenuBar.getWindowPos().x;
        float startY = editorMenuBar.getWindowPos().y;

        return mouse.getX() > startX && mouse.getX() < startX + width
                &&  mouse.getY() > startY && mouse.getY() < startY + height;
    }

    public void highLightObject(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        spriteRenderer.setHighLight(true);
        spriteRenderer.setColor(selectorHoverColor);
    }

    public void highLightObject(List<GameObject> objectList){
        for(GameObject gameObject: objectList){
            highLightObject(gameObject);
        }
    }

    public void trackMouse(float dt){
        Vector2d scale = draggingObject.getTransform().getScale();
        double xPos = (int)(mouse.getWorldX()/ gridSize) * gridSize + scale.x / 2;
        double yPos = (int)(mouse.getWorldY() / gridSize) * gridSize + scale.y / 2;

        // subtract one grid because we start from 0 in this case
        if(mouse.getWorldY() < 0) yPos -= gridSize;
        if(mouse.getWorldX() < 0) xPos -= gridSize;

        draggingObject.getTransform().setPosition(new Vector2d(xPos, yPos));
        if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1) && placeObjectCooldown < 0 && mouse.isCursorInsideViewPort()){
            place();
            placeObjectCooldown = resetPlaceObjectCooldown;
        }
        placeObjectCooldown -= dt;
    }

    public void trackMouseMultiple(){
        for(Map.Entry<Vector2d, GameObject> entry: objDistanceFromCursorMap.entrySet()){
            Vector2d pos = entry.getValue().getTransform().getPosition();
            Vector2d scale = entry.getValue().getTransform().getScale();

            double objectX = (int)((mouse.getWorldX() - entry.getKey().x)/ gridSize) * gridSize + scale.x / 2;
            double objectY = (int)((mouse.getWorldY() - entry.getKey().y) / gridSize) * gridSize + scale.y / 2;
            pos.set(objectX, objectY);
        }
    }

    public void initObjDistanceFromCursor(){
        objDistanceFromCursorMap = new HashMap<>();

        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;
        for(GameObject gameObject: getAllActiveObjects()){
            Vector2d pos = gameObject.getTransform().getPosition();
            if(pos.x < minX) minX = pos.x;
            if(pos.y < minY) minY = pos.y;
        }

        System.out.println();
        for(GameObject gameObject: getAllActiveObjects()){
            Vector2d pos = gameObject.getTransform().getPosition();
            Vector2d distance = new Vector2d(pos.x - minX, pos.y - minY);
            objDistanceFromCursorMap.put(distance, gameObject);
        }
    }

    public void resetObjDistanceFromCursor(){
        objDistanceFromCursorMap = null;
    }

    public void place(){
//        float scan  = window.getIdBuffer().readIDFromPixel((int) mouse.getViewPortX() , (int) mouse.getViewPortY());
//        if((scan == 0 || scan == draggingObject.getGameObjectID())) {
        if(!isSquareOccupied(mouse.getViewPortX(), mouse.getViewPortY(), draggingObject.getGameObjectID())){
            GameObject objectClone = new GameObject(draggingObject.getName());

            for(Component c: draggingObject.getAllComponent()){
                Component compClone = c.copy();
                if(compClone != null) objectClone.addComponent(compClone);
            }
            objectClone.setTransformFromItself();

            draggingObject.getTransform().increaseZIndex();
            draggingObject = objectClone;

            editorScene.addGameObjectToScene(draggingObject);
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
        Vector2d scale = transform.getScale();

        Vector2d delta = mouse.getDelta();
        double x  = (delta.x * currentZoomValue);
        double y  = (delta.y * currentZoomValue);
        Vector2d value = addToBuffer(x, y);

        if(mouse.isCursorInsideViewPort()) {
            if (index == 0) {
                double val = Math.abs(value.x) > Math.abs(value.y) ? value.x : value.y;
                if (scale.x - val > 0 && scale.y - val > 0) {
                    scale.x -= val;
                    scale.y -= val;
                }
            } else if (index == 1 && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                double xPos = (int) ((mouse.getWorldX() - (scale.x / 2)) / gridSize) * gridSize + scale.x / 2;
                double yPos = (int) ((mouse.getWorldY() - (scale.y / 2)) / gridSize) * gridSize + scale.y / 2;

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

        GameObject active = editorScene.getObjectByID(id);
        if(active != null && active.isTriggerable()){

            if(!multipleMode){
                unselectActiveObjects();
            }

            highLightObject(active);
            if(!activeObjectList.contains(active)) setObjectActive(active);
        }

        return id;
    }

    public boolean isSquareOccupied(double viewPortX, double viewPortY, int draggingObjectID){
        // number of current grids in x axis
        double currentGridX = uProjectionDimension.x * currentZoomValue / Configuration.gridSize;
        // size of currentXGrid;
        double gridSizeX = 1440 / currentGridX; // todo delete magic number

        // number of current grids in y axis
        double currentGridY = uProjectionDimension.y * currentZoomValue / Configuration.gridSize;
        // size of currentYGrid;
        double gridSizeY = 810 / currentGridY; // todo delete magic number

        // start scan from this x,y position
        int x = (int)(((int) (viewPortX / gridSizeX)) * gridSizeX);
        int y = (int)(((int) (viewPortY / gridSizeY)) * gridSizeY);
        HashSet<Integer> idSet = window.getIdBuffer().readIDFromPixel(x + pixOffsetByCheckingSquare, y + pixOffsetByCheckingSquare, (int)gridSizeX - pixOffsetByCheckingSquare, (int)gridSizeY - pixOffsetByCheckingSquare);

        idSet.remove(0);
        idSet.remove(draggingObjectID);
        idSet.remove(1); /// todo what is id 1?

        return !idSet.isEmpty();
    }

    public void selectorUpdate(){
        if(selector != null) editorScene.removeFromScene(selector);
        if(mouse.isMouseDragging() && !wasDraggedLastFrame && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)){
            selectorActive = true;
            startDraggingWMode = mouse.getWorld();
            startDraggingVMode = mouse.getViewPortPos();
            startDraggingMMode = mouse.getMouseListenerPos();
            unselectActiveObjects();
        }else if(!mouse.isMouseDragging() && wasDraggedLastFrame){
            selectorActive = false;
            DebDraw.notify(Disable, selectorID);
            if(distance != null) {
                int startFromX = (int) startDraggingVMode.x;
                int startFromY = (int) startDraggingVMode.y;

                int width = (int)((mouse.getMouseListenerPos().x - startDraggingMMode.x) * selectorScale);
                int height = (int)((mouse.getMouseListenerPos().y - startDraggingMMode.y) * -1 * selectorScale);

                if(width < 0){
                    startFromX = (int) mouse.getViewPortX();
                    width *= -1;
                }

                if(height < 0) {
                    startFromY = (int) mouse.getViewPortY();
                    height *= -1;
                }

                HashSet<Integer> idSet = window.getIdBuffer().readIDFromPixel(startFromX, startFromY, width  + 2, height + 2);
                for(int id: idSet){
                   GameObject gameObject = editorScene.getObjectByID(id);
                   if(gameObject != null) {
                       highLightObject(gameObject);
                       setObjectActive(gameObject);
                   }
                }
            }
            startDraggingWMode = null;
            startDraggingVMode = null;
        }else if(wasDraggedLastFrame){
            endDragging = mouse.getWorld();
            if(startDraggingWMode != null && mouse.isMouseDragging() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2)){
                distance = new Vector2d(endDragging.x - startDraggingWMode.x, endDragging.y - startDraggingWMode.y);
                center = new Vector2d(startDraggingWMode.x + (distance.x / 2), startDraggingWMode.y + (distance.y / 2));
                DebDraw.notify(Clear, selectorID);
                DebDraw.notify(Enable, selectorID);
                DebDraw.addBox(center, distance, 0, new Vector3f(0, 0, 0), selectorID, selectorZIndex, Static);
                DebDraw.notify(SetDirty, selectorID);
                if(selector != null) Window.getInstance().getCurrentScene().removeFromScene(selector);
                selector = new GameObject("Selector");
                selector.setNonSerializable();
                selector.addComponent(new Transform(center, distance, 0, -100));
                selector.setTransformFromItself();
                selector.addComponent(new SpriteRenderer(selectorBorderColor));
                editorScene.addGameObjectToScene(selector);
            }
        }

        wasDraggedLastFrame = mouse.isMouseDragging() && mouse.isButtonPressed(GLFW_MOUSE_BUTTON_2);
    }

    public Vector2d addToBuffer(double x, double y){
        xBuffer += x;
        yBuffer += y;

        double xv = 0;
        double yv = 0;
        if(Math.abs(xBuffer) >= gridSize){
            xv = xBuffer > 0 ? gridSize * -1: gridSize;
            resetXBuffer();
        }

        if(Math.abs(yBuffer) >= gridSize){
            yv = yBuffer > 0 ? gridSize * -1 : gridSize;
            resetYBuffer();
        }

        return new Vector2d(xv, yv);
    }

    public void resetXBuffer(){
        xBuffer = 0;
    }

    public void resetYBuffer(){
        yBuffer = 0;
    }

    public void setObjectActive(GameObject active){
        if(!activeObjectList.contains(active)) activeObjectList.add(active);

        // highLight when there is more then 1 active object
        if(activeObjectList.size() == 2){
            highLightObject(activeObjectList);
        }else if(activeObjectList.size() > 2){
            highLightObject(active);
        }
    }

    public void unselectActiveObject(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) spriteRenderer.resetColor();
        removeFromActiveList(gameObject);
    }

    public void removeFromActiveList(GameObject gameObject){
        activeObjectList.remove(gameObject);
    }

    public void setObjectActive(List<GameObject> cloneList){
        unselectActiveObjects();
        activeObjectList = cloneList;
        highLightObject(activeObjectList);
    }

    public boolean isDistanceMapLoaded(){
        return objDistanceFromCursorMap != null;
    }

    public void unselectActiveObjects(){
        for(GameObject active: activeObjectList){
            SpriteRenderer spriteRenderer = active.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null) spriteRenderer.resetColor();
        }

        activeObjectList.clear();
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

    public static List<GameObject> getAllActiveObjects() {
        return activeObjectList;
    }

    public GameObject getDraggingObject(){
        return draggingObject;
    }

    public Gizmo getGizmo() {
        return gizmo;
    }

    public MouseListener getMouseListener(){
        return mouse;
    }
}
