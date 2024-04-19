package main.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.haspid.Window;

import static main.Configuration.imGuiColor;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class InspectorWindow {
    private static InspectorWindow instance;
    private static MouseControls mouseControls;

    private InspectorWindow(){
        mouseControls = MouseControls.getInstance();
    }

    public static InspectorWindow getInstance(){
        if(instance == null) instance = new InspectorWindow();

        return instance;
    }

    public void display(){
        if(mouseControls.getActiveGameObject() != null){
            ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
            ImGui.begin("Inspector");
            mouseControls.getActiveGameObject().dearGui();
            ImGui.end();
            ImGui.popStyleColor(1);
        }
    }
}
