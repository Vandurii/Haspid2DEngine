package main.scene;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Configuration;
import main.Editor.MouseControls;
import main.Editor.ViewPort;
import main.components.*;
import main.haspid.*;
import main.prefabs.Prefabs;
import main.renderer.DebugDraw;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;


import static main.Configuration.*;

public class EditorScene extends Scene {
    private MouseControls mouseControls;
    private GameObject textureObject3;
    private ImGuiLayer imGuiLayer;
    private SpriteSheet decorationAndBlocks;
    private GridLines gridLines;
    private int c;

    public EditorScene() {}

    @Override
    public void init() {
        gridLines = new GridLines();
        load();
        AssetPool.getTexture(marioImagePath);
        decorationAndBlocks = AssetPool.getSpriteSheet(decorationAndBlockConfig);

        mouseControls = new MouseControls();

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        camera = new Camera(new Vector2f(0, 0));
    }

    @Override
    public void update(float dt) {
        mouseControls.update(dt);
        DebugDraw.draw();
        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
        Window.getInstance().getFrameBuffer().unBind();
        dearGui();
        gridLines.update(dt);
       // AssetPool.printResources();
    }

    public void dearGui(){
        imGuiLayer.startFrame();
        ViewPort.displayViewPort();

        if(activeGameObject != null){
           ImGui.begin("Inspector");
           activeGameObject.dearGui();
           ImGui.end();
        }

        ImGui.begin("Properties Winow");
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;

        for(int i = 0; i < decorationAndBlocks.getSize(); i++){
            Sprite sprite = decorationAndBlocks.getSprite(i);
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
        ImGui.end();

        imGuiLayer.endFrame();
    }

    public void end(){
        imGuiLayer.dispose();
    }
}