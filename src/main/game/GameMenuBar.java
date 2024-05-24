package main.game;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import main.haspid.Window;
import main.physics.events.EventSystem;
import main.physics.events.Event;
import main.physics.events.EventType;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.*;

public class GameMenuBar {
    public void display(){
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, menuBarHeight);
        ImGui.pushStyleColor(ImGuiCol.Button, exitNormal.getRed(), exitNormal.getGreen(), exitNormal.getBlue(), exitNormal.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, exitHover.getRed(), exitHover.getGreen(), exitHover.getBlue(), exitHover.getAlpha());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, exitActive.getRed(), exitActive.getGreen(), exitActive.getBlue(), exitActive.getAlpha());
        ImGui.beginMainMenuBar();

        if (ImGui.menuItem("Editor")) {
            EventSystem.notify(null, new Event(EventType.GameEngineStop));
            glfwRestoreWindow(Window.getInstance().getGlfwWindow());
        }

        long glfw = Window.getInstance().getGlfwWindow();

        ImGui.setCursorPos(ImGui.getWindowSizeX() - (menuBarButtonSize + menuBarButtonSpacing), 0);
        if(ImGui.button("X", menuBarButtonSize, menuBarButtonSize)){
            glfwSetWindowShouldClose(glfw, true);
        }

        ImGui.popStyleColor(3);
        ImGui.popStyleVar(1);
        ImGui.endMainMenuBar();
    }
}
