package main.prefabs;

import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.Transform;
import org.joml.Vector2f;

import static main.Configuration.spriteSize;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float width, float height){
        GameObject holdingObject = new GameObject("Generated", new Transform(new Vector2f(), new Vector2f(width * spriteSize, height * spriteSize)), 2);
        SpriteRenderer spriteRenderer = new SpriteRenderer(new Sprite(sprite.getTexture(), width, height, sprite.getSpriteCords()));
        holdingObject.addComponent(spriteRenderer);

        return holdingObject;
    }
}
