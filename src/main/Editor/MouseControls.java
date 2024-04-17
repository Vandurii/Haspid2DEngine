package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import org.joml.Vector2f;

import static main.Configuration.gridSize;
import static main.Configuration.zoom;
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
        Vector2f camPos = Window.getInstance().getCurrentScene().getCamera().getPosition();

        if(holdingObject != null){
            int objectX = (int)(((mouse.getWorldX() * zoom) + camPos.x) / gridSize) * gridSize;
            int objectY = (int)(((mouse.getWorldY() * zoom) + camPos.y) / gridSize) * gridSize;
            holdingObject.getTransform().setPosition(new Vector2f(objectX, objectY));
            if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
                place();
            }
        }
    }
}
