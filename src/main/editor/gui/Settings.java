package main.editor.gui;

import imgui.ImGui;
import main.editor.EditorScene;

public class Settings {

    private boolean console;
    private boolean resourcesMonitor;


    private boolean display;
    private EditorScene editorScene;

    public Settings(EditorScene editorScene){
        this.editorScene = editorScene;
        this.display = false;

        this.resourcesMonitor = editorScene.shouldResourceDisplay();
    }

    public void display(){
        if(display) {
            ImGui.begin("Settings");
            if(ImGui.button("close")){
                display = false;
            }

            if(ImGui.checkbox("Console", console)){
                console = !console;
                editorScene.displayConsole(console);
            }

            if(ImGui.checkbox("Resources Monitor", resourcesMonitor)){
                resourcesMonitor = !resourcesMonitor;
                editorScene.displayResources(resourcesMonitor);
            }


            ImGui.end();
        }
    }

    public boolean getDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
