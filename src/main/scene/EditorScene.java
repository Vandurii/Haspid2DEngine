package main.scene;

import imgui.ImGui;
import imgui.app.Configuration;
import main.Editor.*;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;


import java.awt.*;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private ImGuiLayer imGuiLayer;
    private GameObject levelEditorStuff;
    private PropertiesWindow propertiesWindow;

    public EditorScene() {}

    @Override
    public void init() {
        MouseControls mouseControls = MouseControls.getInstance();
        GridLines gridLines = GridLines.getInstance();

        load();
        camera = new Camera(new Vector2f(0, 0));

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(gridLines);
        levelEditorStuff.addComponent(mouseControls);

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