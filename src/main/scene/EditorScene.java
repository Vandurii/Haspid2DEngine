package main.scene;

import main.components.Sprite;
import main.components.SpriteRenderer;
import main.components.SpriteSheet;
import main.haspid.Camera;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.renderer.Texture;
import main.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.*;

public class EditorScene extends Scene {
    private GameObject textureObject3;

    public EditorScene() {}

    @Override
    public void init() {
        SpriteSheet spriteSheet = AssetPool.getSpriteSheet(firstSpriteSheet);

        camera = new Camera(new Vector2f(-250, 0));
        for (int x=0; x < 4; x++) {
            GameObject gameObject = new GameObject("Obj" + x, new Transform(new Vector2f(x * 20 * 1.5f, 20), new Vector2f(20, 20)));
            gameObject.addComponent(new SpriteRenderer(new Sprite(new Vector4f(x * 0.2f, x * 0.2f, x * 0.2f, 1))));
            this.addGameObjectToScene(gameObject);
        }

        GameObject textureObject = new GameObject("objTex", new Transform(new Vector2f(100f, 100f), new Vector2f(256f, 256f)));
        SpriteRenderer tex = new SpriteRenderer(new Sprite(new Texture(marioImagePath)));
        textureObject.addComponent(tex);
        addGameObjectToScene(textureObject);

        GameObject textureObject2 = new GameObject("objTex2", new Transform(new Vector2f(500f, 300), new Vector2f(256f, 256f)));
        SpriteRenderer tex2 = new SpriteRenderer(new Sprite(new Texture(marioImagePath)));
        //SpriteRenderer tex2 = new SpriteRenderer(new Sprite(new Vector4f(1 ,1 ,1,1)));
        textureObject2.addComponent(tex2);
        addGameObjectToScene(textureObject2);

        textureObject3 = new GameObject("objTex3", new Transform(new Vector2f(800f, 300), new Vector2f(256f, 256f)));
        SpriteRenderer tex3 = new SpriteRenderer(spriteSheet.getSprite(5));
        textureObject3.addComponent(tex3);
        addGameObjectToScene(textureObject3);
    }

    @Override
    public void update(float dt) {
        camera.getPosition().x += 1.f;
       //System.out.println("FPS: " + (1.0f / dt));

        if(textureObject3.getTransform().getPosition().x < 850)textureObject3.getTransform().setPosition(new Vector2f(textureObject3.getTransform().getPosition().x + 0.5f, textureObject3.getTransform().getPosition().y));

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
    }
}