package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import main.components.Component;
import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.util.SpriteSheet;
import main.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private GameObject textureObject3;
    private ImGuiLayer imGuiLayer;

    public EditorScene() {}

    @Override
    public void init() {
        SpriteSheet spriteSheet = AssetPool.getSpriteSheet(firstSpriteSheet);
        imGuiLayer = new ImGuiLayer(Window.getInstance().getGlfwWindow());
        imGuiLayer.initImGui();

        camera = new Camera(new Vector2f(-250, 0));

//        GameObject textureObject = new GameObject("objTex", new Transform(new Vector2f(100f, 100f), new Vector2f(256f, 256f)), 0);
//        SpriteRenderer tex = new SpriteRenderer(new Sprite(AssetPool.getTexture(marioImagePath)));
//        textureObject.addComponent(tex);
//        addGameObjectToScene(textureObject);
//
//        GameObject textureObject2 = new GameObject("objTex2", new Transform(new Vector2f(500f, 300), new Vector2f(256f, 256f)), 0);
//        SpriteRenderer tex2 = new SpriteRenderer(new Sprite(new Vector4f(0, 0, 1, 1)));
//        //SpriteRenderer tex2 = new SpriteRenderer(new Sprite(new Vector4f(1 ,1 ,1,1)));
//        textureObject2.addComponent(tex2);
//        addGameObjectToScene(textureObject2);
//        activeGameObject = textureObject2;
//
//        textureObject3 = new GameObject("objTex3", new Transform(new Vector2f(800f, 300), new Vector2f(256f, 256f)),0 );
//        SpriteRenderer tex3 = new SpriteRenderer(spriteSheet.getSprite(5));
//        textureObject3.addComponent(tex3);
//        addGameObjectToScene(textureObject3);
//
//        GameObject green = new GameObject("green", new Transform(new Vector2f(300, 65), new Vector2f(300, 300)), 0);
//        SpriteRenderer greenR = new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/green.png")));
//        green.addComponent(greenR);
//        addGameObjectToScene(green);

        load();

        GameObject red = new GameObject("red", new Transform(new Vector2f(200, 200), new Vector2f(300, 300)), 0);
        SpriteRenderer redR = new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/red.png")));
        red.addComponent(redR);
        addGameObjectToScene(red);
        activeGameObject = red;
    }

    @Override
    public void update(float dt) {
        imGuiLayer.update(dt);

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
       // imGuiLayer.destroy();
    }

    public void dearGui(){

        if(activeGameObject != null){
           ImGui.begin("Inspector"); //todo
           activeGameObject.dearGui();
           ImGui.end(); //todo
        }

        ImGui.begin("Window");
        ImGui.text("some Text");
        ImGui.end();
    }
}