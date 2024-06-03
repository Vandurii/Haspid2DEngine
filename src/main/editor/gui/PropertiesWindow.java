package main.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import main.components.SpriteRenderer;
import main.components.physicsComponent.ColliderType;
import main.editor.EditorScene;
import main.editor.Prefabs;
import main.editor.editorControl.MouseControls;
import main.haspid.Direction;
import main.haspid.GameObject;
import main.util.*;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;
import static main.games.mario.behaviour.QuestionBlockBeh.BlockType.Coin;

public class PropertiesWindow {

    private float yPadding;
    private float buttonSize;
    private float itemYPadding;
    private float itemXPadding;
    private float buttonSpacing;
    private float soundButtonScalar;

    public int activeTab;
    private EditorScene editorScene;
    private List<Properties> tabList;
    private MouseControls mouseControls;

    public PropertiesWindow(EditorScene editorScene, MouseControls mouseControls, List<Properties> tabList){
        this.yPadding = 5;
        this.buttonSize = 20;
        this.itemXPadding = 5;
        this.itemYPadding = 7;
        this.buttonSpacing = 13;
        this.tabList = tabList;
        this.soundButtonScalar = 1.75f;
        this.editorScene = editorScene;
        this.mouseControls = mouseControls;
    }

    public void display(){
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemXPadding, itemYPadding);
        ImGui.begin("Properties Window");

        if(ImGui.beginTabBar("Properties Bar")) {
            for(int i = 0; i < tabList.size(); i++){
                Properties properties = tabList.get(i);
                if(ImGui.beginTabItem(properties.getName())){
                    // initialize index for active tab
                    if(ImGui.isItemActive()){
                        activeTab = i;
                    }
                    if(properties instanceof SpriteSheet spriteSheet) {
                        generateButtons(spriteSheet);
                    }else if(properties instanceof AudioSheet audioSheet){
                        generateButtons(audioSheet);
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }

        ImGui.newLine();

        // delete Button
        Texture tex = AssetPool.getTexture(removeImagePath, false);
        ImGui.setCursorPos(ImGui.getContentRegionAvailX() - buttonSize, yPadding);
        if(ImGui.imageButton(tex.getTexID(), buttonSize, buttonSize)){
            if(!tabList.isEmpty() && activeTab < tabList.size()) {
                // otherwise last tab won't be deleted
                if(tabList.size() == 1) activeTab = 0;
                editorScene.removePropertiesSafe(tabList.get(activeTab));
            }
        }

        // add Button
        tex = AssetPool.getTexture(addImagePath, false);
        ImGui.setCursorPos(ImGui.getContentRegionAvailX() - buttonSpacing - (buttonSize * 2), yPadding);
        if(ImGui.imageButton(tex.getTexID(), buttonSize, buttonSize)){
            editorScene.displayFileBrowser(true);
        }

        ImGui.popStyleVar(1);
        ImGui.end();
    }

    public void generateButtons(SpriteSheet spriteSheet){
        for (int i = 0; i < spriteSheet.getSize(); i++) {
            SpriteRenderer sprite = spriteSheet.getSprite(i);
            double spriteWidth = sprite.getWidth();
            double spriteHeight = sprite.getHeight();
            Vector2d scale = new Vector2d(spriteWidth * spriteSheet.getScalar(), spriteHeight * spriteSheet.getScalar());
            int texID = sprite.getTexID();
            Vector2d[] cords = sprite.getSpriteCords();

            ImGui.pushID(EditorScene.generateID());
            if (ImGui.imageButton(texID, (float) spriteWidth, (float) spriteHeight, (float) cords[3].x, (float) cords[3].y, (float) cords[1].x, (float) cords[1].y)) {
                GameObject holdingObject = Prefabs.generateBopObject(sprite, scale, ColliderType.Box);
                // todo
                if(spriteSheet.getName().equals("icons")){
                    switch (i){
                        case 0 -> holdingObject = Prefabs.generateMario(scale);
                        case 1 -> holdingObject = Prefabs.generateGoomba(scale);
                        case 5 -> holdingObject = Prefabs.generatePipe(sprite,scale, Direction.Down);
                        case 6 -> holdingObject = Prefabs.generatePipe(sprite, scale, Direction.Up);
                        case 7 -> holdingObject = Prefabs.generatePipe(sprite, scale, Direction.Right);
                        case 8 -> holdingObject = Prefabs.generatePipe(sprite, scale, Direction.Left);
                        case 9 -> holdingObject = Prefabs.generateQuestionBlock(scale, Coin);
                        case 11 -> holdingObject = Prefabs.generateMushroom(scale);
                        case 13 -> holdingObject = Prefabs.generateFlower(scale);
                        case 14 -> holdingObject = Prefabs.generateCoin(scale);
                        case 15 -> holdingObject = Prefabs.generateTurtle(new Vector2d(scale.x, scale.y * 2)); // todo
                        default -> holdingObject = Prefabs.generateBopObject(sprite, scale, ColliderType.Box);
                    }
                }else if(spriteSheet.getName().equals("items")){
                    if(i == 6){
                        holdingObject = Prefabs.generateFlag(sprite, scale);
                    }
                }else if(spriteSheet.getName().equals("blocks")){
                    if(i > 33){
                        holdingObject = Prefabs.generateSpriteObject(sprite, scale);
                    }
                }
                mouseControls.pickupObject(holdingObject);
            }
            ImGui.popID();

            // check if there is enough space for next button in this line
            spaceManager((float) spriteWidth);
        }
    }

    public void generateButtons(AudioSheet audioSheet){
        for(Sound sound: audioSheet.getSoundList()) {
            //Generate button name from path.
            String path = sound.getFilePath();
            String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".ogg"));

            ImGui.pushID(EditorScene.generateID());
            if (ImGui.button(name)) {
                if (!sound.isPlaying()) {
                    sound.play();
                } else {
                    sound.stop();
                }
            }

            // check if there is enough space for next button in this line
            float buttonWidth = name.length() * (ImGui.getFontSize() / soundButtonScalar);
            spaceManager(buttonWidth);

            ImGui.popID();
        }
    }

    public void spaceManager(float buttonWidth){
        float lastItemSpacingX = ImGui.getItemRectMaxX();
        float itemSpacingX = ImGui.getStyle().getItemSpacingX();

        float windowPosX = ImGui.getWindowPosX();
        float windowSizeX = ImGui.getWindowSizeX();
        float windowEdgeX = windowPosX + windowSizeX;

        float nextButtonPosX = lastItemSpacingX + itemSpacingX + buttonWidth;

        if(nextButtonPosX < windowEdgeX){
            ImGui.sameLine();
        }
    }
}
