package main.scene;

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

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        MouseListener mouseListener = MouseListener.getInstance();

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

        load();

//        GameObject gameObject = new GameObject("temp");
//        gameObject.addComponent(new SpriteRenderer(colorRedAlpha));
//        gameObject.addComponent(new Transform(new Vector2f(300, 300), new Vector2f(32, 32)));
//        gameObject.addComponent(new RigidBody(3, 5f, new Vector3f(9, 9, 9), new Vector4f(1, 1, 1, 1)));
//        gameObject.setTransformFromItself();
//        addGameObjectToScene(gameObject);

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
        ViewPort.getInstance().display();
        inspectorWindow.display();
        propertiesWindow.display();

        imGuiLayer.endFrame();
    }

    public void end(){
        imGuiLayer.dispose();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        this.activeGameObject = activeGameObject;
    }
}