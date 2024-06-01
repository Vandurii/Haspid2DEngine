package main.components.stateMachine;

import main.components.Component;
import main.components.SpriteRenderer;
import main.editor.JImGui;
import main.haspid.Writable;
import main.renderer.RenderBatch;

import java.util.ArrayList;
import java.util.List;

public class StateMachine extends Component implements Writable {
    private String name;
    private String defaultAnimationTitle;
    private List<Animation> animationList;
    private transient String currentAnimationTitle;
    private transient Animation currentAnimation;

    public StateMachine(String name, String defaultAnimationTitle, List<Animation> animationList){
        this.name = name;
        this.animationList = animationList;
        this.defaultAnimationTitle = defaultAnimationTitle;
        this.currentAnimationTitle = defaultAnimationTitle;
        this.currentAnimation = findAnimation(defaultAnimationTitle);
    }

    @Override
    public void init(){
        // Set texture here because update loop doesn't work in edit mode.
        currentAnimationTitle = defaultAnimationTitle;
        currentAnimation = findAnimation(currentAnimationTitle);
        if(getParent() != null) {
            getParent().setSprite(currentAnimation.getCurrentFrame().getSpriteRenderer());
        }
    }

    @Override
    public void update(float dt) {
        // get current animation and update it
        currentAnimation = findAnimation(currentAnimationTitle);
        currentAnimation.update(dt);

        // set new sprite to the object
        getParent().setSprite(currentAnimation.getCurrentFrame().getSpriteRenderer());

        // if the animation shouldn't loop switch it to default after is used
        if(!currentAnimation.doesLoop() && currentAnimation.isUsed()){
            currentAnimation.setUsed(false);
            currentAnimationTitle = defaultAnimationTitle;
        }
    }

    public Animation findAnimation(String title){
        for(Animation animation: animationList){
            if(animation.getTitle().equals(title)) return animation;
        }

        return  null;
    }

    public void switchAnimation(String title){
        this.currentAnimationTitle = title;
    }

    @Override
    public Component copy() {
        List<Animation> animList = new ArrayList<>();
        for(Animation animation: animationList){
            animList.add(animation.copy());
        }

        return new StateMachine(name, defaultAnimationTitle, animationList);
    }

    @Override
    public void dearGui(){
        String title = (String) JImGui.drawValue("Default", defaultAnimationTitle, this.hashCode() + "");
        if(findAnimation(title) != null) {
            defaultAnimationTitle = title;
            currentAnimationTitle = title;
        }

        for(Animation animation: animationList){
            String hash = this.hashCode() + animation.getTitle();
            animation.setTitle((String)JImGui.drawValue("State:", animation.getTitle(), hash));

            int index = 0;
            for(Frame frame: animation.getFrameList()){
                hash += frame.hashCode();
                SpriteRenderer sprite = frame.getSpriteRenderer();
                int ID = (int) JImGui.drawValue("Slot", sprite.getSpriteID(), hash);
                sprite.setSpriteID(ID);
                frame.setFrameTime((float)JImGui.drawValue("Frame " + index++ + ": ", frame.getFrameTime(), hash));
            }
        }
    }

    public String getNextAnimationTitle(){
        return currentAnimationTitle;
    }

    @Override
    public String getName(){
        return  name;
    }
}
