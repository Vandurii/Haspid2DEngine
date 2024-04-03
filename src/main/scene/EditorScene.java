package main.scene;

import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.*;

public class EditorScene extends Scene {

    public EditorScene() {}

    @Override
    public void init() {

        camera = new Camera(new Vector2f(-250, 0));
        for (int x=0; x < 4; x++) {
            GameObject gameObject = new GameObject("Obj" + x, new Transform(new Vector2f(x * 20 * 1.5f, 20), new Vector2f(20, 20)));
            gameObject.addComponent(new SpriteRenderer(new Vector4f(x * 0.2f, x * 0.2f, x * 0.2f, 1)));
            this.addGameObjectToScene(gameObject);
        }

        GameObject textureObject = new GameObject("objTex", new Transform(new Vector2f(100f, 100f), new Vector2f(256f, 256f)));
        SpriteRenderer tex = new SpriteRenderer(new Texture(marioImagePath));
        textureObject.addComponent(tex);
        addGameObjectToScene(textureObject);

        GameObject textureObject2 = new GameObject("objTex2", new Transform(new Vector2f(500f, 300), new Vector2f(256f, 256f)));
        SpriteRenderer tex2 = new SpriteRenderer(new Texture(marioImagePath));
        textureObject2.addComponent(tex2);
        addGameObjectToScene(textureObject2);
    }

    @Override
    public void update(float dt) {
        camera.getPosition().x += 1.f;
       // System.out.println("FPS: " + (1.0f / dt));

        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }

        getRenderer().render();
    }
}