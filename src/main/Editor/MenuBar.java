package main.Editor;

import imgui.ImGui;
import main.observers.EventSystem;
import main.observers.events.Event;
import main.observers.events.EventType;

public class MenuBar {

    public void display(){
        ImGui.beginMenuBar();
        if(ImGui.beginMenu("File")){
            if(ImGui.menuItem("save", "ctrl+s")) EventSystem.notify(null, new Event(EventType.SaveLevel));
            if(ImGui.menuItem("load", "ctrl+l")){
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }
            ImGui.endMenu();
        }
        ImGui.endMenuBar();
    }
}
