package main.editor.gui;

import imgui.*;
import main.editor.EditorScene;
import main.editor.editorControl.EventController;
import main.util.AssetPool;
import main.util.Texture;

import static main.Configuration.colorOrangeA;
import static main.Configuration.ideaImagePath;

public class HelpPanel {
    private int imageSize;
    private EditorScene editorScene;

    public HelpPanel(EditorScene editorScene){
        this.imageSize = 20;
        this.editorScene = editorScene;
    }

    public void display(){
        ImGui.begin("Help Panel");

        for(EventController.Hint hint: EventController.hintMap.values()){
            if(hint.isEnabled()) {
                Texture tex = AssetPool.getTexture(ideaImagePath, false);
                ImGui.image(tex.getTexID(), imageSize, imageSize);
                ImGui.sameLine();
                ImGui.textColored(colorOrangeA.x, colorOrangeA.y, colorOrangeA.z, colorOrangeA.w, hint.getText());
            }
        }

        ImGui.end();
    }
}
