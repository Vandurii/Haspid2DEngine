package main.editor.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import main.haspid.Window;

import static main.Configuration.*;

public class ViewPort {

    private static ViewPort instance;
    private static float viewPortStartFromX, viewPortStartFromY;
    private static float viewPortWidth, viewPortHeight;
    private static float windowStartFromX, windowStartFromY;
    private static float windowWidth, windowHeight;

    private ViewPort(){
    };

    public static ViewPort getInstance(){
        if(instance == null) instance = new ViewPort();

        return instance;
    }

    public static void resetInstance(){
        instance = new ViewPort();
    }

    public void display() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f);
        ImGui.begin("View Port", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        update();

        ImGui.setCursorPos(viewPortStartFromX, viewPortStartFromY);

        int textID = Window.getInstance().getFrameBuffer().getTextureID();
        ImGui.image(textID, viewPortWidth, viewPortHeight, 0, 1, 1, 0);

        ImGui.popStyleVar(1);
        ImGui.end();
    }

    private void update() {
        ImVec2 windowSize = ImGui.getContentRegionAvail();

        float width = windowSize.x;
        float height = width / aspectRatio;

        if (height > windowSize.y) {
            height = windowSize.y;
            width = height * aspectRatio;
        }

        viewPortWidth = width;
        viewPortHeight = height;

        viewPortStartFromX = ((windowSize.x - width) / 2f);
        viewPortStartFromY = ((windowSize.y - height) / 2f);

        windowStartFromX = ImGui.getWindowPosX();
        windowStartFromY = ImGui.getWindowPosY();

        windowWidth = windowSize.x;
        windowHeight = windowSize.y;

        // update scale for Selector
        selectorScale = 1440 / viewPortWidth;//todo
    }

    public float getViewPortStartFromX() {
        return viewPortStartFromX;
    }

    public float getViewPortStartFromY() {
        return viewPortStartFromY;
    }

    public float getViewPortWidth() {
        return viewPortWidth;
    }

    public float getViewPortHeight() {
        return viewPortHeight;
    }

    public float getWindowStartFromX() {
        return windowStartFromX;
    }

    public float getWindowStartFromY() {
        return windowStartFromY;
    }

    public float getWindowWidth() {
        return windowWidth;
    }

    public float getWindowHeight() {
        return windowHeight;
    }
}
