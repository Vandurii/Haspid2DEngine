package main.game;

import main.components.Component;
import main.haspid.KeyListener;
import main.haspid.MouseListener;
import main.haspid.Window;
import main.scene.EditorScene;

import static main.Configuration.keyDebounceC;
import static org.lwjgl.glfw.GLFW.*;

public class GameKeyControls extends Component {

    private float keyDebounce;
    private KeyListener keyboard;
    private float resetDebounce;

    public GameKeyControls(){
        this.keyDebounce = keyDebounceC;
        this.resetDebounce = keyDebounce;
        this.keyboard = KeyListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if(keyDebounce < 0) {
            if (keyboard.isKeyPressed(GLFW_KEY_2)) {
                System.out.println("2 was pressed");
            }
            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }
}
