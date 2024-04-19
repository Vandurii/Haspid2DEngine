package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.KeyListener;
import main.haspid.Window;
import main.renderer.RenderBatch;
import main.renderer.Renderer;
import main.scene.Scene;
import main.util.AssetPool;

import java.util.List;

import static main.Configuration.keyDebounceC;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private static KeyControls instance;

    private float keyDebounce;
    private KeyListener keyboard;
    private float resetDebounce;
    private MouseControls mouseControls;

    private KeyControls(){
        this.keyDebounce = keyDebounceC;
        this.resetDebounce = keyDebounce;
        this.keyboard = KeyListener.getInstance();
        this.mouseControls = MouseControls.getInstance();
    }

    public static KeyControls getInstance(){
        if(instance == null) instance = new KeyControls();

        return instance;
    }

    @Override
    public void update(float dt) {
        GameObject activeObject = mouseControls.getActiveGameObject();
        Scene scene = Window.getInstance().getCurrentScene();

        if(keyDebounce < 0) {
            if (keyboard.isKeyPressed(GLFW_KEY_P)) {
                printInfo();
            } else if (keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
                if(mouseControls.getCursorObject() != null){
                    mouseControls.clearCursor();
                }else {
                   mouseControls.setActiveGameObject(null);
                }
            } else if (keyboard.isKeyPressed(GLFW_KEY_L)) {
                List<RenderBatch> rList = Renderer.getInstance().getRenderBatchList();
                for (RenderBatch rb : rList) {
                    if (rb.getzIndex() == -5) {
                        System.out.println(rb.getFreeSlots().stream().toList());
                    }
                }
            }else if(keyboard.isKeyPressed(GLFW_KEY_5)){
                Gizmo.getInstance().setGizmoIndex(0);
            }else if(keyboard.isKeyPressed(GLFW_KEY_6)){
                Gizmo.getInstance().setGizmoIndex(1);
            }else if(keyboard.isKeyPressed(GLFW_KEY_7)){
                Gizmo.getInstance().setGizmoIndex(2);
            }else if(keyboard.isKeyPressed(GLFW_KEY_DELETE) && activeObject != null){
                SpriteRenderer spriteRenderer = activeObject.getComponent(SpriteRenderer.class);
                if(spriteRenderer != null){
                    spriteRenderer.markToRemove();
                    scene.removeFromScene(activeObject);
                    mouseControls.setActiveGameObject(null);
                }
            }

            keyDebounce = resetDebounce;
        }
        keyDebounce -= dt;
    }

    public void printInfo(){
        for(RenderBatch rb: Renderer.getInstance().getRenderBatchList()){
            rb.printPointsValues();
        }
        AssetPool.printResourcesInAssetPool();
        Scene.printSceneObjects();
    }
}
