package main.editor;

import imgui.app.Configuration;
import main.editor.editorControl.CameraControl;
import main.editor.editorControl.Gizmo;
import main.editor.editorControl.KeyControls;
import main.editor.editorControl.MouseControls;
import main.editor.gui.*;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.Renderer;
import main.haspid.Scene;
import main.util.AssetPool;
import main.util.Properties;

import java.util.ArrayList;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private HelpPanel helpPanel;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private EditorMenuBar editorMenuBar;
    private ConsoleWindow consoleWindow;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private InspectorWindow inspectorWindow;
    private ArrayList<Properties> properties;
    private PropertiesWindow propertiesWindow;
    private ResourcesManager resourcesManager;

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();

        loadSceneObject();
        editorMode = true;

        gridLines = new GridLines();
        gizmo = new Gizmo(this);
        helpPanel = new HelpPanel(this);
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
      //  addGameObjectToScene(levelEditorStuff);

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

        consoleWindow = new ConsoleWindow();
        inspectorWindow = new InspectorWindow();
        editorMenuBar = new EditorMenuBar(this);
        resourcesManager = new ResourcesManager(this);
        propertiesWindow = new PropertiesWindow(mouseControls, properties);
    }

    @Override
    public void update(float dt) {
        addComponentToObject();
        removeComponentFromObject();

        updateDearGui();
        levelEditorStuff.update(dt);
        updateGameObject(dt);
    }

    public void render(float dt, boolean bufferIdMode){
        if(!bufferIdMode){
           DebugDraw.notify(Draw, gridID);
           DebugDraw.notify(Draw, selectorID);
        }
        getRenderer().render();

        // todo check rays cast
//        DebDraw.notify(SetDirty, rayCastID);
//        DebDraw.notify(Draw, rayCastID);

        DebugDraw.notify(Draw, colliderID);
    }

    public void updateDearGui(){
        imGuiLayer.startFrame();

        helpPanel.display();
        consoleWindow.Display();
        editorMenuBar.display();
        inspectorWindow.display();
        propertiesWindow.display();
        resourcesManager.Display();
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