package main.editor.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImString;
import main.components.SpriteRenderer;
import main.components.stateMachine.Animation;
import main.components.stateMachine.Frame;
import main.editor.EditorScene;
import main.editor.JImGui;
import main.util.AssetPool;
import main.util.SpriteConfig;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;

public class Creator {
    private EditorScene editorScene;

    private int index;
    private final double dt;
    private int miniImageSize;
    private boolean animation;
    private double timeTracker;
    private int spriteImageSize;
    private int currentSpriteIndex;
    private double defaultFrameValue;
    private SpriteConfig currentConfig;

    private String animationName;
    private double frameTime;
    private boolean doesLoop;
    private List<Frame> frameList;

    private List<String> errorList;
    private List<Animation> animationList;
    private List<FrameData> frameDataList;
    private List<SpriteConfig> configList;

    private String titleError = "The title field can't be empty.";
    private String frameError = "Frame time can't be 0.";
    private String frameListError = "Animation must have at least one frame.";

    public Creator(EditorScene editorScene){
        this.animationName = "";
        this.dt = 1d / 60d;
        this.spriteImageSize = 50;
        this.miniImageSize = 25;
        this.defaultFrameValue = 0.23;
        this.editorScene = editorScene;
        this.frameList = new ArrayList<>();
        this.errorList = new ArrayList<>();
        this.currentConfig = smallFormConfig;
        this.frameDataList = new ArrayList<>();
        this.animationList = new ArrayList<>();
        this.configList = Arrays.asList(smallFormConfig, bigFormConfig, turtleConfig, itemsConfig, decorationAndBlockConfig, pipesConfig);
    }

    public void display(){
        ImGui.begin("Creator");
        ImGui.beginTabBar("Main");

        createTab();
        animationTab();
        scaleAnimationTab();

        ImGui.endTabBar();
        ImGui.end();
    }

    public void createImage(boolean clickable, Frame ...frames){
        for(Frame f: frames){
            SpriteRenderer sprite = f.getSpriteRenderer();
            Vector2d[] cords = sprite.getSpriteCords();
            if(clickable) {
                ImGui.imageButton(sprite.getTexID(), spriteImageSize, spriteImageSize, (float) cords[2].x, (float) cords[0].y, (float) cords[0].x, (float) cords[2].y);
            }else{
                ImGui.image(sprite.getTexID(), spriteImageSize, spriteImageSize, (float) cords[2].x, (float) cords[0].y, (float) cords[0].x, (float) cords[2].y);
            }
            ImGui.sameLine();
        }
    }

    public void createImage(boolean clickable, SpriteRenderer spriteRenderer, int Size){
        Vector2d[] cords = spriteRenderer.getSpriteCords();
        if(clickable) {
            ImGui.imageButton(spriteRenderer.getTexID(), Size, Size, (float) cords[2].x, (float) cords[0].y, (float) cords[0].x, (float) cords[2].y);
        }else{
            ImGui.image(spriteRenderer.getTexID(), Size, Size, (float) cords[2].x, (float) cords[0].y, (float) cords[0].x, (float) cords[2].y);
        }
    }

    public void createAnimation(){
        //================
        // all frames area
        //===============
        ImGui.bulletText("Frame List:");

        for (int i = 0; i < frameList.size(); i++) {
            SpriteRenderer sprite = frameList.get(i).getSpriteRenderer();
            int texID = sprite.getTexID();
            Vector2d[] cords = sprite.getSpriteCords();

            ImGui.pushID(i);
            if (ImGui.imageButton(texID, (float) spriteImageSize, (float) spriteImageSize, (float) cords[3].x, (float) cords[3].y, (float) cords[1].x, (float) cords[1].y)) {
                frameList.remove(frameList.get(i));
                ImGui.popID();
                return;
            }
            ImGui.popID();
            if(enoughSpaceForNextButton((float)miniImageSize)) ImGui.sameLine();
        }

        doSpacing(1);

        //================
        // view area
        //===============
        ImGui.bulletText("View:");
        if(!frameList.isEmpty()) {
            if ((timeTracker -= dt) <= 0) {
                // increase index, start from begin if end
                currentSpriteIndex++;
                if (currentSpriteIndex >= frameList.size()) currentSpriteIndex = 0;

                // set time tracker to new sprite time
                timeTracker = frameList.get(currentSpriteIndex).getFrameTime();
            }

            if(currentSpriteIndex >= frameList.size()) currentSpriteIndex = 0;
            createImage(false, frameList.get(currentSpriteIndex));
        }

        doSpacing(2);

        //================
        // add frame area
        //===============
        ImGui.bulletText("Frame Settings:");
        SpriteSheet currentSpriteSheet = AssetPool.getSpriteSheet(currentConfig);

        frameTime = (float) JImGui.drawValue("  Frame Time: ", frameTime, this.hashCode() + "");

        if(currentSpriteSheet.getSize() <= index) index = 0;
        SpriteRenderer sprite = currentSpriteSheet.getSprite(index);
        createImage(false, sprite, spriteImageSize);

        if(ImGui.button("Set Default Time")){
            frameTime = defaultFrameValue;
        }

        ImGui.sameLine();

        if(ImGui.button("Add Frame")){
            if(frameTime == 0) {
                errorList.add(frameError);
            }else{
                errorList.remove(frameError);
                frameList.add(new Frame(sprite, frameTime));
            }
        }

        if(ImGui.beginCombo("##spriteSelector", currentConfig.name)){
            for(int i = 0; i < configList.size(); i++){
                if(ImGui.button(configList.get(i).name)){
                    currentConfig = configList.get(i);
                }
            }
            ImGui.endCombo();
        }

        for (int i = 0; i < currentSpriteSheet.getSize(); i++) {
            sprite = currentSpriteSheet.getSprite(i);

            int texID = sprite.getTexID();
            Vector2d[] cords = sprite.getSpriteCords();

            ImGui.pushID(i);
            if (ImGui.imageButton(texID, (float) miniImageSize, (float) miniImageSize, (float) cords[3].x, (float) cords[3].y, (float) cords[1].x, (float) cords[1].y)) {
                index = i;
            }
            ImGui.popID();
            if(enoughSpaceForNextButton((float)miniImageSize)) ImGui.sameLine();
        }

        doSpacing(5);

        //================
        // save area
        //===============
        ImGui.bulletText("Save Animation:");
        ImGui.text(" ");
        boolean imBoolean = doesLoop;
        ImGui.sameLine();
        if(ImGui.checkbox("Loop",imBoolean)) doesLoop = !doesLoop;

        animationName = (String) JImGui.drawValue("  Animaion Name: ", animationName, this.hashCode() + "");
        ImGui.text(" ");
        ImGui.sameLine();

        if(ImGui.button("Cancel")){
            reset();
        }

        ImGui.sameLine();
        if(ImGui.button("Save Animation")){
            if(animationName.isEmpty()){
                errorList.add(titleError);
            }else if(frameList.isEmpty()){
                errorList.add(frameListError);
            }else{
                animationList.add(new Animation(animationName, doesLoop, new ArrayList<>(frameList)));
                reset();
            }
        }

        displayErrors();
    }

    public void createTab(){
        if(ImGui.beginTabItem("Create")) {

            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("Create Animation")){
                    animation = true;
                }
                ImGui.endPopup();
            }

            if (animation) {
                createAnimation();
            }

            ImGui.endTabItem();
        }
    }

    public void animationTab(){
        if(ImGui.beginTabItem("Animation")){
            // update all animation i animation tab
            for(Animation animation: animationList) {
                String title = animation.getTitle();

                FrameData frameData = null;

                // check if list contain data from this animation
                boolean contain = false;
                for(FrameData data: frameDataList){
                    if(data.title.equals(title)){
                        contain = true;
                        frameData = data;
                    }
                }

                // if not contain than initialize it
                if(!contain){
                    frameData = new FrameData(title, 0, 0);
                    frameDataList.add(frameData);
                }

                // update time
                frameData.time -= dt;

                if (frameData.time <= 0) {
                    // increase index, start from begin if end
                    frameData.index++;
                    if (frameData.index >= animation.getFrameList().size()) frameData.index = 0;

                    // set time tracker to new sprite time
                    frameData.time = animation.getFrameList().get(frameData.index).getFrameTime();
                }

                createImage(false, animation.getFrameList().get(frameData.index));
            }
            ImGui.endTabItem();
        }
    }

    public void scaleAnimationTab(){
        if(ImGui.beginTabItem("Scale Animation")) {
            ImGui.endTabItem();
        }
    }

    public void reset(){
        index = 0;
        animationName = "";
        doesLoop = false;
        animation = false;
        frameList.clear();
        errorList.clear();
        currentSpriteIndex = 0;
    }

    public void displayErrors(){
        for(String error: errorList){
            ImGui.textColored(colorRedA.x, colorRedA.y, colorRedA.z, colorRedA.w, error);
        }
    }

    public void doSpacing(int count){
        for(int i = 0; i < count; i++){
            ImGui.newLine();
        }
    }

    public boolean enoughSpaceForNextButton(float buttonWidth){
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float window = windowPos.x + windowSize.x;


        ImVec2 lastButton = new ImVec2();
        ImGui.getItemRectMax(lastButton);
        float nextButton = lastButton.x + itemSpacing.x + buttonWidth;

        return nextButton < window;
    }

    public class FrameData{
        int index;
        double time;
        String title;

        public FrameData(String title, double time, int index){
            this.time = time;
            this.index = index;
            this.title = title;
        }
    }
}
