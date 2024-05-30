package main.components.stateMachine;

import main.components.Component;
import main.editor.JImGui;

import java.util.ArrayList;
import java.util.List;

public class StateMachine extends Component  {
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
        // get current animation and ubpdate it
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
            animation.setTitle((String)JImGui.drawValue("State:", animation.getTitle(), this.hashCode() + ""));

            int index = 0;
            for(Frame frame: animation.getFrameList()){
                frame.setFrameTime((float)JImGui.drawValue("Frame " + index++ + ": ", frame.getFrameTime(), this.hashCode() + animation.getTitle()));
            }
        }
    }

    public String getNextAnimationTitle(){
        return currentAnimationTitle;
    }

    public String getName(){
        return  name;
    }
}
