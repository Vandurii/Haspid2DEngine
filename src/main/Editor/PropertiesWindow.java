package main.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import main.components.Sprite;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.prefabs.Prefabs;
import main.util.SpriteSheet;
import org.joml.Vector2f;

import static main.Configuration.imGuiColor;

public class PropertiesWindow {

    private MouseControls mouseControls;
    private SpriteSheet spriteSheet;

    public PropertiesWindow(MouseControls mouseControls, SpriteSheet spriteSheet){
        this.mouseControls = mouseControls;
        this.spriteSheet = spriteSheet;
    }

    public void display(){
        ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
        ImGui.begin("Properties Winow");
        ImGui.popStyleColor(1);
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;

        for(int i = 0; i < spriteSheet.getSize(); i++){
            Sprite sprite = spriteSheet.getSprite(i);
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int texID = sprite.getTexID();
            Vector2f[] cords = sprite.getSpriteCords();

            ImGui.pushID(i);
            if(ImGui.imageButton(texID, spriteWidth, spriteHeight, cords[0].x, cords[0].y, cords[2].x, cords[2].y)){
                GameObject holdingObject = Prefabs.generateSpriteObject( sprite,  spriteWidth,  spriteHeight);
                mouseControls.pickupObject(holdingObject);
            }
            ImGui.popID();

            ImVec2 lastButton = new ImVec2();
            ImGui.getItemRectMax(lastButton);
            float nextButton = lastButton.x + itemSpacing.x + spriteWidth;

            if(nextButton < window){
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }
}
