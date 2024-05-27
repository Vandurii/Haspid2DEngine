package main.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import main.components.SpriteRenderer;
import main.editor.EditorScene;
import main.renderer.RenderBatch;
import main.renderer.Renderer;
import main.util.Texture;
import org.joml.Vector4f;

import java.lang.management.*;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;
import org.lwjgl.system.libc.LibCStdlib;

import static main.Configuration.menuBarHeight;

public class ResourcesManager {
    private Renderer renderer;
    private EditorScene editorScene;

    private Vector4f colorRed;
    private Vector4f colorGreen;
    private Vector4f colorCyan;
    private Vector4f colorGrey;
    private Vector4f colorOrange;
    private Vector4f lightGreen;

    public ResourcesManager(EditorScene editorScene){
        this.editorScene = editorScene;
        this.renderer = Renderer.getInstance();
        this.colorGreen = new Vector4f(0f, 1f, 0f, 1f);
        this.colorRed = new Vector4f(1f, 0.5f, 0.5f, 1f);
        this.colorCyan = new Vector4f(0f, 0.9f, 0.9f, 1f);
        this.colorGrey = new Vector4f(0.7f, 0.7f, 0.7f, 1f);
        this.colorOrange = new Vector4f(0.9f, 0.7f, 0.1f, 1f);
        this.lightGreen = new Vector4f(0.6f, 0.85f, 0.5f, 1f);
    }

    public void Display(){
        ImGui.begin("Resources Manager");

        if(ImGui.collapsingHeader("System Resources")) {
            displayProcesorUsage();
            displayMemoryUsage();
        }

        if(ImGui.collapsingHeader("Scene")) {
            displaySceneObjects();
            displayRenders();
        }

        if(ImGui.collapsingHeader("Resources")) {
            displayAssets();
        }

        ImGui.end();
    }

    public void doSpacing(int count){
        String spacing = "";
        for(int i = 0; i < count; i++) {
           spacing += "\n";
        }
        ImGui.text(spacing);
    }

    public void displayAssets(){
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "Assets");

        List<RenderBatch> renderList = editorScene.getRenderer().getRenderBatchList();
        if(!renderList.isEmpty()){
            for(Texture texture: editorScene.getRenderer().getRenderBatchList().get(0).getTextureList()){
                ImGui.text("\t" + texture.getFilePath());
            }
        }
    }

    public void DisplayLineRenderer(){

    }

    public void displayMemoryUsage(){
        int gigaByte = 1_073_741_824;
        int megaByte = 1_048_576;
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        double used = (double)memoryMXBean.getHeapMemoryUsage().getUsed() / megaByte;
        double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() / megaByte;

        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "Heap Memory:");
        ImGui.sameLine();
        ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, String.format("%.0f MB", used));

        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "Commited Memory:");
        ImGui.sameLine();
        ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, String.format("%.0f MB", commited));

        doSpacing(1);
    }

    public void displayProcesorUsage(){
        ImGui.bullet();
        ImGui.sameLine();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "CPU");
        ImGui.sameLine();
        ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, String.format("%.1f", (osBean.getCpuLoad() * 100)));
        ImGui.sameLine();
        ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, "%%");

        doSpacing(1);
    }

    public void displayRenders(){
        doSpacing(1);
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "Renders");

        for(RenderBatch renderBatch: renderer.getRenderBatchList()){
            int i = 0;
            if(ImGui.beginCombo("##" + renderBatch, "show details")){
                for(SpriteRenderer sR: renderBatch.getSpriteToRender()){
                    i++;
                    if(sR == null){
                        ImGui.text("[null]");
                        continue;
                    }
                    ImGui.text(String.format("[%s] %s", i, sR.getParent().getName()));
                }
                ImGui.endCombo();
            }

            ImGui.text("\tFilled in:\t");
            ImGui.sameLine();
            ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, String.format("%.1f", (100.0 / ((double)renderBatch.getMaxBathSize() / renderBatch.getSpriteCount()))));
            ImGui.sameLine();
            ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, "%%");
            ImGui.text(String.format("\tIndex:\t\t %s", renderBatch.getzIndex()));
            ImGui.text(String.format("\tCapacity: \t %s", renderBatch.getMaxBathSize()));
            ImGui.text(String.format("\tLoaded:   \t %s", renderBatch.getSpriteCount()));
            if(!renderBatch.hasRoom()){
                ImGui.sameLine();
                ImGui.textColored(colorGreen.x, colorGreen.y, colorGreen.z, colorGreen.w, "Max");
            }
            doSpacing(1);
        }
    }

    public void displaySceneObjects(){
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(colorGrey.x, colorGrey.y, colorGrey.z, colorGrey.w, "Object in Scene");
        ImGui.sameLine();
        ImGui.textColored(lightGreen.x, lightGreen.y, lightGreen.z, lightGreen.w, String.format("[%s]", editorScene.getSceneObjectList().size()));
    }
}
