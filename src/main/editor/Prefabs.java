package main.editor;

import main.components.*;
import main.components.behaviour.CoinBeh;
import main.components.Hitable;
import main.components.behaviour.FlowerBeh;
import main.components.behaviour.MushroomBeh;
import main.components.behaviour.QuestionBlockBeh;
import main.components.physicsComponent.*;
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
    private static SpriteSheet sheet = AssetPool.getSpriteSheet(smallForm);


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

    public static GameObject generateColliderObject(SpriteRenderer spriteRenderer, double width, double height, ColliderType colliderType){
        GameObject colliderObject = generateSpriteObject(spriteRenderer, width, height);
        RigidBody rigidBody = new RigidBody();

        Collider collider = null;
        if(colliderType == ColliderType.Box) {
            collider = new BoxCollider(new Vector2d(objectHalfSize, objectHalfSize));
        }else if(colliderType == ColliderType.Circle){
            collider = new CircleCollider(objectHalfSize);
        }else if(colliderType == ColliderType.PillBox){
            collider = new PillboxCollider();
        }else{
            throw new IllegalStateException("Error in Prefabs class: unknown collider type");
        }

        colliderObject.addComponent(rigidBody);
        colliderObject.addComponent(collider);

        return colliderObject;
    }

    public static GameObject generateStaticObject(SpriteRenderer spriteR, double width, double height, ColliderType colliderType){
        GameObject staticObject = generateColliderObject(spriteR, width, height, colliderType);
        RigidBody  rigidBody = staticObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Static);

        return staticObject;
    }

    public static GameObject generateDynamicObject(SpriteRenderer spriteR, double width, double height, ColliderType colliderType){
        GameObject dynamicObject = generateColliderObject(spriteR, width, height, colliderType);
        RigidBody  rigidBody = dynamicObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Dynamic);

        return dynamicObject;
    }

    public static GameObject generateBopObject(SpriteRenderer spriteRenderer, double width, double height, ColliderType colliderType){
        GameObject bopObject = generateStaticObject(spriteRenderer, width, height, colliderType);
        bopObject.addComponent(new Hitable());

        return bopObject;
    }

    public static GameObject generateDynamicAnimatedObject(double width, double height, ColliderType colliderType, Animation ...animations){
        GameObject dynamicAnimatedObject = generateDynamicObject(new SpriteRenderer(), width, height, colliderType);
        StateMachine stateMachine = new StateMachine(animations[0].getTitle());
        for(Animation animation: animations){
            stateMachine.addState(animation);
        }
        dynamicAnimatedObject.addComponent(stateMachine);

        return  dynamicAnimatedObject;
    }

    public static GameObject generateStaticAnimatedObject(double width, double height, ColliderType colliderType, Animation ...animations){
        GameObject staticAnimatedObject = generateStaticObject(new SpriteRenderer(), width, height, colliderType);
        StateMachine stateMachine = new StateMachine(animations[0].getTitle());
        for(Animation animation: animations){
            stateMachine.addState(animation);
        }
        staticAnimatedObject.addComponent(stateMachine);

        return  staticAnimatedObject;
    }

    public static GameObject generateMario(double width, double height){
        GameObject mario = generateDynamicObject(new SpriteRenderer(), width, height, ColliderType.Box);
        mario.addComponent(AssetPool.getStateMachine("smallMario"));
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateQuestionBlock(double width, double height, BlockType blockType){
        Animation animation = new Animation("questionBlock", true);

        animation.addFrame(items.getSprite(0), 0.57f);
        animation.addFrame(items.getSprite(1), 0.23f);
        animation.addFrame(items.getSprite(2), 0.23f);

        GameObject questionBlock = generateStaticAnimatedObject(width, height, ColliderType.Box, animation);
        questionBlock.addComponent(new QuestionBlockBeh(blockType));

        return questionBlock;
    }

    public static GameObject generateCoin(double width, double height ){
        Animation animation = new Animation("coinFlip", true);
        animation.addFrame(items.getSprite(7), 0.57);
        animation.addFrame(items.getSprite(8), 0.23);
        animation.addFrame(items.getSprite(9), 0.23);

        GameObject coin = generateStaticAnimatedObject(width, height,ColliderType.Circle,  animation);
        coin.setName("coin");
        coin.addComponent(new CoinBeh());

        return  coin;
    }

    public static GameObject generateFlower(double width, double height){
        GameObject flower = generateStaticObject(items.getSprite(20), width, height, ColliderType.Circle);

        flower.setName("flower");
        flower.addComponent(new FlowerBeh());

        RigidBody rigidBody = flower.getComponent(RigidBody.class);
        rigidBody.setSensor(true);

        return flower;
    }

    public static GameObject generateMushroom(double width, double height){
        GameObject flower = generateDynamicObject(items.getSprite(10), width, height, ColliderType.Circle);

        flower.setName("mushroom");
        flower.addComponent(new MushroomBeh());

        return flower;
    }
}
