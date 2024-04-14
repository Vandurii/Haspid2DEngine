package main.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import main.haspid.Window;
import org.joml.Vector2f;

import static main.Configuration.*;

public class ViewPort {

    public static float viewPortStartFromX, viewPortStartFromY;
    public static float viewPortWidth, viewPortHeight;
    public static float windowStartFromX, windowStartFromY;

    public static void displayViewPort(){
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f);
        ImGui.begin("View Port", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.popStyleVar(1);

        Vector2f viewSize = calculateViewSize();
        Vector2f center = getStartPosition(viewSize);

        ImGui.setCursorPos(center.x, center.y);

        int textID = Window.getInstance().getFrameBuffer().getTextureID();
        ImGui.image(textID, viewSize.x, viewSize.y, 0, 1, 1, 0);

        ImGui.end();

    }

    public static Vector2f calculateViewSize(){
        ImVec2 windowSize = ImGui.getContentRegionAvail();

        float width = windowSize.x;
        float height = width / aspectRatio;

        if(height > windowSize.y){
            height = windowSize.y;
            width = height * aspectRatio;
        }

        viewPortWidth = width;
        viewPortHeight = height;

        return new Vector2f(width, height);
    }

    public static Vector2f getStartPosition(Vector2f viewSize){
        ImVec2 windowSize = ImGui.getContentRegionAvail();

        float startX = ((windowSize.x - viewSize.x) / 2f);
        float startY = ((windowSize.y - viewSize.y) / 2f);

        viewPortStartFromX = startX;
        viewPortStartFromY = startY;

        windowStartFromX = ImGui.getWindowPosX();
        windowStartFromY = ImGui.getWindowPosY();

        return  new Vector2f(startX , startY);
    }
}
