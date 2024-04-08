package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.ImVec2;
import main.components.*;
import main.haspid.*;
import main.prefabs.Prefabs;
import main.util.SpriteConfig;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private MouseControls mouseControls;
    private GameObject textureObject3;
    private ImGuiLayer imGuiLayer;

    public EditorScene() {}

    @Override
    public void init() {
//        GameObject red = new GameObject("red", new Transform(new Vector2f(200, 200), new Vector2f(250, 250)), 0);
//        SpriteRenderer renderer = new SpriteRenderer(new Sprite(new Vector4f(0, 1, 0, 1)));
//        red.addComponent(renderer);
//        red.addComponent(new RigidBody(1, 1f, new Vector3f(), new Vector4f()));
//        addGameObjectToScene(red);
        load();
        mouseControls = new MouseControls();
        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.initImGui();

        camera = new Camera(new Vector2f(-250, 0));





    }

    @Override
    public void update(float dt) {
        imGuiLayer.update(dt);

        mouseControls.update(dt);

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
                GameObject holdingObject = Prefabs.generateSpriteObject( sprite,  spriteWidth,  spriteHeight);
                mouseControls.pickupObject(holdingObject);
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