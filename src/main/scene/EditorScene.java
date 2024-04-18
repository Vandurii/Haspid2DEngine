package main.scene;

import imgui.app.Configuration;
import main.Editor.*;
import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.RenderBatch;
import main.renderer.Renderer;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2f;
import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class EditorScene extends Scene {
    private ImGuiLayer imGuiLayer;
    private GameObject levelEditorStuff;
    private PropertiesWindow propertiesWindow;
    private SpriteSheet gizmos;

    public EditorScene() {}

    @Override
    public void init() {
        gizmos = AssetPool.getSpriteSheet(gizmosConfig);

        load();

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
    }

    @Override
    public void update(float dt) {
        dearGui();
        levelEditorStuff.update(dt);
        InspectorWindow.getInstance().update();

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