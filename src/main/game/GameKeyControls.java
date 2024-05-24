package main.game;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.KeyListener;
import main.haspid.Window;
import main.renderer.RenderBatch;

import java.util.Arrays;
import java.util.List;

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
            List<RenderBatch> rList = Window.getInstance().getCurrentScene().getRenderer().getRenderBatchList();

                for(RenderBatch rb: rList){
                    System.out.println(rb.getzIndex() + " : " +  rb.getSpriteCount()); // todo
                    for(SpriteRenderer sp: rb.getSpriteToRender()){
                        if(sp == null) continue;
                        System.out.println(sp.getParent().getName() + "\t: " + sp + "\t: "  + sp.getTexture().getFilePath() + "\t:" + sp.getSpriteID());
                    }
                    System.out.println();
                }
            }
            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }
}
