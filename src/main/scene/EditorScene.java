package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Configuration;
import main.components.*;
import main.haspid.*;
import main.prefabs.Prefabs;
import main.renderer.DebugDraw;
import main.util.SpriteConfig;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;


import static main.Configuration.*;

public class EditorScene extends Scene {
    private MouseControls mouseControls;
    private GameObject textureObject3;
    private ImGuiLayer imGuiLayer;
    private SpriteSheet decorationAndBlocks;
    private int c;

    public EditorScene() {}

    @Override
    public void init() {
        load();

        mouseControls = new MouseControls();

        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.init(new Configuration());

        camera = new Camera(new Vector2f(-250, 0));


        AssetPool.getTexture(marioImagePath);
        decorationAndBlocks = AssetPool.getSpriteSheet(decorationAndBlockConfig);

//
//        GameObject g1 = new GameObject("1", new Transform(new Vector2f(250, 250), new Vector2f(250, 250)), 0);
//        g1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture(marioImagePath))));
//
//        GameObject g2 = new GameObject("2", new Transform(new Vector2f(600, 250), new Vector2f(250, 250)), 0);
//        g2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture(marioImagePath))));
//
//        GameObject g3 = new GameObject("3", new Transform(new Vector2f(900, 250), new Vector2f(250, 250)), 0);
//        g3.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture(marioImagePath))));
//
//        addGameObjectToScene(g1, g2, g3);
    }

    @Override
    public void update(float dt) {
        dearGui();
        mouseControls.update(dt);
        DebugDraw.draw();
        DebugDraw.drawBoxes2D(new Vector2f(10, 500), new Vector2f(100, 200), c, colorBlue);
        DebugDraw.drawCircle2D(new Vector2f(600, 500), 50);
        DebugDraw.addLine2D(new Vector2f(c++, 100), new Vector2f(800, 800));
        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
        Window.getInstance().getFrameBuffer().bind();
       // AssetPool.printResources();
    }

    public void dearGui(){
        imGuiLayer.startFrame();

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