package main.util;

import main.haspid.Writable;

public class SpriteConfig implements Writable {
    public String name;
    public int spacing;
    public boolean flip;
    public int numSprites;
    public Texture texture;
    public String filePath;
    public int spriteWidth;
    public int spriteHeight;
    public double worldScalar;

    public SpriteConfig(String filePath, int spriteWidth, int spriteHeight, int numSprites, int spacing, double worldScalar, String name, boolean flip) {
        this.name = name;
        this.flip = flip;
        this.spacing = spacing;
        this.filePath = filePath;
        this.numSprites = numSprites;
        this.spriteWidth = spriteWidth;
        this.worldScalar = worldScalar;
        this.spriteHeight = spriteHeight;
    }

    public SpriteConfig getClone(){
        return new SpriteConfig(filePath, spriteWidth, spriteHeight, numSprites, spacing, worldScalar, name, flip);
    }

    @Override
    public String getName() {
        return name;
    }

}
