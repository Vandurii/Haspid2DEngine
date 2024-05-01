package main.prefabs;

import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.Transform;
import org.joml.Vector2f;

import static main.Configuration.spriteSize;

public class Prefabs {

    public static GameObject generateSpriteObject(SpriteRenderer spriteR, float width, float height){
        GameObject holdingObject = new GameObject("Generated");
        holdingObject.addComponent(new Transform(new Vector2f(), new Vector2f(width * spriteSize, height * spriteSize)));
        holdingObject.setTransformFromItself();
        SpriteRenderer spriteRenderer = new SpriteRenderer(spriteR.getTexture(), width, height, spriteR.getSpriteCords());
        holdingObject.addComponent(spriteRenderer);

        return holdingObject;
    }
}
