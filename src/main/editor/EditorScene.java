package main.editor;

import imgui.app.Configuration;
import main.editor.editorControl.CameraControl;
import main.editor.editorControl.Gizmo;
import main.editor.editorControl.KeyControls;
import main.editor.editorControl.MouseControls;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebDraw;
import main.renderer.DebugDraw;
import main.renderer.DrawMode;
import main.renderer.Renderer;
import main.haspid.Scene;
import main.util.AssetPool;
import main.util.Properties;

import java.util.ArrayList;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;
import static main.renderer.DrawMode.Dynamic;
import static main.renderer.DrawMode.Static;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private EditorMenuBar editorMenuBar;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private SceneHierarchy sceneHierarchy;
    private InspectorWindow inspectorWindow;
    private ArrayList<Properties> properties;
    private PropertiesWindow propertiesWindow;

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();

        loadSceneObject();
        editorMode = true;

        gridLines = new GridLines();
        gizmo = new Gizmo(this);
        sceneHierarchy = new SceneHierarchy(this);
        mouseControls = new MouseControls(this, mouseListener, gizmo);
        cameraControl = new CameraControl(camera, mouseControls);
        keyControls = new KeyControls(mouseControls, cameraControl, this);

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(Console.getInstance());
        levelEditorStuff.addComponent(gridLines);
        levelEditorStuff.addComponent(mouseControls);
        levelEditorStuff.addComponent(cameraControl);
        levelEditorStuff.addComponent(keyControls);
        levelEditorStuff.addComponent(gizmo);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        properties = new ArrayList<>();
        properties.add(AssetPool.getSpriteSheet(itemsConfig));
        properties.add(AssetPool.getSpriteSheet(smallFormConfig));
        properties.add(AssetPool.getSpriteSheet(decorationAndBlockConfig));
        properties.add(AssetPool.getSpriteSheet(pipesConfig));
        properties.add(AssetPool.getSpriteSheet(turtleConfig));
        properties.add(AssetPool.getSpriteSheet(iconConfig));
        properties.add(AssetPool.getAllSound());

        editorMenuBar = new EditorMenuBar(this);
        inspectorWindow = new InspectorWindow();
        propertiesWindow = new PropertiesWindow(mouseControls, properties);
    }

    @Override
    public void update(float dt) {
        updateDearGui();
        levelEditorStuff.update(dt);
        updateGameObject(dt);
    }

    int i = 1;
    public void render(float dt, boolean bufferIdMode){
        if(!bufferIdMode){
          //  DebugDraw.draw();
           DebDraw.notify(Draw, gridID);
           DebDraw.notify(Draw, selectorID);
        }
        getRenderer().render();


//        DebDraw.notify(SetDirty, rayCastID);
//        DebDraw.notify(Draw, rayCastID);

        if(i== 1)DebDraw.notify(SetDirty, colliderID);
        DebDraw.notify(Draw, colliderID);
        i++;

    }

    public void updateDearGui(){
        imGuiLayer.startFrame();

        editorMenuBar.display();
        sceneHierarchy.display();
        inspectorWindow.display();
        propertiesWindow.display();
        ViewPort.getInstance().display();

        imGuiLayer.endFrame();
    }

    public void destroy(){
        currentZoomValue = 1;
        mouseControls.unselectActiveObjects();
        mouseControls.removeDraggingObject();
    }

    public void disposeDearGui(){
        imGuiLayer.dispose();
    }

    public MouseControls getMouseControls(){
        return  mouseControls;
    }

    public EditorMenuBar getMenuBar(){
        return editorMenuBar;
    }
}