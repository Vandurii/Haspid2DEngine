package main.editor;

import imgui.app.Configuration;
import main.editor.editorControl.*;
import main.editor.gui.*;
import main.haspid.*;
import main.haspid.Window;
import main.renderer.DebugDraw;
import main.renderer.Renderer;
import main.haspid.Scene;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private Creator creator;
    private Settings settings;
    private HelpPanel helpPanel;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private FileDialog fileDialog;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private EditorMenuBar editorMenuBar;
    private ConsoleWindow consoleWindow;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private EventController eventController;
    private InspectorWindow inspectorWindow;
    private PropertiesWindow propertiesWindow;
    private ResourcesManager resourcesManager;

    private static int ID;

    @Override
    public void init() {
        System.out.println(windowsScale);

        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();
        DebugDraw.reset();

        loadSceneFromFile();
        editorMode = true;


        gridLines = new GridLines(this);
        gizmo = new Gizmo(this);
        eventController = new EventController();
        helpPanel = new HelpPanel(this);
        mouseControls = new MouseControls(this, mouseListener, gizmo);
        cameraControl = new CameraControl(camera);
        keyControls = new KeyControls(mouseControls, cameraControl, this);

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(Console.getInstance());
        levelEditorStuff.addComponent(gridLines);
        levelEditorStuff.addComponent(mouseControls);
        levelEditorStuff.addComponent(cameraControl);
        levelEditorStuff.addComponent(keyControls);
        levelEditorStuff.addComponent(gizmo);
        levelEditorStuff.addComponent(eventController);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        fileDialog = new FileDialog(this);
        consoleWindow = new ConsoleWindow();
        creator = new Creator(this);
        inspectorWindow = new InspectorWindow();
        editorMenuBar = new EditorMenuBar(this);
        resourcesManager = new ResourcesManager(this);
        propertiesWindow = new PropertiesWindow(this, mouseControls, getProperties());
        settings = new Settings(this);

        if(maximize) editorMenuBar.maximize();
    }

    @Override
    public void update(float dt) {
        resetID();

        levelEditorStuff.update(dt);
        sceneUpdate(dt);
        updateDearGui();
    }

    public void render(float dt, boolean bufferIdMode){
        // render grid
        if(!bufferIdMode) DebugDraw.notify(Draw, gridID);

        // don't draw gizmo because it will be draw on the top
        getRenderer().skipLayer(gizmoZIndex, true);

        // draw scene objects
        getRenderer().render();

        // draw collider lines
        if(EventController.collider && !bufferIdMode) {
            DebugDraw.notify(Draw, colliderID);
        }

        if(!bufferIdMode) DebugDraw.notify(Draw, selectorID);

        // draw gizmos on the top of everything
        getRenderer().renderLayer(gizmoZIndex);
    }

    public void updateDearGui(){
        imGuiLayer.startFrame();

        creator.display();
        helpPanel.display();
        consoleWindow.Display();
        editorMenuBar.display();
        inspectorWindow.display();
        propertiesWindow.display();
        resourcesManager.Display();
        settings.display();
        fileDialog.display();
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

    public void displayFileBrowser(boolean display){
        fileDialog.setDisplay(display);
    }

    public void displaySettings(boolean display){
        settings.setDisplay(display);
    }

    public void displayConsole(boolean display){
        consoleWindow.setDisplay(display);
    }

    public void displayResources(boolean display){
        resourcesManager.setDisplay(display);
    }

    public void displayGrid(boolean display){
        gridLines.setDisplay(display);
    }

    public boolean shouldResourceDisplay(){
        return resourcesManager.shouldDisplay();
    }

    public boolean shouldGridDisplay(){
        return gridLines.shouldDisplay();
    }

    public static void resetID(){
        ID = 0;
    }

    public static int generateID(){
        return  ID++;
    }
}