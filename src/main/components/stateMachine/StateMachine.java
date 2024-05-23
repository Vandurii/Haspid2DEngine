package main.components.stateMachine;

import main.components.Component;
import main.components.SpriteRenderer;
import main.editor.InactiveInEditor;
import main.editor.JImGui;
import main.util.Texture;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class StateMachine extends Component implements InactiveInEditor {
    private List<Animation> animationList;
    private String nextAnimation;
    private String defaultAnimation;
    private transient Animation currentAnimation;

    public StateMachine(String defaultAnimation){
        this.animationList = new ArrayList<>();
        this.defaultAnimation = defaultAnimation;
        this.nextAnimation = defaultAnimation;
    }

    @Override
    public void start(){
        currentAnimation = findAnimation(nextAnimation);
        updateTexture();
    }

    @Override
    public void update(float dt) {
        currentAnimation = findAnimation(nextAnimation);
        currentAnimation.update(dt);

        SpriteRenderer nextSpriteRender = currentAnimation.getCurrentSpriteRender();

        if(nextSpriteRender == null){
            currentAnimation = findAnimation(defaultAnimation);
            nextAnimation = defaultAnimation;
        }

        updateTexture();
    }

    public void updateTexture(){
        SpriteRenderer nextSpriteRender = currentAnimation.getCurrentSpriteRender();
        Texture nextTexture = nextSpriteRender.getTexture();
        Vector2d[] nextTexCords = nextSpriteRender.getSpriteCords();

        SpriteRenderer objectSpriteRender = getParent().getComponent(SpriteRenderer.class);
        objectSpriteRender.setTexture(nextTexture);
        objectSpriteRender.setSpriteCords(nextTexCords);
        objectSpriteRender.setDirty();
    }

    @Override
    public void dearGui(){
        for(Animation animation: animationList){
            animation.setTitle((String)JImGui.drawValue("State:", animation.getTitle(), this.hashCode() + ""));

            int index = 0;
            for(Frame frame: animation.getFrameList()){
                frame.setFrameTime((float)JImGui.drawValue("Frame " + index++ + ": ", frame.getFrameTime(), this.hashCode() + animation.getTitle()));
            }
        }
    }

    @Override
    public Component copy() {
        StateMachine stateMachine = new StateMachine(defaultAnimation);
        for(Animation animation: animationList){
            stateMachine.addState(animation.copy());
        }

        return stateMachine;
    }

    public void addState(Animation ...animations){
        for(Animation animation: animations){
            animationList.add(animation);
        }
    }

    public Animation findAnimation(String title){

        for(Animation animation: animationList){
            if(animation.getTitle().equals(title)) return animation;
        }

        return null;
    }

    public void switchAnimation(String title){
        this.nextAnimation = title;
    }

    public String getNextAnimationTitle(){
        return nextAnimation;
    }

    public String getDefaultAnimation() {
        return defaultAnimation;
    }

    public void setDefaultAnimation(String defaultAnimation) {
        this.defaultAnimation = defaultAnimation;
    }
}
