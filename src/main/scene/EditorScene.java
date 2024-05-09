package main.scene;

import imgui.app.Configuration;
import main.Editor.*;
import main.components.Animation;
import main.components.SpriteRenderer;
import main.components.StateMachine;
import main.haspid.*;
import main.haspid.Window;
import main.prefabs.Prefabs;
import main.renderer.DebugDraw;
import main.renderer.Renderer;
import main.util.AssetPool;
import main.util.Properties;
import main.util.SpriteSheet;
import main.util.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private MenuBar menuBar;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private GameObject activeGameObject;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private SceneHierarchy sceneHierarchy;
    private InspectorWindow inspectorWindow;
    private PropertiesWindow propertiesWindow;

    private ArrayList<Properties> properties;

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();
        DebugDraw.resetVertexArray();

        load();
        editorMode = true;

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
        properties.add(AssetPool.getSpriteSheet(firstSpriteSheet));
        properties.add(AssetPool.getSpriteSheet(decorationAndBlockConfig));
        properties.add(AssetPool.getAllSound());

        menuBar = new MenuBar();
        inspectorWindow = new InspectorWindow(mouseControls);
        propertiesWindow = new PropertiesWindow(mouseControls, properties);
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
        sceneHierarchy.display();
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