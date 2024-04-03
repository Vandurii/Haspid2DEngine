package main.renderer;

import main.components.SpriteRenderer;
import main.haspid.GameObject;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public class Renderer {
    private int maxBatchSize = batchSize;
    private List<RenderBatch> rendererBatchList;

    public Renderer(){
        rendererBatchList = new ArrayList<>();
    }

    public void add(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) add(spriteRenderer);
    }

    public void add(SpriteRenderer spriteRenderer){

        boolean added = false;
        for(RenderBatch rBatch: rendererBatchList){
            if(rBatch.hasRoom()){
                if(rBatch.hasTextureListRoom() || rBatch.hasTexture(spriteRenderer.getSprite().getTexture())) {
                    rBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            RenderBatch newRenderBatch = new RenderBatch(maxBatchSize);
            newRenderBatch.start();
            newRenderBatch.addSprite(spriteRenderer);
            rendererBatchList.add(newRenderBatch);
        }
    }

    public void render(){
        for(RenderBatch rBatch: rendererBatchList){
            rBatch.render();
        }
    }

    public List<RenderBatch> getRenderBatchList(){
        return rendererBatchList;
    }
}
