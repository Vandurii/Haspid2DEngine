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


import java.util.Arrays;

import static main.Configuration.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.opengl.GL11.*;

public class EditorScene extends Scene {
    private ImGuiLayer imGuiLayer;
    private SpriteSheet decorationAndBlocks;
    private GameObject levelEditorStuff;

    public EditorScene() {}

    @Override
    public void init() {
        load();

        levelEditorStuff = new GameObject("LevelEditorStuff");
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new MouseControls());

        AssetPool.getTexture(marioImagePath);
        decorationAndBlocks = AssetPool.getSpriteSheet(decorationAndBlockConfig);

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        camera = new Camera(new Vector2f(0, 0));
    }

    @Override
    public void update(float dt) {
        dearGui();
        levelEditorStuff.update(dt);

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }
       // AssetPool.printResources();
    }

    public void render(float dt, boolean bufferIdMode){
        if(!bufferIdMode){
            DebugDraw.draw();
        }
        getRenderer().render();
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
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(holdingObject);
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