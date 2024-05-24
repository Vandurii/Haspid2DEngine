package main.editor;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import main.haspid.Window;
import main.physics.events.EventSystem;
import main.physics.events.Event;
import main.physics.events.EventType;
import main.util.AssetPool;
import main.util.Texture;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class EditorMenuBar {
    private ImVec2 windowPos;
    private ImVec2 windowSize;
    private boolean maximizeMode;
    private EditorScene editorScene;

    public EditorMenuBar(EditorScene editorScene){
        this.editorScene = editorScene;
    }

    public void display(){
        long glfw = Window.getInstance().getGlfwWindow();

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, menuBarHeight);
        ImGui.beginMainMenuBar();

        // icon
        Texture icon = AssetPool.getTexture(iconPath, false);
        int iconID = icon.getTexID();
        ImGui.image(iconID, (float) icon.getWidth() / iconScale, (float) icon.getHeight() / iconScale);


        if(ImGui.beginMenu("File")){
            if(ImGui.menuItem("save", "ctrl+s")) EventSystem.notify(null, new Event(EventType.SaveLevel));
            if(ImGui.menuItem("load", "ctrl+l")) EventSystem.notify(null, new Event(EventType.LoadLevel));

            ImGui.endMenu();
        }

        if(ImGui.menuItem("Run")){
            EventSystem.notify(null, new Event(EventType.GameEngineStart));
            glfwMaximizeWindow(glfw);
        }

        ImGui.pushStyleColor(ImGuiCol.Button, exitNormal.getRed(), exitNormal.getGreen(), exitNormal.getBlue(), exitNormal.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, exitHover.getRed(), exitHover.getGreen(), exitHover.getBlue(), exitHover.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, exitActive.getRed(), exitActive.getGreen(), exitActive.getBlue(), exitActive.getAlpha());
        ImGui.setCursorPos(ImGui.getWindowSizeX() - (menuBarButtonSize + menuBarButtonSpacing), 0);
        if(ImGui.button("X", menuBarButtonSize, menuBarButtonSize)){
            glfwSetWindowShouldClose(glfw, true);
        }
        ImGui.popStyleColor(3);

        ImGui.pushStyleColor(ImGuiCol.Button, maximizeNormal.getRed(), maximizeNormal.getGreen(), maximizeNormal.getBlue(), maximizeNormal.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, maximizeHover.getRed(), maximizeHover.getGreen(), maximizeHover.getBlue(), maximizeHover.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, maximizeActive.getRed(), maximizeActive.getGreen(), maximizeActive.getBlue(), maximizeActive.getAlpha());
        ImGui.setCursorPos(ImGui.getWindowSizeX() - (menuBarButtonSize + menuBarButtonSpacing) * 2, 0);
        if(ImGui.button("M", menuBarButtonSize, menuBarButtonSize)){
            if(!maximizeMode) {
                glfwMaximizeWindow(glfw);
                maximizeMode = true;
            }else{
                glfwRestoreWindow(glfw);
                maximizeMode = false;
            }
        }
        ImGui.popStyleColor(3);

        ImGui.pushStyleColor(ImGuiCol.Button, minimizeNormal.getRed(), minimizeNormal.getGreen(), minimizeNormal.getBlue(), minimizeNormal.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, minimizeHover.getRed(), minimizeHover.getGreen(), minimizeHover.getBlue(), minimizeHover.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, minimizeActive.getRed(), minimizeActive.getGreen(), minimizeActive.getBlue(), minimizeActive.getAlpha());
        ImGui.setCursorPos(ImGui.getWindowSizeX() - (menuBarButtonSize + menuBarButtonSpacing) * 3, 0);
        if(ImGui.button("V", menuBarButtonSize, menuBarButtonSize)){
           glfwIconifyWindow(glfw);
        }
        ImGui.popStyleColor(3);

        updatePos();

        ImGui.popStyleVar(1);
        ImGui.endMainMenuBar();
    }

    public void updatePos(){
        windowPos =  ImGui.getWindowPos();
        windowSize = ImGui.getWindowSize();
    }

    public ImVec2 getWindowSize(){
        return windowSize;
    }

    public ImVec2 getWindowPos(){
        return windowPos;
    }

    public boolean getMaximizedMode(){
        return maximizeMode;
    }
}
