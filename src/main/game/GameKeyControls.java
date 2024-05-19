package main.game;

import main.components.Component;
import main.haspid.KeyListener;
import main.haspid.Window;

import static main.Configuration.keyDebounceC;
import static org.lwjgl.glfw.GLFW.*;

public class GameKeyControls extends Component {

    private double keyDebounce;
    private KeyListener keyboard;
    private double resetDebounce;

    public GameKeyControls(){
        this.keyDebounce = keyDebounceC;
        this.resetDebounce = keyDebounce;
        this.keyboard = KeyListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if(keyDebounce < 0) {
            if (keyboard.isKeyPressed(GLFW_KEY_2)) {
                System.out.println(Window.getInstance().getCurrentScene().getSceneObjectList().stream().toList());
                System.out.println(Window.getInstance().getCurrentScene().getPhysics().getWorld().getBodyCount());
            }
            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }
}
