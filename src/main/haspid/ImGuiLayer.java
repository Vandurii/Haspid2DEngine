package main.haspid;

import imgui.*;
import imgui.app.Configuration;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;

import static main.Configuration.*;
import static main.Configuration.imGuiColor;

public class ImGuiLayer {
    private long glfwWindow;
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    public ImGuiLayer(long glfwWindow){
        this.glfwWindow = glfwWindow;
    }

    public void init(final Configuration config) {
        initImGui(config);
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init("#version 330 core");
    }

    private void initImGui(final Configuration config) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);;
    }

    public void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        setupDockSpace();

        ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, imGuiFrameBackground.x, imGuiFrameBackground.y, imGuiFrameBackground.z, imGuiFrameBackground.w);
        ImGui.pushStyleColor(ImGuiCol.Header, imGuiHeader.x, imGuiHeader.y, imGuiHeader.z, imGuiHeader.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, imGuiHeaderHov.x, imGuiHeaderHov.y, imGuiHeaderHov.z, imGuiHeaderHov.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, imGuiHeaderActive.x, imGuiHeaderActive.y, imGuiHeaderActive.z, imGuiHeaderActive.w);
        ImGui.pushStyleColor(ImGuiCol.TabActive, imGuiTabActive.x, imGuiTabActive.y, imGuiTabActive.z, imGuiTabActive.w);
        ImGui.pushStyleColor(ImGuiCol.TabHovered, imGuiTabHovColor.x, imGuiTabHovColor.y, imGuiTabHovColor.z, imGuiTabHovColor.w);
        ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, imGuiTabUnfocusedActive.x, imGuiTabUnfocusedActive.y, imGuiTabUnfocusedActive.z, imGuiTabUnfocusedActive.w);
        ImGui.pushStyleColor(ImGuiCol.Tab, imGuiTabColor.x, imGuiTabColor.y, imGuiTabColor.z, imGuiTabColor.w);
        ImGui.pushStyleColor(ImGuiCol.Button, imGuiButtonColor.x, imGuiButtonColor.y, imGuiButtonColor.z, imGuiButtonColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, imGuiButtonHovColor.x, imGuiButtonHovColor.y, imGuiButtonHovColor.z, imGuiButtonHovColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, imGuiButtonActiveColor.x, imGuiButtonActiveColor.y, imGuiButtonActiveColor.z, imGuiButtonActiveColor.w);
    }

    private void setupDockSpace(){
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGui.setNextWindowPos(0f, 0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(windowWidth, windowHeight);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f);
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("DockSpace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(3);

        ImGui.dockSpace(ImGui.getID("DockSpace"));
        ImGui.end();
    }

    public void endFrame() {
        ImGui.popStyleColor(12);
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        disposeImGui();
    }

    private void disposeImGui() {
        ImGui.destroyContext();
    }
}
