package main.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.haspid.Window;

import static main.Configuration.imGuiColor;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class InspectorWindow {
    private static InspectorWindow instance;
    private static GameObject activeGameObject;

    private InspectorWindow(){}

    public static InspectorWindow getInstance(){
        if(instance == null) instance = new InspectorWindow();

        return instance;
    }

    public void display(){
        if(activeGameObject != null){
            ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
            ImGui.begin("Inspector");
            activeGameObject.dearGui();
            ImGui.end();
            ImGui.popStyleColor(1);
        }
    }

    public void update(){
        if(MouseListener.getInstance().isButtonPressed(GLFW_MOUSE_BUTTON_1)){
            int x = (int) MouseListener.getInstance().getScreenX();
            int y = (int) MouseListener.getInstance().getScreenY();
            int id = (int) Window.getInstance().getIdBuffer().readIDFromPixel(x, y);

            GameObject active = Window.getInstance().getCurrentScene().getGameObjectFromID(id);
            if(active != null) activeGameObject = active;
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        InspectorWindow.activeGameObject = activeGameObject;
    }
}
