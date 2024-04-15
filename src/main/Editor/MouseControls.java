package main.Editor;

import main.components.Component;
import main.haspid.*;
import org.joml.Vector2f;

import static main.Configuration.gridSize;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class MouseControls extends Component {
    private static MouseControls instance;
    private static GameObject holdingObject;

    private MouseControls(){};

    public void place(){
        holdingObject = null;
    }

    public static MouseControls getInstance(){
        if(instance == null) instance = new MouseControls();

        return instance;
    }

    public void pickupObject(GameObject holdingObject){
        Window.getInstance().getCurrentScene().addGameObjectToScene(holdingObject);
        this.holdingObject = holdingObject;
    }

    @Override
    public void update(float dt) {
        MouseListener mouse = MouseListener.getInstance();

        if(holdingObject != null){
            int  objectX = (int)(mouse.getWorldX() / gridSize) * gridSize;
            int  objectY = (int)(mouse.getWorldY() / gridSize) * gridSize;

            holdingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
            if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
                place();
            }
        }
    }
}
