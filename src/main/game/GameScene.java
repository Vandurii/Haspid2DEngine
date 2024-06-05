package main.game;

import imgui.app.Configuration;
import main.components.Hitable;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.Collider;
import main.components.physicsComponent.RigidBody;
import main.editor.editorControl.EventController;
import main.editor.gui.ViewPort;
import main.haspid.*;
import main.physics.BodyType;
import main.renderer.DebugDraw;
import main.renderer.Renderer;


import javax.swing.border.EmptyBorder;

import static main.Configuration.colliderID;
import static main.Configuration.staticColliderID;
import static main.renderer.DebugDrawEvents.Clear;
import static main.renderer.DebugDrawEvents.Draw;

public class GameScene extends Scene {

    private ImGuiLayer imGuiLayer;
    private GameObject gameSceneStuff;
    public GameMenuBar gameMenuBar = new GameMenuBar();

    @Override
    public void init() {
        for(Transform t: BoxCollider.getColliderData()) {
            physics.add(createColliderObject(t));
        }

        BoxCollider.reset();
        DebugDraw.notify(Clear, colliderID);

        EventController.physic = true;
        GameKeyControls gameControls = new GameKeyControls();

        gameSceneStuff = new GameObject("gameSceneStuff");
        gameSceneStuff.addComponent(gameControls);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        loadSceneFromFile();
    }

    public GameObject createColliderObject(Transform transform){
        GameObject gameObject = new GameObject("Collider custom");
        gameObject.addComponent(transform);
        gameObject.setTransformFromItself();
        gameObject.addComponent(new BoxCollider());
        RigidBody rigidBody = new RigidBody();
        rigidBody.setBodyType(BodyType.Static);
        gameObject.addComponent(rigidBody);

        return gameObject;
    }

    @Override
    public void update(float dt) {
        gameSceneStuff.update(dt);
        sceneUpdate(dt);
        dearGui();
    }

    @Override
    public void render(float dt, boolean bufferIDMode) {
        Renderer.getInstance().render();;
     //   DebugDraw.notify(Draw, colliderID);
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
    public void destroy() {

    }
}
