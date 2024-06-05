package main.components.stateMachine;

import main.components.Component;
import main.components.SpriteRenderer;
import main.editor.JImGui;
import main.haspid.Writable;
import org.joml.Vector2d;

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

    public void rotateCornets(){
        for(Animation animation: animationList){
            for(Frame frame: animation.getFrameList()){
              Vector2d[] cords = frame.getSpriteRenderer().getSpriteCords();

              Vector2d a = new Vector2d(cords[0].x, cords[0].y);
              Vector2d b = new Vector2d(cords[1].x, cords[1].y);
              Vector2d c = new Vector2d(cords[2].x, cords[2].y);
              Vector2d d = new Vector2d(cords[3].x, cords[3].y);

              cords[0] = new Vector2d(d.x, d.y);
              cords[1] = new Vector2d(c.x, c.y);
              cords[2] = new Vector2d(b.x, b.y);
              cords[3] = new Vector2d(a.x, a.y);
            }
        }
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
        String title = (String) JImGui.drawValue("Default", defaultAnimationTitle);
        if(findAnimation(title) != null) {
            defaultAnimationTitle = title;
            currentAnimationTitle = title;
        }

        for(Animation animation: animationList){
            animation.setTitle((String)JImGui.drawValue("State:", animation.getTitle()));

            int index = 0;
            for(Frame frame: animation.getFrameList()){
                SpriteRenderer sprite = frame.getSpriteRenderer();
                int ID = (int) JImGui.drawValue("Slot", sprite.getTextureSlotInRender());
                sprite.setTextureSlotInRender(ID);
                frame.setFrameTime((float)JImGui.drawValue("Frame " + index++ + ": ", frame.getFrameTime()));
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
