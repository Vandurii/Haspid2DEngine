package main.components;

import main.haspid.*;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.sql.SQLOutput;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class MouseControls extends Component implements Helper {
    private GameObject holdingObject;

    public void place(){
        holdingObject = null;
    }

    public void pickupObject(GameObject holdingObject){
        Window.getInstance().getCurrentScene().addGameObjectToScene(holdingObject);
        this.holdingObject = holdingObject;
    }

    @Override
    public void update(float dt) {
        MouseListener mouse = MouseListener.getInstance();

        if(isNotNull(holdingObject)){
            holdingObject.getTransform().setPosition(new Vector2f((float) mouse.getOrthoX() - 16, (float) mouse.getOrthoY() - 16));
            if(mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1)){
                place();
            }
        }
    }

    @Override
    public boolean isNotNull(Object obj) {
        return obj != null;
    }
}
