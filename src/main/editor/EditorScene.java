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
import main.util.Properties;

import java.util.List;

import static main.Configuration.*;
import static main.renderer.DebugDrawEvents.*;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;

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
    private InspectorWindow inspectorWindow;
    private PropertiesWindow propertiesWindow;
    private ResourcesManager resourcesManager;

    private static int ID;

    @Override
    public void init() {
        // todo
        new Event();

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
        // render grid and selector
        if(!bufferIdMode){
           DebugDraw.notify(Draw, gridID);
           DebugDraw.notify(Draw, selectorID);
        }

        // don't draw gizmo because it will be draw on the top
        getRenderer().skipLayer(gizmoZIndex, true);

        // draw scene objects
        getRenderer().render();

        // draw collider lines
        if(Event.collider) {
            DebugDraw.notify(Draw, colliderID);
        }

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