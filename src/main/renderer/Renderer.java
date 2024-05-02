package main.renderer;

import main.components.SpriteRenderer;
import main.haspid.GameObject;
import main.haspid.MouseListener;
import main.haspid.Transform;
import main.util.AssetPool;
import main.util.Shader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static main.Configuration.*;

public class Renderer {
    private static int maxBatchSize = batchSize;
    private static List<RenderBatch> rendererBatchList;
    private static Shader currentShader;
    private static Renderer instance;
    private static List<SpriteRenderer> spriteRenderersToRelocate;

    private Renderer(){
        currentShader = AssetPool.getShader(defaultShaderPath);
        rendererBatchList = new ArrayList<>();
        spriteRenderersToRelocate = new ArrayList<>();
    }

    public static Renderer getInstance(){
        if(instance == null) instance = new Renderer();

        return  instance;
    }

    public static void resetInstance(){
        instance = new Renderer();
    }

    public void add(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) add(spriteRenderer);
    }

    public void add(SpriteRenderer spriteRenderer){
        Transform transform = spriteRenderer.getParent().getTransform();

        boolean added = false;
        for(RenderBatch rBatch: rendererBatchList){
            if(rBatch.hasRoom() && rBatch.getzIndex() == transform.getZIndex()){
                if(rBatch.hasTextureListRoom() || rBatch.hasTexture(spriteRenderer.getTexture())) {
                    rBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            RenderBatch newRenderBatch = new RenderBatch(maxBatchSize, transform.getZIndex());
            newRenderBatch.start();
            newRenderBatch.addSprite(spriteRenderer);
            rendererBatchList.add(newRenderBatch);
            Collections.sort(rendererBatchList);
        }
    }

    public void render(){
        for(RenderBatch rBatch: rendererBatchList){
            rBatch.render();
        }

        for(SpriteRenderer spriteRenderer: spriteRenderersToRelocate){
            add(spriteRenderer);
            spriteRenderer.setDirty();
        }
        spriteRenderersToRelocate.clear();
    }

    public void replaceShader(Shader shader){
        currentShader  = shader;
    }

    public Shader getShader(){
        return currentShader;
    }

    public List<RenderBatch> getRenderBatchList(){
        return rendererBatchList;
    }

    public void addToRelocateList(SpriteRenderer spriteRenderer){
        spriteRenderersToRelocate.add(spriteRenderer);
    }
}
