package main.game;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.KeyListener;
import main.haspid.Window;
import main.physics.events.Event;
import main.physics.events.EventSystem;
import main.physics.events.EventType;
import main.renderer.RenderBatch;

import java.util.List;

import static main.Configuration.keyShortCooldown;
import static org.lwjgl.glfw.GLFW.*;

public class GameKeyControls extends Component {

    private double keyDebounce;
    private KeyListener keyboard;
    private double resetDebounce;

    public GameKeyControls(){
        this.keyDebounce = keyShortCooldown;
        this.resetDebounce = keyDebounce;
        this.keyboard = KeyListener.getInstance();
    }

    @Override
    public void update(float dt) {
        if(keyDebounce < 0) {
            if (keyboard.isKeyPressed(GLFW_KEY_2)) {
            }else if(keyboard.isKeyPressed(GLFW_KEY_ESCAPE)){
                EventSystem.notify(null, new Event(EventType.GameEngineStop));
                glfwRestoreWindow(Window.getInstance().getGlfwWindow());
            }
            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }
}
