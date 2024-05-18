package main.components.behaviour;

import main.components.PlayerController;
import main.components.Hitable;
import main.editor.Prefabs;
import main.haspid.GameObject;
import main.haspid.Window;

import static main.Configuration.standardSpriteSize;

public class QuestionBlockBeh extends Hitable {
    public enum BlockType {
        Coin,
        PowerUp,
        Invincibility
    }

    private boolean isUsed;
    private BlockType blockType;

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

    }

    public void doInvincibility(){

    }
}
