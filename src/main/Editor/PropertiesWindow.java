package main.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import main.components.Animation;
import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.prefabs.Prefabs;
import main.util.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;
import static main.Configuration.imGuiTabActive;

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
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int texID = sprite.getTexID();
            Vector2f[] cords = sprite.getSpriteCords();

            ImGui.pushID(i);
            if (ImGui.imageButton(texID, spriteWidth, spriteHeight, cords[3].x, cords[3].y, cords[1].x, cords[1].y)) {
                GameObject holdingObject;
                // todo
                if(i == 0 && index == 3){
                    System.out.println("true");
                    List<SpriteRenderer> animationList = new ArrayList<>();
                    SpriteSheet sheet = AssetPool.getSpriteSheet(firstSpriteSheet);

                    animationList.add(sheet.getSprite(0));
                    animationList.add(sheet.getSprite(2));
                    animationList.add(sheet.getSprite(3));
                    animationList.add(sheet.getSprite(2));

                    holdingObject = Prefabs.generateAnimateObject(spriteWidth, spriteHeight, 0.23f, "run", animationList);
                }else if(i == 0 && index == 2){
                    SpriteSheet items = AssetPool.getSpriteSheet(itemsConfig);
                    Animation animation = new Animation("questionBlock");

                    animation.addFrame(items.getSprite(0), 0.57f);
                    animation.addFrame(items.getSprite(1), 0.23f);
                    animation.addFrame(items.getSprite(2), 0.23f);


                    holdingObject = Prefabs.generateAnimateObject(spriteWidth, spriteHeight, animation);
                }else{
                    holdingObject = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                }
                mouseControls.pickupObject(holdingObject);
            }
            ImGui.popID();

            if (enoughSpaceForNextButton(spriteWidth)) ImGui.sameLine();
        }
    }

    public void generateButtons(AudioSheet audioSheet, int index){
        for(Sound sound: audioSheet.getSoundList()) {
            String p = sound.getFilePath();
            String name = p.substring(p.lastIndexOf("/") + 1, p.lastIndexOf(".ogg"));
            if (ImGui.button(name)) {
                if (!sound.isPlaying()) {
                    sound.play();
                } else {
                    sound.stop();
                }
            }

            float buttonWidth = name.length() * (ImGui.getFontSize() / 1.75f);
            if(enoughSpaceForNextButton(buttonWidth)) ImGui.sameLine();
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
