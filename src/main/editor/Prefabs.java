package main.editor;

import main.components.*;
import main.games.mario.behaviour.*;
import main.components.Hitable;
import main.components.physicsComponent.*;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.games.mario.behaviour.CoinBeh;
import main.haspid.Direction;
import main.haspid.GameObject;
import main.games.mario.behaviour.QuestionBlockBeh.BlockType;
import main.haspid.Transform;
import main.physics.BodyType;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;

public class Prefabs {
    private static SpriteSheet items = AssetPool.getSpriteSheet(itemsConfig);
    private static SpriteSheet sheet = AssetPool.getSpriteSheet(smallFormConfig);


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
        width -= width / 10;
        height -= height / 10;
        Vector2d halfSize = new Vector2d(width / 10, height / 10);
        if(colliderType == ColliderType.Box) {
            collider = new BoxCollider(halfSize);
        }else if(colliderType == ColliderType.Circle){
            collider = new CircleCollider(halfSize.y);
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
        bopObject.setName("bopObject");
        bopObject.addComponent(new Hitable());

        return bopObject;
    }

    public static GameObject generateDynamicAnimatedObject(double width, double height, ColliderType colliderType, List<Animation> animationList, String name){
        GameObject dynamicAnimatedObject = generateDynamicObject(new SpriteRenderer(), width, height, colliderType);
        StateMachine stateMachine = new StateMachine(animationList.get(0).getTitle(), name, animationList);
        dynamicAnimatedObject.addComponent(stateMachine);

        return  dynamicAnimatedObject;
    }

    public static GameObject generateStaticAnimatedObject(double width, double height, ColliderType colliderType, List<Animation> animationList, String name){
        GameObject staticAnimatedObject = generateStaticObject(new SpriteRenderer(), width, height, colliderType);
        StateMachine stateMachine = new StateMachine(animationList.get(0).getTitle(), name, animationList);
        staticAnimatedObject.addComponent(stateMachine);

        return  staticAnimatedObject;
    }

    public static GameObject generateMario(double width, double height){
        GameObject mario = generateDynamicObject(new SpriteRenderer(AssetPool.getTexture(defaultTexturePath, true)), width, height, ColliderType.Box);
        StateMachine stateMachine = AssetPool.getStateMachine("smallMario");
        mario.addComponent(stateMachine);
        mario.addComponent(new PlayerController());
        mario.setName("mario");

        return mario;
    }

    public static GameObject generateGoomba(double width, double height){
        GameObject goomba = generateDynamicObject(new SpriteRenderer(), width, height, ColliderType.Circle);
        goomba.addComponent(AssetPool.getStateMachine("goomba"));
        goomba.addComponent(new GoombaBeh());
        goomba.setName("goomba");

        return goomba;
    }

    public static GameObject generateTurtle(double width, double height){
        GameObject turtle = generateDynamicObject(new SpriteRenderer(), width, height, ColliderType.Box);
        turtle.addComponent(AssetPool.getStateMachine("turtle"));
        turtle.addComponent(new TurtleBeh());
        turtle.setName("turtle");

        return turtle;
    }

    public static GameObject generateQuestionBlock(double width, double height, BlockType blockType){
        GameObject questionBlock = generateStaticObject(new SpriteRenderer(), width, height, ColliderType.Box);
        questionBlock.addComponent(AssetPool.getStateMachine("questionBlock"));
        questionBlock.addComponent(new QuestionBlockBeh(blockType));
        questionBlock.setName("questionBlock");

        return questionBlock;
    }

    public static GameObject generateCoin(double width, double height ){
        GameObject coin = generateStaticObject(new SpriteRenderer(), width, height,ColliderType.Circle);
        coin.addComponent(AssetPool.getStateMachine("coin"));
        coin.addComponent(new CoinBeh());
        coin.setName("coin");

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

    public static GameObject generatePipe(SpriteRenderer spriteRenderer, double width, double height, Direction direction){
        GameObject pipe = generateStaticObject(spriteRenderer, width, height, ColliderType.Box);
        pipe.addComponent(new PipeBeh(direction));
        pipe.setName("pipe");

        return pipe;
    }

    public static GameObject generateFlag(SpriteRenderer spriteRenderer, double width, double height){
        GameObject flag = generateStaticObject(spriteRenderer, width, height, ColliderType.Box);
        RigidBody rigidBody = flag.getComponent(RigidBody.class);
        rigidBody.setSensor(true);
        flag.addComponent(new FlagBeh());
        flag.setName("flag");

        return flag;
    }

    public static GameObject generateFireball(SpriteRenderer spriteRenderer, double width, double height, PlayerController playerController){
        GameObject fireball = generateDynamicObject(spriteRenderer, width, height, ColliderType.Circle);
        fireball.addComponent(new FireballBeh(playerController));
        fireball.setName("fireball");

        return fireball;
    }
}
