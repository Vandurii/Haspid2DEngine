package main.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import main.components.SpriteRenderer;
import main.components.physicsComponent.ColliderType;
import main.editor.editorControl.MouseControls;
import main.haspid.Direction;
import main.haspid.GameObject;
import main.util.*;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;
import static main.Configuration.imGuiTabActive;
import static main.components.behaviour.QuestionBlockBeh.BlockType.Coin;

public class PropertiesWindow {

    private MouseControls mouseControls;
    private List<Properties> tabList;

    public PropertiesWindow(MouseControls mouseControls, List<Properties> tabList){
        this.mouseControls = mouseControls;
        this.tabList = tabList;
    }

    public void display(){
        ImGui.pushStyleColor(ImGuiCol.Button, imGuiButtonColor.x, imGuiButtonColor.y, imGuiButtonColor.z, imGuiButtonColor.w);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
        ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, imGuiTabInactive.x, imGuiTabInactive.y, imGuiTabInactive.z, imGuiTabInactive.w);
        ImGui.pushStyleColor(ImGuiCol.TabActive, imGuiTabActive.x, imGuiTabActive.y, imGuiTabActive.z, imGuiTabActive.w);
        ImGui.begin("Properties Window");
        if(ImGui.beginTabBar("Properties Bar")) {
            int i = 1;
            for(Properties tab: tabList){
                if(ImGui.beginTabItem("<" + i++ + ">")){
                    if(tab instanceof SpriteSheet spriteSheet) {
                        generateButtons(spriteSheet, i);
                    }else if(tab instanceof AudioSheet audioSheet){
                        generateButtons(audioSheet, i);
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        ImGui.popStyleColor(4);
        ImGui.end();
    }

    public void generateButtons(SpriteSheet spriteSheet, int index){
        for (int i = 0; i < spriteSheet.getSize(); i++) {
            SpriteRenderer sprite = spriteSheet.getSprite(i);
            double spriteWidth = sprite.getWidth();
            double spriteHeight = sprite.getHeight();
            int texID = sprite.getTexID();
            Vector2d[] cords = sprite.getSpriteCords();

            ImGui.pushID((index * 100) + i);
            if (ImGui.imageButton(texID, (float) spriteWidth, (float) spriteHeight, (float) cords[3].x, (float) cords[3].y, (float) cords[1].x, (float) cords[1].y)) {
                GameObject holdingObject = Prefabs.generateBopObject(sprite, spriteWidth, spriteHeight, ColliderType.Box);
                // todo
                if(index == 7){
                    switch (i){
                        case 0 -> holdingObject = Prefabs.generateMario(spriteWidth / 2, spriteHeight /2);
                        case 1 -> holdingObject = Prefabs.generateGoomba(spriteWidth / 2, spriteHeight / 2);
                        case 5 -> holdingObject = Prefabs.generatePipe(sprite, spriteWidth, spriteHeight, Direction.Down);
                        case 6 -> holdingObject = Prefabs.generatePipe(sprite, spriteWidth, spriteHeight, Direction.Up);
                        case 7 -> holdingObject = Prefabs.generatePipe(sprite, spriteWidth, spriteHeight, Direction.Right);
                        case 8 -> holdingObject = Prefabs.generatePipe(sprite, spriteWidth, spriteHeight, Direction.Left);
                        case 9 -> holdingObject = Prefabs.generateQuestionBlock(spriteWidth / 2, spriteHeight / 2, Coin);
                        case 11 -> holdingObject = Prefabs.generateMushroom(spriteWidth / 2, spriteHeight / 2);
                        case 13 -> holdingObject = Prefabs.generateFlower(spriteWidth / 2, spriteHeight / 2);
                        case 14 -> holdingObject = Prefabs.generateCoin(spriteWidth /2, spriteHeight / 2);
                        case 15 -> holdingObject = Prefabs.generateTurtle(spriteWidth / 2, spriteHeight / 2 / 10 * 14);
                        default -> holdingObject = Prefabs.generateBopObject(sprite, spriteWidth / 2, spriteHeight / 2, ColliderType.Box);
                    }
                }else if(index == 2){
                    if(i == 6){
                        holdingObject = Prefabs.generateFlag(sprite, 16, spriteHeight);
                    }
                }else if(index == 4){
                    if(i > 33){
                        holdingObject = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                    }
                }
                mouseControls.pickupObject(holdingObject);
            }
            ImGui.popID();

            if (enoughSpaceForNextButton((float) spriteWidth)) ImGui.sameLine();
        }
    }

    public void generateButtons(AudioSheet audioSheet, int index){
        for(Sound sound: audioSheet.getSoundList()) {
            String p = sound.getFilePath();
            String name = p.substring(p.lastIndexOf("/") + 1, p.lastIndexOf(".ogg"));
            ImGui.pushID(index);
            if (ImGui.button(name)) {
                if (!sound.isPlaying()) {
                    sound.play();
                } else {
                    sound.stop();
                }
            }

            float buttonWidth = name.length() * (ImGui.getFontSize() / 1.75f);
            if(enoughSpaceForNextButton(buttonWidth)) ImGui.sameLine();
            ImGui.popID();
        }
    }

    public boolean enoughSpaceForNextButton(float buttonWidth){
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;


        ImVec2 lastButton = new ImVec2();
        ImGui.getItemRectMax(lastButton);
        float nextButton = lastButton.x + itemSpacing.x + buttonWidth;

        return nextButton < window;
    }
}
