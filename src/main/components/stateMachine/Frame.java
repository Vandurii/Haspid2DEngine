package main.components.stateMachine;

import main.components.SpriteRenderer;

public class Frame {
    private double frameTime;
    private SpriteRenderer spriteRenderer;

    public Frame(SpriteRenderer spriteRenderer, double frameTime){
        this.frameTime = frameTime;
        this.spriteRenderer = spriteRenderer;
    }

    public Frame copy(){
        return new Frame(spriteRenderer.copy(), frameTime);
    }

    public double getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }

    public SpriteRenderer getSpriteRenderer() {
        return spriteRenderer;
    }
}
