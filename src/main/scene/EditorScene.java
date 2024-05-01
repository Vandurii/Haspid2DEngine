package main.scene;

import imgui.app.Configuration;
import main.Editor.*;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.util.AssetPool;
import org.joml.Vector2f;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private ImGuiLayer imGuiLayer;
    private GameObject levelEditorStuff;
    private PropertiesWindow propertiesWindow;

    public EditorScene() {}

    @Override
    public void init() {
        camera = new Camera(new Vector2f(0, 0));

        MouseControls mouseControls = MouseControls.getInstance();
        KeyControls keyControls = KeyControls.getInstance();
        GridLines gridLines = GridLines.getInstance();
        CameraControl cameraControl = new CameraControl(camera);
        Gizmo gizmo = Gizmo.getInstance();

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(gridLines);
        levelEditorStuff.addComponent(mouseControls);
        levelEditorStuff.addComponent(cameraControl);
        levelEditorStuff.addComponent(keyControls);
        levelEditorStuff.addComponent(gizmo);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

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
        InspectorWindow.getInstance().display();
        propertiesWindow.display();

        imGuiLayer.endFrame();
    }

    public void end(){
        imGuiLayer.dispose();
    }
}