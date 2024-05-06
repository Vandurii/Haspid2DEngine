package main.scene;

import imgui.ImGui;
import imgui.app.Configuration;
import main.Editor.*;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.Renderer;
import main.util.AssetPool;
import org.joml.Vector2f;

import static main.Configuration.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private KeyControls keyControls;
    private GameObject activeGameObject;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private InspectorWindow inspectorWindow;
    private PropertiesWindow propertiesWindow;
    public MenuBar menuBar = new MenuBar();

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        MouseListener mouseListener = MouseListener.getInstance();

        editorMode = true;
        load();

        gizmo = new Gizmo(this);
        gridLines = GridLines.getInstance();
        mouseControls = new MouseControls(this, mouseListener, gizmo);
        cameraControl = new CameraControl(camera, mouseControls);
        keyControls = new KeyControls(mouseControls, cameraControl, this);

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(gridLines);
        levelEditorStuff.addComponent(mouseControls);
        levelEditorStuff.addComponent(cameraControl);
        levelEditorStuff.addComponent(keyControls);
        levelEditorStuff.addComponent(gizmo);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        inspectorWindow = new InspectorWindow(mouseControls);
        propertiesWindow = new PropertiesWindow(mouseControls, AssetPool.getSpriteSheet(decorationAndBlockConfig));
    }

    @Override
    public void update(float dt) {
        dearGui();
        levelEditorStuff.update(dt);

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }
    }

    public void render(float dt, boolean bufferIdMode){
        if(!bufferIdMode){
            DebugDraw.draw();
        }
        getRenderer().render();
    }

    public void dearGui(){
        imGuiLayer.startFrame();
        menuBar.display();
        inspectorWindow.display();
        propertiesWindow.display();
        ViewPort.getInstance().display();

        imGuiLayer.endFrame();
    }

    public void end(){
        zoom = 1;
        mouseControls.clearCursor();
        mouseControls.setActiveGameObject(null);

        imGuiLayer.dispose();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        this.activeGameObject = activeGameObject;
    }

    public MouseControls getMouseControls(){
        return  mouseControls;
    }

    public KeyControls getKeyControls(){
        return  keyControls;
    }
}