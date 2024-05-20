package main.components.stateMachine;

import main.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private String title;
    private boolean doesLoop;
    private transient double timeTracker;
    private transient int currentSpriteRender;
    private List<Frame> frameList;

    public Animation(String title, boolean doesLoop){
        this.title = title;
        this.doesLoop = doesLoop;
        this.frameList = new ArrayList<>();
    }

    public void update(float dt){
        timeTracker -= dt;
        if(timeTracker <= 0){
            increaseCurrentSpriteIfAvailable();
            if(frameList.get(currentSpriteRender).isUsed() && !doesLoop){
                frameList.get(currentSpriteRender).setUsed(false);
                currentSpriteRender = -1;
                return;
            }
            timeTracker = frameList.get(currentSpriteRender).getFrameTime();
        }
    }

    public void increaseCurrentSpriteIfAvailable(){
        if(currentSpriteRender + 1 < frameList.size()){
            currentSpriteRender++;
        }else{
            currentSpriteRender = 0;
        }
    }

    public void addFrame(SpriteRenderer spriteRenderer, double frameTime){
        frameList.add(new Frame(spriteRenderer, frameTime));
    }

    public Animation copy(){
        Animation animation = new Animation(title, doesLoop);
        animation.setLoop(doesLoop);
        animation.setTimeTracker(timeTracker);
        animation.setCurrentSpriteRender(currentSpriteRender);
        for(Frame f: frameList){
            Frame fCopy = f.copy();
            animation.addFrame(fCopy.getSpriteRenderer(), fCopy.getFrameTime());
        }

        return animation;
    }

    public void setLoop(boolean doesLoop){
        this.doesLoop = doesLoop;
    }

    public SpriteRenderer getCurrentSpriteRender(){
        if(currentSpriteRender == -1){
            currentSpriteRender = 0;
            return null;
        }

        return frameList.get(currentSpriteRender).getSpriteRenderer();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTimeTracker(){
        return timeTracker;
    }

    public void setDoesLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void setTimeTracker(double timeTracker) {
        this.timeTracker = timeTracker;
    }

    public void setCurrentSpriteRender(int currentSpriteRender) {
        this.currentSpriteRender = currentSpriteRender;
    }

    public void setFrameList(List<Frame> frameList) {
        this.frameList = frameList;
    }

    public List<Frame> getFrameList() {
        return frameList;
    }
}
