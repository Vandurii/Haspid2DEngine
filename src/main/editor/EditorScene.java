package main.editor;

import imgui.app.Configuration;
import main.editor.editorControl.CameraControl;
import main.editor.editorControl.Gizmo;
import main.editor.editorControl.KeyControls;
import main.editor.editorControl.MouseControls;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.Renderer;
import main.haspid.Scene;
import main.util.AssetPool;
import main.util.Properties;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private EditorMenuBar editorMenuBar;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private SceneHierarchy sceneHierarchy;
    private InspectorWindow inspectorWindow;
    private ArrayList<Properties> properties;
    private PropertiesWindow propertiesWindow;
    private List<GameObject> activeGameObjectList;

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();
        DebugDraw.resetVertexArray();

        load();
        editorMode = true;
        activeGameObjectList = new ArrayList<>();

        gridLines = new GridLines();
        gizmo = new Gizmo(this);
        sceneHierarchy = new SceneHierarchy();
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

        properties = new ArrayList<>();
        properties.add(AssetPool.getSpriteSheet(itemsConfig));
        properties.add(AssetPool.getSpriteSheet(smallForm));
        properties.add(AssetPool.getSpriteSheet(decorationAndBlockConfig));
        properties.add(AssetPool.getAllSound());

        editorMenuBar = new EditorMenuBar(this);
        inspectorWindow = new InspectorWindow(mouseControls);
        propertiesWindow = new PropertiesWindow(mouseControls, properties);
    }

    @Override
    public void update(float dt) {
        dearGui();
        levelEditorStuff.update(dt);
        updateGameObject(dt);
    }

    public void render(float dt, boolean bufferIdMode){
        if(!bufferIdMode){
            DebugDraw.draw();
        }
        getRenderer().render();
    }

    public void dearGui(){
        imGuiLayer.startFrame();

        editorMenuBar.display();
        sceneHierarchy.display();
        inspectorWindow.display();
        propertiesWindow.display();
        ViewPort.getInstance().display();

        imGuiLayer.endFrame();
    }

    public void clear(){
        zoom = 1;
        mouseControls.unselectActiveObjects();
        mouseControls.removeDraggingObject();
    }

    public void disposeDearGui(){
        imGuiLayer.dispose();
    }

    public List<GameObject> getActiveGameObjectList() {
        return activeGameObjectList;
    }

    public void addObjectToActiveList(GameObject activeGameObject) {
        activeGameObjectList.add(activeGameObject);
    }

    public void clearActiveObjectList(){
        activeGameObjectList.clear();
    }

    public MouseControls getMouseControls(){
        return  mouseControls;
    }

    public KeyControls getKeyControls(){
        return  keyControls;
    }

    public EditorMenuBar getMenuBar(){
        return editorMenuBar;
    }

    public void setActiveGameObjectList(List<GameObject> activeGameObjectList){
        this.activeGameObjectList = activeGameObjectList;
    }
}