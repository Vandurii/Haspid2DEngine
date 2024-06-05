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


    private static GameObject generateObject(Vector2d scale){
        GameObject holdingObject = new GameObject("Generated");
        holdingObject.addComponent(new Transform(new Vector2d(), new Vector2d(scale.x, scale.y)));
        holdingObject.setTransformFromItself();

        return holdingObject;
    }

    public static GameObject generateSpriteObject(SpriteRenderer spriteR, Vector2d scale){
        GameObject holdingObject = generateObject(scale);
        SpriteRenderer spriteRenderer = new SpriteRenderer(spriteR.getTexture(), scale.x, scale.y, spriteR.getSpriteCords());
        holdingObject.addComponent(spriteRenderer);

        return holdingObject;
    }

    public static GameObject generateColliderObject(SpriteRenderer spriteRenderer, Vector2d scale, ColliderType colliderType){
        GameObject colliderObject = generateSpriteObject(spriteRenderer, scale);
        RigidBody rigidBody = new RigidBody();

        Collider collider = null;
        if(colliderType == ColliderType.Box) {
            collider = new BoxCollider();
        }else if(colliderType == ColliderType.Circle){
            collider = new CircleCollider(scale.x / 2);
        }else if(colliderType == ColliderType.PillBox){
            collider = new PillboxCollider();
        }else{
            throw new IllegalStateException("Error in Prefabs class: unknown collider type");
        }

        colliderObject.addComponent(rigidBody);
        colliderObject.addComponent(collider);

        return colliderObject;
    }

    public static GameObject generateStaticObject(SpriteRenderer spriteR, Vector2d scale, ColliderType colliderType){
        GameObject staticObject = generateColliderObject(spriteR, scale, colliderType);
        RigidBody  rigidBody = staticObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Static);

        return staticObject;
    }

    public static GameObject generateDynamicObject(SpriteRenderer spriteR, Vector2d scale, ColliderType colliderType){
        GameObject dynamicObject = generateColliderObject(spriteR, scale, colliderType);
        RigidBody  rigidBody = dynamicObject.getComponent(RigidBody.class);
        rigidBody.setBodyType(BodyType.Dynamic);

        return dynamicObject;
    }

    public static GameObject generateBopObject(SpriteRenderer spriteRenderer, Vector2d scale, ColliderType colliderType){
        GameObject bopObject = generateStaticObject(spriteRenderer, scale, colliderType);
        bopObject.setName("bopObject");
        bopObject.addComponent(new Hitable());

        return bopObject;
    }

    public static GameObject generateDynamicAnimatedObject(Vector2d scale, ColliderType colliderType, List<Animation> animationList, String name){
        GameObject dynamicAnimatedObject = generateDynamicObject(new SpriteRenderer(), scale, colliderType);
        StateMachine stateMachine = new StateMachine(animationList.get(0).getTitle(), name, animationList);
        dynamicAnimatedObject.addComponent(stateMachine);

        return  dynamicAnimatedObject;
    }

    public static GameObject generateStaticAnimatedObject(Vector2d scale, ColliderType colliderType, List<Animation> animationList, String name){
        GameObject staticAnimatedObject = generateStaticObject(new SpriteRenderer(), scale, colliderType);
        StateMachine stateMachine = new StateMachine(animationList.get(0).getTitle(), name, animationList);
        staticAnimatedObject.addComponent(stateMachine);

        return  staticAnimatedObject;
    }

    public static GameObject generateMario(Vector2d scale){
        GameObject mario = generateDynamicObject(new SpriteRenderer(AssetPool.getTexture(defaultTexturePath, true)), scale, ColliderType.Box);
        StateMachine stateMachine = AssetPool.getStateMachine("smallMario");
        mario.addComponent(stateMachine);
        mario.addComponent(new PlayerController());
        mario.setName("mario");

        return mario;
    }

    public static GameObject generateGoomba(Vector2d scale){
        GameObject goomba = generateDynamicObject(new SpriteRenderer(), scale, ColliderType.Box);
        goomba.addComponent(AssetPool.getStateMachine("goomba"));
        goomba.addComponent(new GoombaBeh());
        goomba.setName("goomba");

        return goomba;
    }

    public static GameObject generateTurtle(Vector2d scale){
        GameObject turtle = generateDynamicObject(new SpriteRenderer(), scale, ColliderType.Box);
        turtle.addComponent(AssetPool.getStateMachine("turtle"));
        turtle.addComponent(new TurtleBeh());
        turtle.setName("turtle");

        return turtle;
    }

    public static GameObject generateQuestionBlock(Vector2d scale, BlockType blockType){
        GameObject questionBlock = generateStaticObject(new SpriteRenderer(), scale, ColliderType.Box);
        questionBlock.addComponent(AssetPool.getStateMachine("questionBlock"));
        questionBlock.addComponent(new QuestionBlockBeh(blockType));
        questionBlock.setName("questionBlock");

        return questionBlock;
    }

    public static GameObject generateCoin(Vector2d scale){
        GameObject coin = generateStaticObject(new SpriteRenderer(), scale,ColliderType.Circle);
        coin.addComponent(AssetPool.getStateMachine("coin"));
        coin.addComponent(new CoinBeh());
        coin.setName("coin");

        return  coin;
    }

    public static GameObject generateFlower(Vector2d scale){
        GameObject flower = generateStaticObject(items.getSprite(20), scale, ColliderType.Circle);

        flower.setName("flower");
        flower.addComponent(new FlowerBeh());

        RigidBody rigidBody = flower.getComponent(RigidBody.class);
        rigidBody.setSensor(true);

        return flower;
    }

    public static GameObject generateMushroom(Vector2d scale){
        GameObject flower = generateDynamicObject(items.getSprite(10), scale, ColliderType.Circle);

        flower.setName("mushroom");
        flower.addComponent(new MushroomBeh());

        return flower;
    }

    public static GameObject generatePipe(SpriteRenderer spriteRenderer, Vector2d scale, Direction direction){
        GameObject pipe = generateStaticObject(spriteRenderer, scale, ColliderType.Box);
        pipe.addComponent(new PipeBeh(direction));
        pipe.setName("pipe");

        return pipe;
    }

    public static GameObject generateFlag(SpriteRenderer spriteRenderer, Vector2d scale){
        GameObject flag = generateStaticObject(spriteRenderer, scale, ColliderType.Box);
        RigidBody rigidBody = flag.getComponent(RigidBody.class);
        rigidBody.setSensor(true);
        flag.addComponent(new FlagBeh());
        flag.setName("flag");

        return flag;
    }

    public static GameObject generateFireball(SpriteRenderer spriteRenderer, Vector2d scale, PlayerController playerController, boolean goingRight){
        GameObject fireball = generateDynamicObject(spriteRenderer, scale, ColliderType.Circle);
        fireball.addComponent(new FireballBeh(playerController, goingRight));
        fireball.setName("fireball");

        return fireball;
    }
}
