package main.components.behaviour;

import main.components.PlayerController;
import main.components.Hitable;
import main.components.physicsComponent.ColliderType;
import main.editor.Prefabs;
import main.haspid.GameObject;
import main.haspid.Scene;
import main.haspid.Window;
import org.joml.Vector2d;

import static main.Configuration.*;

public class QuestionBlockBeh extends Hitable {
    public enum BlockType {
        Coin,
        PowerUp,
        Invincibility
    }

    private BlockType blockType;
    private transient boolean isUsed;

    public QuestionBlockBeh(BlockType blockType){
        this.blockType = blockType;
    }

    @Override
    public void playerHit(PlayerController playerController) {
        if(isUsed) return;

        switch (blockType){
            case Coin -> doCoin();
            case PowerUp ->  doPowerUp();
            case Invincibility -> doInvincibility();
        }

        isUsed = true;
    }

    public void doCoin(){
        GameObject coin = Prefabs.generateCoin(standardSpriteSize, standardSpriteSize);
        coin.getTransform().getPosition().set(getParent().getTransform().getPosition());
        Window.getInstance().getCurrentScene().addGameObjectToScene(coin);
    }

    public void doPowerUp(){
        spawnFlower();
    }

    public void doInvincibility(){

    }

    public void spawnFlower(){
        GameObject flower = Prefabs.generateFlower(standardSpriteSize, standardSpriteSize);
        Vector2d pos = getParent().getTransform().getPosition();
        flower.getTransform().setPosition(pos.x, pos.y + objectHalfSize * 2);
        Window.getInstance().getCurrentScene().addGameObjectToScene(flower);
    }
}
