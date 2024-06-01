package main.editor.gui;

import imgui.ImGui;
import main.editor.EditorScene;

public class Settings {

    private boolean console;
    private boolean resourcesMonitor;
    private boolean grid;


    private boolean display;
    private EditorScene editorScene;

    public Settings(EditorScene editorScene){
        this.editorScene = editorScene;
        this.display = false;

        this.resourcesMonitor = editorScene.shouldResourceDisplay();
        this.grid = editorScene.shouldGridDisplay();
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

            if(ImGui.checkbox("Show Grid", grid)){
                grid = !grid;
                System.out.println(grid);
                editorScene.displayGrid(grid);
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
