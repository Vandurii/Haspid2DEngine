package main.games.mario.behaviour;

import main.components.PlayerController;
import main.components.Hitable;
import main.components.stateMachine.StateMachine;
import main.editor.Prefabs;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.haspid.Window;
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
    private transient StateMachine stateMachine;

    private transient double coinSize;
    private transient double flowerSize;
    private transient double mushroomSize;

    private Transform transform;
    private Vector2d pos;
    private Vector2d scale;

    public QuestionBlockBeh(BlockType blockType){
        this.coinSize = flowerSize = mushroomSize = standardSpriteSize;
        this.blockType = blockType;
    }

    @Override
    public void init(){
        transform = getParent().getTransform();
        pos = transform.getPosition();
        scale = transform.getScale();
        this.stateMachine = getParent().getComponent(StateMachine.class);
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
        stateMachine.switchAnimation("inactive");
    }

    public void doCoin(){
        GameObject coin = Prefabs.generateCoin(new Vector2d(coinSize));
        coin.getTransform().getPosition().set(pos);
        Window.getInstance().getCurrentScene().addObjectToSceneSafe(coin);
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
        GameObject flower = Prefabs.generateFlower(new Vector2d(flowerSize));
        flower.getTransform().setPosition(pos.x, pos.y + scale.y);
        Window.getInstance().getCurrentScene().addObjectToSceneSafe(flower);
    }

    public void spawnMushroom(){
        GameObject mushroom = Prefabs.generateMushroom(new Vector2d(mushroomSize));
        mushroom.getTransform().setPosition(pos.x, pos.y + scale.y);
        Window.getInstance().getCurrentScene().addObjectToSceneSafe(mushroom);
    }
}
