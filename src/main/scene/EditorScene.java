package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.ImVec2;
import main.components.Component;
import main.components.RigidBody;
import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.util.SpriteConfig;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private GameObject textureObject3;
    private ImGuiLayer imGuiLayer;

    public EditorScene() {}

    @Override
    public void init() {
        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.initImGui();

        camera = new Camera(new Vector2f(-250, 0));

        load();
    }

    @Override
    public void update(float dt) {
        imGuiLayer.update(dt);
        System.out.println(MouseListener.getInstance().getOrthoX());

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
       // imGuiLayer.destroy();
    }

    public void dearGui(){
        SpriteSheet properties = AssetPool.getSpriteSheet(new SpriteConfig("assets/images/decorationsAndBlocks.png", 16, 16, 81, 0));

        if(activeGameObject != null){
           ImGui.begin("Inspector"); //todo
           activeGameObject.dearGui();
           ImGui.end(); //todo
        }

        ImGui.begin("Properties Winow");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;

        for(int i = 0; i < properties.getSize(); i++){
            Sprite sprite = properties.getSprite(i);
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int texID = sprite.getTexID();
            Vector2f[] cords = sprite.getSpriteCords();

            ImGui.pushID(i);
            if(ImGui.imageButton(texID, spriteWidth, spriteHeight, cords[0].x, cords[0].y, cords[2].x, cords[2].y)){
                System.out.println("item click:  " + i);
            }
            ImGui.popID();

            ImVec2 lastButton = new ImVec2();
            ImGui.getItemRectMax(lastButton); // todo
            float nextButton = lastButton.x + itemSpacing.x + spriteWidth;

            if(nextButton < window){
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}