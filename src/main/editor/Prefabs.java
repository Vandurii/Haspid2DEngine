package main.editor;

import main.components.*;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.physics.BodyType;
import org.joml.Vector2d;

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

    public static GameObject generateMario(double width, double height, Animation animation){
        GameObject mario = generateDynamicAnimatedObject(width, height, animation);
        mario.addComponent(new PlayerController());

        return mario;
    }
}
