package main.components.behaviour;

import main.components.Component;
import main.haspid.Transform;
import main.haspid.Window;
import main.util.AssetPool;
import org.joml.Vector2d;

import static main.Configuration.*;

public class CoinBeh extends Component {
    private double speed;
    private double distance;
    private transient double maxPosY;

    public CoinBeh(){
        this.speed = 15;
        this.distance = 8;
    }

    @Override
    public void start(){
        maxPosY = getParent().getTransform().getPosition().y + distance;
        AssetPool.getSound(coin).play();
    }

    @Override
    public void update(float dt) {
        Transform t = getParent().getTransform();
        Vector2d pos = t.getPosition();
        if(pos.y < maxPosY){
            pos.y += dt * speed;
            System.out.println();
            t.getScale().x -= (0.5f * dt) % -1;
        }else{
            Window.getInstance().getCurrentScene().removeFromSceneRuntime(getParent());
        }
    }
}
