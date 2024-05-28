package main.editor.gui;

import imgui.ImGui;
import main.editor.EditorScene;

public class HelpPanel {
    private EditorScene editorScene;

    public HelpPanel(EditorScene editorScene){
        this.editorScene = editorScene;
    }

    public void display(){
        ImGui.begin("Help Panel");
        ImGui.text("Right-click on an object to activate it.");
        ImGui.text("Use scroll to change the world scale.");
        ImGui.end();
    }
}
