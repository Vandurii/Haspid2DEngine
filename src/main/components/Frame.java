package main.components;

public class Frame {
    private float frameTime;
    private SpriteRenderer spriteRenderer;

    public Frame(SpriteRenderer spriteRenderer, float frameTime){
        this.frameTime = frameTime;
        this.spriteRenderer = spriteRenderer;
    }

    public float getFrameTime() {
        return frameTime;
    }

    public Frame copy(){
        return new Frame(spriteRenderer.copy(), frameTime);
    }

    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }

    public SpriteRenderer getSpriteRenderer() {
        return spriteRenderer;
    }

    public void setSpriteRenderer(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
    }
}
