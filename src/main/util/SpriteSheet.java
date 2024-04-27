package main.util;

import main.components.SpriteRenderer;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Texture parentTexture;
    private List<SpriteRenderer> spriteList;

    protected SpriteSheet(SpriteConfig config){
        this.spriteList = new ArrayList<>();
        this.parentTexture = config.texture;


        int spritesInColumn = parentTexture.getWidth() / config.spriteWidth;
        int spritesInRow = parentTexture.getHeight() / config.spriteHeight;
        float spriteWidth = 1f / (float) spritesInColumn;
        float spriteHeight = 1f / (float) spritesInRow;
        for(int i = 0; i < config.numSprites; i++){

            int column = i;
            int row = spritesInRow - 1;
            if(column >= spritesInColumn){
                column -= (i / spritesInColumn) * spritesInColumn;
                row = row - (i / spritesInColumn);
            }

            float x0 = column * spriteWidth;
            float x1 = column * spriteWidth + spriteWidth;
            float y0 = row * spriteHeight;;
            float y1 = row * spriteHeight + spriteHeight;

            spriteList.add(new SpriteRenderer(parentTexture,config.spriteWidth, config.spriteHeight,  new Vector2f[]{
                    new Vector2f(x1, y1),
                    new Vector2f(x1, y0),
                    new Vector2f(x0, y0),
                    new Vector2f(x0, y1)
            }));
        }
    }

    public SpriteRenderer getSprite(int index){
        return spriteList.get(index);
    }

    public int getSize(){
        return spriteList.size();
    }
}
