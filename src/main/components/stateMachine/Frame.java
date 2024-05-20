package main.components.stateMachine;

import main.components.SpriteRenderer;

public class Frame {
    private double frameTime;
    private SpriteRenderer spriteRenderer;
    private boolean used;

    public Frame(SpriteRenderer spriteRenderer, double frameTime){
        this.frameTime = frameTime;
        this.spriteRenderer = spriteRenderer;
    }

    public double getFrameTime() {
        used = true;
        return frameTime;
    }

    public boolean isUsed(){
        return used;
    }

    public void setUsed(boolean used){
        this.used = used;
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
