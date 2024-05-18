package main.game;

import imgui.app.Configuration;
import main.editor.EditorMenuBar;
import main.editor.ViewPort;
import main.haspid.GameObject;
import main.haspid.ImGuiLayer;
import main.haspid.Window;
import main.renderer.Renderer;
import main.haspid.Scene;

import static main.Configuration.gameClearColor;

public class GameScene extends Scene {

    private ImGuiLayer imGuiLayer;
    private GameObject gameSceneStuff;
    public GameMenuBar gameMenuBar = new GameMenuBar();

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

        removeDeadObject();
    }

    @Override
    public void render(float dt, boolean bufferIDMode) {
        Renderer.getInstance().render();;
    }

    public void dearGui(){
        imGuiLayer.startFrame();
        ViewPort.getInstance().display();
        gameMenuBar.display();
        imGuiLayer.endFrame();
    }

    public void disposeDearGui(){
        imGuiLayer.dispose();
    }

    @Override
    public void clear() {

    }
}
