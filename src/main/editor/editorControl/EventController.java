package main.editor.editorControl;

import main.components.Component;
import main.haspid.MouseListener;

import java.util.HashMap;

public class EventController extends Component {


    public static boolean physic = false;
    public static boolean collider = true;
    public static boolean resetMode = false;
    public static boolean resourcesMonitor = true;

    public static MouseListener mouseListener;
    public static HashMap<String, Hint> hintMap = new HashMap<>();

    // declare parameters
    public static int activeObjects;
    public static boolean insideViewPort;
    public static boolean activeOrHoldingObject;
    public static boolean holdingObject;
    public static boolean isGizmoHot;

    // Declare IDs.
    public static String singleActiveID;
    public static String setSingleActiveID;
    public static String setMultiActiveID;
    public static String setActiveCtrlID;
    public static String viewProjectionID;
    public static String moveCameraID;
    public static String resetCameraID;
    public static String placeHoldingObjectID;
    public static String deleteHoldingObjectID;
    public static String deleteFromSceneID;
    public static String moveObjectsID;
    public static String copyObjectsID;
    public static String unselectActiveID;
    public static String unselectActiveEscID;
    public static String gizmoTool1ID;
    public static String gizmoTool2ID;
    public static String gizmoTool3ID;

    // Declare condition.
    public static boolean singleActiveObject;
    public static boolean setSingleActiveObject;
    public static boolean setMultiActiveObject;
    public static boolean setActiveCtrl;
    public static boolean viewProjection;
    public static boolean moveCamera;
    public static boolean resetCamera;
    public static boolean placeHoldingObject;
    public static boolean deleteHoldingObject;
    public static boolean deleteFromScene;
    public static boolean moveObjects;
    public static boolean copyObjects;
    public static boolean unselectActive;
    public static boolean unselectActiveEsc;
    public static boolean gizmoTool1;
    public static boolean gizmoTool2;
    public static boolean gizmoTool3;

    public EventController(){
        mouseListener = MouseListener.getInstance();

        // Initialize IDs.
        singleActiveID = "singleActive";
        setSingleActiveID = "setSingleActive";
        setMultiActiveID = "setMultiActive";
        setActiveCtrlID = "setCtrlActive";
        viewProjectionID = "viewProjection";
        moveCameraID = "moveCamera";
        resetCameraID = "resetCamera";
        placeHoldingObjectID = "placeHoldingObject";
        deleteHoldingObjectID = "deleteHoldingObject";
        deleteFromSceneID = "deleteFromScene";
        moveObjectsID = "moveObjects";
        copyObjectsID = "copyObjects";
        unselectActiveID = "unselectActive";
        unselectActiveEscID = "unselectActiveEsc";
        gizmoTool1ID = "gizmoTool1";
        gizmoTool2ID = "gizmoTool2";
        gizmoTool3ID = "gizmoTool3";

        // Load hints.
        loadHint(new Hint(singleActiveID, "Use '1' - '2' - '3' key to change the tool."));
        loadHint(new Hint(setSingleActiveID, "Right-click on an Object to activate it."));
        loadHint(new Hint(setMultiActiveID, "Hold right mouse button and move it to multi select."));
        loadHint(new Hint(setActiveCtrlID, "Hold 'Ctrl' key and use right mouse button to multi select."));
        loadHint(new Hint(viewProjectionID, "Use scroll to change the world scale."));
        loadHint(new Hint(moveCameraID, "Hold left mouse button and move it to control camera."));
        loadHint(new Hint(resetCameraID, "Press 'R' to reset camera position."));
        loadHint(new Hint(placeHoldingObjectID, "Left-click to place the object."));
        loadHint(new Hint(deleteHoldingObjectID, "Right-click or 'Esc' to remove the object."));
        loadHint(new Hint(deleteFromSceneID, "Press 'Del' key to remove selected object from scene"));
        loadHint(new Hint(moveObjectsID, "Press arrow key to move the selected objects."));
        loadHint(new Hint(copyObjectsID, "Press 'c' key to copy selected objects."));
        loadHint(new Hint(unselectActiveID, "Left-click to deactivate objects."));
        loadHint(new Hint(unselectActiveEscID, "Press 'Esc' key to deactivate objects."));
        loadHint(new Hint(gizmoTool1ID, "Hold left mouse button and move it to change position of the object."));
        loadHint(new Hint(gizmoTool2ID, "Hold left mouse button and move it to change scale of the object."));
        loadHint(new Hint(gizmoTool3ID, "Hold left mouse button and move it to change scale of the object."));
    }

    @Override
    public void update(float dt) {
        // init parameters
        activeObjects = MouseControls.getAllActiveObjects().size();
        activeOrHoldingObject = MouseControls.hasDraggingOrActiveObject();
        insideViewPort = mouseListener.isCursorInsideViewPort();
        holdingObject = MouseControls.hasDraggingObject();
        isGizmoHot = Gizmo.isHot();

        // Initialize conditions.
        singleActiveObject = activeObjects == 1;
        setSingleActiveObject = !activeOrHoldingObject;
        setMultiActiveObject = insideViewPort && !activeOrHoldingObject;
        setActiveCtrl = !holdingObject;
        viewProjection = insideViewPort && !activeOrHoldingObject;
        moveCamera = insideViewPort && !activeOrHoldingObject;
        resetCamera = insideViewPort && !activeOrHoldingObject;
        placeHoldingObject = insideViewPort && holdingObject;
        deleteHoldingObject = insideViewPort && holdingObject;
        deleteFromScene = activeObjects > 0;
        moveObjects = activeObjects > 0;
        copyObjects = activeObjects > 0;
        unselectActive = insideViewPort && activeObjects > 0 && !isGizmoHot;
        unselectActiveEsc = activeObjects > 0;
        gizmoTool1 = isGizmoHot && (Gizmo.getGizmoToolIndex() == 1);
        gizmoTool2 = isGizmoHot && (Gizmo.getGizmoToolIndex() == 2);
        gizmoTool3 = isGizmoHot && (Gizmo.getGizmoToolIndex() == 0);

        // Update hints condition.
        hintMap.get(singleActiveID).setEnabled(singleActiveObject);
        hintMap.get(setSingleActiveID).setEnabled(setSingleActiveObject);
        hintMap.get(setMultiActiveID).setEnabled(setMultiActiveObject);
        hintMap.get(setActiveCtrlID).setEnabled(setActiveCtrl);
        hintMap.get(viewProjectionID).setEnabled(viewProjection);
        hintMap.get(moveCameraID).setEnabled(moveCamera);
        hintMap.get(resetCameraID).setEnabled(resetCamera);
        hintMap.get(placeHoldingObjectID).setEnabled(placeHoldingObject);
        hintMap.get(deleteHoldingObjectID).setEnabled(deleteHoldingObject);
        hintMap.get(deleteFromSceneID).setEnabled(deleteFromScene);
        hintMap.get(moveObjectsID).setEnabled(moveObjects);
        hintMap.get(copyObjectsID).setEnabled(copyObjects);
        hintMap.get(unselectActiveID).setEnabled(unselectActive);
        hintMap.get(unselectActiveEscID).setEnabled(unselectActiveEsc);
        hintMap.get(gizmoTool1ID).setEnabled(gizmoTool1);
        hintMap.get(gizmoTool2ID).setEnabled(gizmoTool2);
        hintMap.get(gizmoTool3ID).setEnabled(gizmoTool3);
    }


    public void loadHint(Hint hint){
        hintMap.put(hint.ID, hint);
    }

    public class Hint{
        private String text;
        private String ID;
        private boolean enabled;

        public Hint(String ID, String text){
            this.text = text;
            this.ID = ID;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }
    }
}
