package main.components.stateMachine;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private String title;
    private boolean doesLoop;
    private List<Frame> frameList;
    private transient boolean used;
    private transient double timeTracker;
    private transient int currentSpriteIndex;

    public Animation(String title, boolean doesLoop, List<Frame> frameList){
        this.title = title;
        this.doesLoop = doesLoop;
        this.frameList = frameList;
    }

    public void update(float dt){
        if((timeTracker -= dt) <= 0){
            // increase index, start from begin if end
            currentSpriteIndex++;
            if(currentSpriteIndex >= frameList.size()) currentSpriteIndex = 0;

            // set time tracker to new sprite time
            timeTracker = frameList.get(currentSpriteIndex).getFrameTime();

            // if sprite doesn't loop changed set used to true so that state machine change his sprite to default
            if(!doesLoop){
                used = true;
            }
        }
    }

    public Animation copy(){
        List<Frame> list = new ArrayList<>();
        for(Frame f: frameList){
            Frame fCopy = f.copy();
            list.add(new Frame(fCopy.getSpriteRenderer(), fCopy.getFrameTime()));
        }

        return new Animation(title, doesLoop, list);
    }

    public boolean doesLoop(){
        return doesLoop;
    }

    public boolean isUsed(){
        return used;
    }

    public Frame getCurrentFrame(){
        return frameList.get(currentSpriteIndex);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Frame> getFrameList() {
        return frameList;
    }

    public void setUsed(boolean used){
        this.used = used;
    }
}
