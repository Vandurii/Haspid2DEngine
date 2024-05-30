package main.editor.gui;

import imgui.ImGui;
import main.components.SpriteRenderer;
import main.components.stateMachine.Animation;
import main.components.stateMachine.Frame;
import main.components.stateMachine.StateMachine;
import main.editor.EditorScene;
import main.editor.JImGui;
import main.util.AssetPool;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.smallFormConfig;

public class Creator {
    private EditorScene editorScene;

    private boolean animation;

    private int index;
    private String name;
    private double frameTime;
    private boolean doesLoop;
    private List<Frame> frameList;

    public Creator(EditorScene editorScene){
        this.editorScene = editorScene;
        this.name = "Title";
        this.frameList = new ArrayList<>();
    }

    public void display(){

    ImGui.begin("Creator");

        if(ImGui.beginPopupContextWindow("ComponentAdder")){
            if(ImGui.menuItem("Create Animation"))  animation = true;
            ImGui.endPopup();
        }


        if(animation) createAnimation();


        ImGui.end();
    }

    public void createAnimation(){
        //ImGui.text("=====================");
        for(Frame f: frameList){
            SpriteRenderer sprite = f.getSpriteRenderer();
            Vector2d[] cords = sprite.getSpriteCords();
            ImGui.sameLine();
            ImGui.image(sprite.getTexID(), 50, 50, (float)cords[2].x, (float)cords[0].y, (float)cords[0].x, (float) cords[2].y);
        }

        ImGui.text("=====================");
        boolean imBoolean = doesLoop;
        if(ImGui.checkbox("doesLoop",imBoolean)) doesLoop = !doesLoop;

        name = (String) JImGui.drawValue("Name: ", name, this.hashCode() + "");

        frameTime = (float) JImGui.drawValue("Frame Time: ", frameTime, this.hashCode() + "");

        index = (int) JImGui.drawValue("Index: ", index, this.hashCode() + "");


        SpriteRenderer sprite = AssetPool.getSpriteSheet(smallFormConfig).getSprite(index);
        Vector2d[] cords = sprite.getSpriteCords();

        ImGui.image(sprite.getTexID(), 50, 50, (float)cords[2].x, (float)cords[0].y, (float)cords[0].x, (float) cords[2].y);



        if(ImGui.button("Add Frame")){
            frameList.add(new Frame(sprite, frameTime));

        }


        if(ImGui.button("Cancel")){
            reset();
        }

        ImGui.sameLine();
        if(ImGui.button("Create")){
            Animation anima = new Animation(name, doesLoop, frameList);
        }
    }

    public void reset(){
        animation = false;
        frameList = new ArrayList<>();
        doesLoop = false;
        name = "Title";
    }



}
