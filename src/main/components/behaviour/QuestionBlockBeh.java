package main.components.behaviour;

import main.components.PlayerController;
import main.components.Hitable;
import main.components.SpriteRenderer;
import main.components.physicsComponent.ColliderType;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.editor.Prefabs;
import main.haspid.GameObject;
import main.haspid.Scene;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2d;
import main.components.PlayerController.PlayerState;

import static main.Configuration.*;

public class QuestionBlockBeh extends Hitable {
    public enum BlockType {
        Coin,
        PowerUp,
        Invincibility
    }

    private BlockType blockType;
    private transient boolean isUsed;
    private transient PlayerState playerState;

    public QuestionBlockBeh(BlockType blockType){
        this.blockType = blockType;
    }

    @Override
    public void update(float dt){

    }

    @Override
    public QuestionBlockBeh copy(){
        return new QuestionBlockBeh(blockType);
    }

    @Override
    public void playerHit(PlayerController playerController) {
        if(isUsed) return;

        playerState = playerController.getPlayerState();

        switch (blockType){
            case Coin -> doCoin();
            case PowerUp ->  doPowerUp();
            case Invincibility -> doInvincibility();
        }

        isUsed = true;
//        StateMachine stateMachine = getParent().getComponent(StateMachine.class);
//        Window.getInstance().getCurrentScene().removeComponentRuntime(getParent(), stateMachine);
//
//        SpriteRenderer spriteRenderer = getParent().getComponent(SpriteRenderer.class);
//        spriteRenderer.markToRemove();
//        SpriteSheet items = AssetPool.getSpriteSheet(itemsConfig);
//       // Window.getInstance().getCurrentScene().addComponentRuntime(getParent(), new SpriteRenderer(items.getSprite(4).getTexture()));
    }

    public void doCoin(){
        GameObject coin = Prefabs.generateCoin(standardSpriteSize, standardSpriteSize);
        coin.getTransform().getPosition().set(getParent().getTransform().getPosition());
        Window.getInstance().getCurrentScene().addObjectToSceneRunTime(coin);
    }

    public void doPowerUp(){
        if(playerState == PlayerState.small) {
            spawnMushroom();
        }else if(playerState == PlayerState.big) {
            spawnFlower();
        }
    }

    public void doInvincibility(){

    }

    public void spawnFlower(){
        GameObject flower = Prefabs.generateFlower(standardSpriteSize, standardSpriteSize);
        Vector2d pos = getParent().getTransform().getPosition();
        flower.getTransform().setPosition(pos.x, pos.y + objectHalfSize * 2);
        Window.getInstance().getCurrentScene().addObjectToSceneRunTime(flower);
    }

    public void spawnMushroom(){
        GameObject mushroom = Prefabs.generateMushroom(standardSpriteSize, standardSpriteSize);
        Vector2d pos = getParent().getTransform().getPosition();
        mushroom.getTransform().setPosition(pos.x, pos.y + objectHalfSize * 2);
        Window.getInstance().getCurrentScene().addObjectToSceneRunTime(mushroom);
    }
}
