package main.components;

import main.Editor.JImGui;
import main.util.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component{
    private HashMap<StateTrigger, String> stateTransfer;
    private List<Animation> animationList;
    private transient Animation currentAnimation;

    public StateMachine(){
        this.animationList = new ArrayList<>();
        this.stateTransfer = new HashMap<>();
    }

    @Override
    public void update(float dt) {
        currentAnimation.update(dt);

        SpriteRenderer objectSpriteRender = getParent().getComponent(SpriteRenderer.class);

        SpriteRenderer nextSpriteRender = currentAnimation.getCurrentSpriteRender();
        Texture nextTexture = nextSpriteRender.getTexture();
        Vector2f[] nextTexCords = nextSpriteRender.getSpriteCords();

        if(objectSpriteRender != null && objectSpriteRender.getSpriteCords() != nextTexCords) {
            objectSpriteRender.setTexture(nextTexture);
            objectSpriteRender.setSpriteCords(nextTexCords);

            objectSpriteRender.markToRelocate();
        }
    }

    @Override
    public void dearGui(){
        for(Animation animation: animationList){
            animation.setTitle((String)JImGui.drawValue("State:", animation.getTitle()));

            int index = 0;
            for(Frame frame: animation.getFrameList()){
                frame.setFrameTime((float)JImGui.drawValue("Frame " + index++ + ": ", frame.getFrameTime()));
            }
        }
    }

    @Override
    public Component copy() {
        StateMachine stateMachine = new StateMachine();
        for(Animation animation: animationList){
            stateMachine.addState(animation.copy());
        }

        return stateMachine;
    }

    @Override
    public void start(){
        currentAnimation = animationList.get(0);
    }

    public void addState(Animation animation){
        animationList.add(animation);
    }

//    public void addStateTransfer(String from, String to, String onTrigger){
//        stateTransfer.put(new StateTrigger(from, onTrigger), to);
//    }
//
//    public void trigger(String trigger){
//        for(StateTrigger stateTrigger: stateTransfer.keySet()){
//            if(stateTrigger.state.equals(currentAnimation.getTitle()) && stateTrigger.trigger.equals(trigger)){
//                if(stateTransfer.get(stateTrigger) != null){
//                    int newStateIndex = animationList.indexOf(stateTransfer.get(stateTrigger));
//                    if(newStateIndex > -1) currentAnimation = animationList.get(newStateIndex);
//                }
//                return;
//            }
//        }
//        System.out.println("Unable to find trigger: " + trigger);
//    }

    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger(String state, String trigger){
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof StateTrigger stateTrigger)) return false;
            return this.state.equals(stateTrigger.state) && this.trigger.equals(stateTrigger.trigger);
        }

        @Override
        public int hashCode(){
            return Objects.hash(state, trigger);
        }
    }
}
