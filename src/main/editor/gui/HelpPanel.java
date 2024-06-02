package main.editor.gui;

import imgui.*;
import main.editor.EditorScene;
import main.haspid.Event;
import main.util.AssetPool;
import main.util.Texture;

import static main.Configuration.ideaImagePath;

public class HelpPanel {
    private EditorScene editorScene;

    public HelpPanel(EditorScene editorScene){
        this.editorScene = editorScene;
    }

    public void display(){

        ImGui.begin("Help Panel");


        for(Event.Hint hint: Event.hintList){
            Texture tex = AssetPool.getTexture(ideaImagePath, false);
            ImGui.image(tex.getTexID(), 20, 20);
            ImGui.sameLine();
            ImGui.text(hint.getText());
        }



        ImGui.end();
    }
}
