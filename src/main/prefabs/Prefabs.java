package main.prefabs;

import main.components.Animation;
import main.components.Frame;
import main.components.SpriteRenderer;
import main.components.StateMachine;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

import static main.Configuration.*;

public class Prefabs {
    private static GameObject generateObject(float width, float height){
        GameObject holdingObject = new GameObject("Generated");
        holdingObject.addComponent(new Transform(new Vector2f(), new Vector2f(width * spriteSize, height * spriteSize)));
        holdingObject.setTransformFromItself();

        return holdingObject;
    }

    public static GameObject generateSpriteObject(SpriteRenderer spriteR, float width, float height){
        GameObject holdingObject = generateObject(width, height);
        SpriteRenderer spriteRenderer = new SpriteRenderer(spriteR.getTexture(), width, height, spriteR.getSpriteCords());
        holdingObject.addComponent(spriteRenderer);

        return holdingObject;
    }

    public static GameObject generateAnimateObject(float width, float height, float defaultFrameTime, String title, List<SpriteRenderer> spriteRenderList){
        Animation animation = new Animation(title);
        for(SpriteRenderer spriteRenderer: spriteRenderList){
            animation.addFrame(spriteRenderer, defaultFrameTime);
        }

        return  generateAnimateObject(width, height, animation);
    }

    public static GameObject generateAnimateObject(float width, float height, Animation animation){
        GameObject animatedHoldingObject = generateObject(width, height);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animation);
        animatedHoldingObject.addComponent(stateMachine);
        animatedHoldingObject.addComponent(new SpriteRenderer());

        return  animatedHoldingObject;
    }
}
