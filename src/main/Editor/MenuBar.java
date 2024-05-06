package main.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import main.observers.EventSystem;
import main.observers.events.Event;
import main.observers.events.EventType;

import static main.Configuration.imGuiMenuBar;
import static main.Configuration.imGuiTabActive;

public class MenuBar {
    private static boolean isPlaying;

    public void display(){
        ImGui.pushStyleColor(ImGuiCol.MenuBarBg, imGuiMenuBar.x, imGuiMenuBar.y, imGuiMenuBar.z, imGuiTabActive.w);
        ImGui.beginMainMenuBar();

        if(ImGui.beginMenu("File")){
            if(ImGui.menuItem("save", "ctrl+s")) EventSystem.notify(null, new Event(EventType.SaveLevel));
            if(ImGui.menuItem("load", "ctrl+l")){
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }
            ImGui.endMenu();
        }

        if(ImGui.menuItem("Play", "", isPlaying, !isPlaying)){
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStart));
        }else if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStop));

        }

        ImGui.popStyleColor(1);
        ImGui.endMainMenuBar();
    }
}
