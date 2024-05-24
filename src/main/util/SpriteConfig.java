package main.util;

import main.util.Texture;

public class SpriteConfig {
    public String name;
    public int spacing;
    public boolean flip;
    public int numSprites;
    public Texture texture;
    public String filePath;
    public int spriteWidth;
    public int spriteHeight;

    public SpriteConfig(String filePath, int spriteWidth, int spriteHeight, int numSprites, int spacing, String name, boolean flip) {
        this.name = name;
        this.flip = flip;
        this.spacing = spacing;
        this.filePath = filePath;
        this.numSprites = numSprites;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }
}
