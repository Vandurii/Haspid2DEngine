package main.editor;

import main.components.*;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.components.physicsComponent.PillboxCollider;
import main.physics.BodyType;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.List;

import static main.Configuration.*;

public class Prefabs {
    private static GameObject generateObject(double width, double height){
        GameObject holdingObject = new GameObject("Generated");
        holdingObject.addComponent(new Transform(new Vector2d(), new Vector2d(width * spriteSize, height * spriteSize)));
        holdingObject.setTransformFromItself();

        return holdingObject;
    }

    public static GameObject generateSpriteObject(SpriteRenderer spriteR, double width, double height){
        GameObject holdingObject = generateObject(width, height);
        SpriteRenderer spriteRenderer = new SpriteRenderer(spriteR.getTexture(), width, height, spriteR.getSpriteCords());
        holdingObject.addComponent(spriteRenderer);

        return holdingObject;
    }

    public static GameObject generateSolidObject(SpriteRenderer spriteR, double width, double height){
        GameObject solidObject = generateSpriteObject(spriteR, width, height);
        RigidBody rigidBody = new RigidBody();
        rigidBody.setBodyType(BodyType.Static);
        BoxCollider boxCollider = new BoxCollider(new Vector2d(width, height));
        solidObject.addComponent(rigidBody);
        solidObject.addComponent(boxCollider);

        return solidObject;
    }

    public static GameObject generateAnimateObject(double width, double height, double defaultFrameTime, String title, List<SpriteRenderer> spriteRenderList){
        Animation animation = new Animation(title);
        for(SpriteRenderer spriteRenderer: spriteRenderList){
            animation.addFrame(spriteRenderer, defaultFrameTime);
        }

        return  generateAnimateObject(width, height, animation);
    }

    public static GameObject generateAnimateObject(double width, double height, Animation animation){
        GameObject animatedHoldingObject = generateObject(width, height);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animation);
        animatedHoldingObject.addComponent(stateMachine);
        animatedHoldingObject.addComponent(new SpriteRenderer());
       animatedHoldingObject.addComponent(new PlayerController());
        animatedHoldingObject.addComponent(new PillboxCollider());

        return  animatedHoldingObject;
    }
}
