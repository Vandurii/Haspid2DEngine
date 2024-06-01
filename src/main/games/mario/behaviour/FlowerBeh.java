package main.games.mario.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.haspid.GameObject;
import main.haspid.Window;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.powerUpAppears;

public class FlowerBeh extends Component {

    @Override
    public void init(){
        AssetPool.getSound(powerUpAppears).play();
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if(playerController != null){

            playerController.powerUP();
            Window.getInstance().getCurrentScene().removeFromSceneSafe(getParent());
        }
    }
}
