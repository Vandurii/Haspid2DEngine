package main.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import main.components.Animation;
import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.prefabs.Prefabs;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;
import static main.Configuration.imGuiTabActive;

public class PropertiesWindow {

    private MouseControls mouseControls;
    private List<SpriteSheet> tabList;

    public PropertiesWindow(MouseControls mouseControls, List<SpriteSheet> tabList){
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
            for(SpriteSheet tab: tabList){
                if(ImGui.beginTabItem("<" + i++ + ">")){
                    generateButtons(tab, i);
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        ImGui.popStyleColor(4);
        ImGui.end();
    }

    public void generateButtons(SpriteSheet spriteSheet, int index){
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;

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

            ImVec2 lastButton = new ImVec2();
            ImGui.getItemRectMax(lastButton);
            float nextButton = lastButton.x + itemSpacing.x + spriteWidth;

            if (nextButton < window) {
                ImGui.sameLine();
            }
        }
    }
}
