package main.components.behaviour;

import main.components.Component;
import main.components.PlayerController;
import main.editor.JImGui;
import main.haspid.Direction;
import main.haspid.GameObject;
import main.haspid.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;

import static main.Configuration.gridSize;

public class PipeBeh extends Component {
    private Direction direction;
    private double entranceTolerance;
    private transient GameObject connectingPipe;
    private String connectingPipeName;

    public PipeBeh(Direction direction){
        this.direction = direction;
        this.entranceTolerance = 0.6;
        connectingPipeName = "pipe";
    }

    @Override
    public void start(){
        connectingPipe = Window.getInstance().getCurrentScene().getGameObjectByName(connectingPipeName);
    }

    public void dearGui(){
        entranceTolerance = (float) JImGui.drawValue("Tolerance: ", entranceTolerance, this.hashCode() + "");
        connectingPipeName = (String) JImGui.drawValue("Exit pipe name: ", connectingPipeName, this.hashCode() + "");
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2d contactNormal){
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if(playerController != null){
            if((direction == Direction.Right) && (contactNormal.x > entranceTolerance) ){
                travelThroughPipe(playerController);
            }else if((direction == Direction.Left) && (contactNormal.x < -entranceTolerance)){
               travelThroughPipe(playerController);
            }else if((direction == Direction.Up) && (contactNormal.y > entranceTolerance)){
                travelThroughPipe(playerController);
            }
        }
    }

    public void travelThroughPipe(PlayerController playerController){
        Vector2d pos = connectingPipe.getTransform().getPosition();
        Direction dir = connectingPipe.getComponent(PipeBeh.class).getDirection();

        switch (dir){
            case Up -> {
                playerController.setPosition(new Vector2d(pos.x, pos.y + gridSize * 4));
            }
            case Down -> {
                playerController.setPosition(new Vector2d(pos.x, pos.y - gridSize * 4));
            }
            case Right -> {
                playerController.setPosition(new Vector2d(pos.x + gridSize * 4, pos.y));
            }
            case Left -> {
                playerController.setPosition(new Vector2d(pos.x - gridSize * 4, pos.y));
            }
        }
    }
    public String getConnectingPipeName() {
        return connectingPipeName;
    }

    public void setConnectingPipeName(String connectingPipeName) {
        this.connectingPipeName = connectingPipeName;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
