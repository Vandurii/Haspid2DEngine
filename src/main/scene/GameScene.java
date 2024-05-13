package main.scene;

import imgui.app.Configuration;
import main.editor.MenuBar;
import main.editor.ViewPort;
import main.game.GameKeyControls;
import main.haspid.GameObject;
import main.haspid.ImGuiLayer;
import main.haspid.Window;
import main.renderer.Renderer;

public class GameScene extends Scene {

    private ImGuiLayer imGuiLayer;
    private GameObject gameSceneStuff;
    public MenuBar menuBar = new MenuBar();

    @Override
    public void init() {
        GameKeyControls gameControls = new GameKeyControls();

        gameSceneStuff = new GameObject("gameSceneStuff");
        gameSceneStuff.addComponent(gameControls);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        load();
    }

    @Override
    public void update(float dt) {
        dearGui();
        gameSceneStuff.update(dt);
        physics.update(dt);

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }
    }

    @Override
    public void render(float dt, boolean bufferIDMode) {
        Renderer.getInstance().render();;
    }

    public void dearGui(){
        imGuiLayer.startFrame();
        ViewPort.getInstance().display();
        menuBar.display();
        imGuiLayer.endFrame();
    }

    public void end(){
        imGuiLayer.dispose();
    }
}
