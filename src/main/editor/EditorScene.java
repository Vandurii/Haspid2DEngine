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

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;

public class EditorScene extends Scene {

    private Gizmo gizmo;
    private Creator creator;
    private Settings settings;
    private HelpPanel helpPanel;
    private GridLines gridLines;
    private ImGuiLayer imGuiLayer;
    private FileBrowser fileBrowser;
    private KeyControls keyControls;
    private MouseListener mouseListener;
    private EditorMenuBar editorMenuBar;
    private ConsoleWindow consoleWindow;
    private MouseControls mouseControls;
    private CameraControl cameraControl;
    private GameObject levelEditorStuff;
    private InspectorWindow inspectorWindow;
    private PropertiesWindow propertiesWindow;
    private ResourcesManager resourcesManager;

    @Override
    public void init() {
        MouseListener.resetInstance();
        Renderer.resetInstance();
        mouseListener = MouseListener.getInstance();
        DebugDraw.reset();

        loadSceneFromFile();
        editorMode = true;

        gridLines = new GridLines(this);
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

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        fileBrowser = new FileBrowser();
        consoleWindow = new ConsoleWindow();
        creator = new Creator(this);
        inspectorWindow = new InspectorWindow();
        editorMenuBar = new EditorMenuBar(this);
        resourcesManager = new ResourcesManager(this);
        propertiesWindow = new PropertiesWindow(this, mouseControls, getProperties());
        settings = new Settings(this);
    }

    @Override
    public void update(float dt) {
        levelEditorStuff.update(dt);
        sceneUpdate(dt);
        updateDearGui();
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

        creator.display();
        helpPanel.display();
        consoleWindow.Display();
        editorMenuBar.display();
        inspectorWindow.display();
        propertiesWindow.display();
        resourcesManager.Display();
        settings.display();
        fileBrowser.display();
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
        fileBrowser.setDisplay(display);
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

    public boolean shouldResourceDisplay(){
        return resourcesManager.shouldDisplay();
    }
}