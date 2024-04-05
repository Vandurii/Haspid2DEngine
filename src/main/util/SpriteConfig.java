package main.util;

import main.util.Texture;

public class SpriteConfig {
    public String filePath;
    public int spriteWidth;
    public int spriteHeight;
    public int numSprites;
    public int spacing;
    public Texture texture;

    public SpriteConfig(String filePath, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.filePath = filePath;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.numSprites = numSprites;
        this.spacing = spacing;
    }
}
