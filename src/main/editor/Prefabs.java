package main.editor;

import main.components.*;
import main.components.behaviour.CoinBeh;
import main.components.Hitable;
import main.components.behaviour.QuestionBlockBeh;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.haspid.GameObject;
import main.components.behaviour.QuestionBlockBeh.BlockType;
import main.haspid.Transform;
import main.physics.BodyType;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import static main.Configuration.*;

public class Prefabs {
    private static SpriteSheet items = AssetPool.getSpriteSheet(itemsConfig);
    private static SpriteSheet sheet = AssetPool.getSpriteSheet(firstSpriteSheet);


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

    public static GameObject generateColliderObject(SpriteRenderer spriteRenderer, double width, double height){
        GameObject colliderObject = generateSpriteObject(spriteRenderer, width, height);
        RigidBody rigidBody = new RigidBody();
        BoxCollider boxCollider = new BoxCollider(new Vector2d(objectHalfSize, objectHalfSize));

        colliderObject.addComponent(rigidBody);
        colliderObject.addComponent(boxCollider);

        return colliderObject;
    }

    public static GameObject generateStaticObject(SpriteRenderer spriteR, double width, double height){
        GameObject staticObject = generateColliderObject(spriteR, width, height);
        RigidBody  rigidBody = staticObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Static);

        return staticObject;
    }

    public static GameObject generateDynamicObject(SpriteRenderer spriteR, double width, double height){
        GameObject dynamicObject = generateColliderObject(spriteR, width, height);
        RigidBody  rigidBody = dynamicObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Dynamic);

        return dynamicObject;
    }

    public static GameObject generateBopObject(SpriteRenderer spriteRenderer, double width, double height){
        GameObject bopObject = generateStaticObject(spriteRenderer, width, height);
        bopObject.addComponent(new Hitable());

        return bopObject;
    }

    public static GameObject generateDynamicAnimatedObject(double width, double height, Animation animation){
        GameObject dynamicAnimatedObject = generateDynamicObject(new SpriteRenderer(), width, height);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animation);
        dynamicAnimatedObject.addComponent(stateMachine);

        return  dynamicAnimatedObject;
    }

    public static GameObject generateStaticAnimatedObject(double width, double height, Animation animation){
        GameObject staticAnimatedObject = generateStaticObject(new SpriteRenderer(), width, height);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animation);
        staticAnimatedObject.addComponent(stateMachine);

        return  staticAnimatedObject;
    }

    public static GameObject generateMario(double width, double height){
        Animation animation = new Animation("run");
        animation.addFrame(sheet.getSprite(0), 0.23);
        animation.addFrame(sheet.getSprite(2), 0.23);
        animation.addFrame(sheet.getSprite(3), 0.23);
        animation.addFrame(sheet.getSprite(2), 0.23);
        GameObject mario = generateDynamicAnimatedObject(width, height, animation);
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateQuestionBlock(double width, double height, BlockType blockType){
        Animation animation = new Animation("questionBlock");

        animation.addFrame(items.getSprite(0), 0.57f);
        animation.addFrame(items.getSprite(1), 0.23f);
        animation.addFrame(items.getSprite(2), 0.23f);

        GameObject questionBlock = generateStaticAnimatedObject(width, height, animation);
        questionBlock.addComponent(new QuestionBlockBeh(blockType));

        return questionBlock;
    }

    public static GameObject generateCoin(double width, double height){
        Animation animation = new Animation("coinFlip");
        animation.addFrame(items.getSprite(7), 0.57);
        animation.addFrame(items.getSprite(8), 0.23);
        animation.addFrame(items.getSprite(9), 0.23);

        GameObject coin = generateDynamicAnimatedObject(width, height, animation);
        coin.addComponent(new CoinBeh());

        return  coin;
    }
}
