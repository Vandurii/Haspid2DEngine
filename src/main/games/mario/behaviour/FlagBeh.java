package main.games.mario.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.components.physicsComponent.RigidBody;
import main.haspid.GameObject;
import main.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.stageClear;

public class FlagBeh extends Component {

    @Override
    public void update(float dt) {

    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2d contactNormal){
        PlayerController playerController = gameObject.getComponent(PlayerController.class);

        if(playerController != null){
           playerController.getParent().getComponent(RigidBody.class).setSensor(true);
            AssetPool.getSound(stageClear).play();

            if(!playerController.isWin()) {
                playerController.setWin(true);
            }
        }
    }
}
