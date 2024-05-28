package main.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import main.components.SpriteRenderer;
import main.editor.EditorScene;
import main.haspid.GameObject;
import main.renderer.*;
import main.util.Texture;
import org.joml.Vector4f;

import java.lang.management.*;
import java.util.HashMap;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;

import static main.Configuration.*;

public class ResourcesManager {
    private Renderer renderer;
    private EditorScene editorScene;

    private String payloadDragDropType = "SceneHierarchy";
    private HashMap<Integer, Boolean> openNodeMap;

    public ResourcesManager(EditorScene editorScene){
        this.editorScene = editorScene;
        this.openNodeMap = new HashMap<>();
        this.renderer = Renderer.getInstance();
    }

    public void Display(){
        ImGui.begin("Resources Manager");

        if(ImGui.collapsingHeader("System Resources")) {
            displayCPUUsage();
            displayMemoryUsage();
        }

        int count = editorScene.getSceneObjectList().size();
        if(ImGui.collapsingHeader("Objects " + count)){
            displaySceneObjects();
        }

        if(ImGui.collapsingHeader("Renderers")) {
            displayRenders();
            displayStaticLines();
            displayDynamicLines();
        }

        if(ImGui.collapsingHeader("Resources")) {
            displayAssets();
        }

        ImGui.end();
    }

    public void displayCPUUsage(){
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        displayParamText("CPU:", String.format("%.1f", (osBean.getCpuLoad() * 100)), colorGreyA, colorLightGreenA);
        ImGui.sameLine();
        ImGui.textColored(colorLightGreenA.x, colorLightGreenA.y, colorLightGreenA.z, colorLightGreenA.w, "%%");
        doSpacing(1);
    }

    public void displayMemoryUsage(){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        int megaByte = 1_048_576;
        double used = (double)memoryMXBean.getHeapMemoryUsage().getUsed() / megaByte;
        double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() / megaByte;

        displayParamText("Heap Memory:", String.format("%.0f MB", used), colorGreyA, colorLightGreenA);
        displayParamText("Commited Memory:", String.format("%.0f MB", commited), colorGreyA, colorLightGreenA);
        doSpacing(1);
    }

    public void displaySceneObjects(){
        List<GameObject> gameObjectList = editorScene.getSceneObjectList();
        displayParamText("Object in Scene:", String.format("[%s]", editorScene.getSceneObjectList().size()), colorGreyA, colorLightGreenA);

        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject gameObject = gameObjectList.get(i);
            //if (!gameObject.isSerializable()) continue;

            boolean isTreeNodeOpen = createTreeNode(gameObject, i);

            if (isTreeNodeOpen) ImGui.treePop();
        }
    }

    public void displayAssets(){
        displayBulletTitle("Loaded Assets: ", colorGreyA);
        List<RenderBatch> renderList = editorScene.getRenderer().getRenderBatchList();

        if(!renderList.isEmpty()){
            for(Texture texture: renderList.get(0).getTextureList()){
                ImGui.text("\t" + texture.getFilePath());
            }
        }
    }

    public void displayRenders(){
        List<RenderBatch> renderBatchList = renderer.getRenderBatchList();

        for(int i = 0; i < renderBatchList.size(); i++){
            RenderBatch renderBatch = renderBatchList.get(i);
            displayBulletTitle("Renderer:" + renderBatch.getzIndex(), colorGreyA);
            if(ImGui.beginCombo("##" + renderBatch, "show details")){
                for(SpriteRenderer spriteRender: renderBatch.getSpriteToRender()){
                    if(spriteRender == null){
                        ImGui.text("[null]");
                        continue;
                    }
                    ImGui.text(String.format("[%s] %s", i, spriteRender.getParent().getName()));
                }
                ImGui.endCombo();
            }

            displayRenderInfo(renderBatch.getzIndex(), renderBatch.getMaxBathSize(), renderBatch.getSpriteCount(), renderBatch.hasRoom());
            doSpacing(1);
        }
    }

    public void displayStaticLines(){
        List<StaticLayer> staticLayerList = DebugDraw.getStaticLayerList();

        for(StaticLayer staticLayer: staticLayerList){
            displayBulletTitle(staticLayer.getID(), colorGreyA);

            if(ImGui.beginCombo("##" + staticLayer, "show details")){
                for(int i = 0; i < staticLayer.getLine().size(); i++){
                    Line2D line = staticLayer.getLine().get(i);
                    ImGui.text(String.format("[%s] X:%.1f Y:%.1f - X:%.1f Y:%.1f", i, line.getFrom().x, line.getFrom().y, line.getTo().x, line.getTo().y));
                }
                ImGui.endCombo();
            }

            ImGui.text(String.format("\tIndex:\t\t %s", staticLayer.getzIndex()));
            ImGui.text(String.format("\tCount:\t\t %s", staticLayer.getLine().size()));
            doSpacing(1);
        }
    }

    public void displayDynamicLines(){
        List<DynamicLayer> dynamicLayerList = DebugDraw.getDynamicLayerList();

        for(DynamicLayer dynamicLayer: dynamicLayerList){
            displayBulletTitle(dynamicLayer.getID(), colorGreyA);
            if(ImGui.beginCombo("##" + dynamicLayer, "show details")){
                for(int i = 0; i < dynamicLayer.getLineListToRender().length; i++){
                    Line2D line = dynamicLayer.getLineListToRender()[i];
                    if(line == null){
                        ImGui.text("[null]");
                        continue;
                    }
                    ImGui.text(String.format("[%s] X:%.1f Y:%.1f - X:%.1f Y:%.1f", i, line.getFrom().x, line.getFrom().y, line.getTo().x, line.getTo().y));
                }
                ImGui.endCombo();
            }

            displayRenderInfo(dynamicLayer.getzIndex(), dynamicLayer.getMaxBathSize(), dynamicLayer.getLineCount(), dynamicLayer.hasRoom());
            doSpacing(1);
        }
    }

    public void displayParamText(String firstText, String secondText, Vector4f firstColor, Vector4f secondColor){
        displayBulletTitle(firstText, firstColor);
        ImGui.sameLine();
        ImGui.textColored(secondColor.x, secondColor.y, secondColor.z, secondColor.w, secondText);
    }

    public void displayBulletTitle(String title, Vector4f titleColor){
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.textColored(titleColor.x, titleColor.y, titleColor.z, titleColor.w, title);
    }

    public void displayRenderInfo(int zIndex, int maxSize, int count, boolean hasRoom){
        ImGui.text("\tFilled in:\t");
        ImGui.sameLine();
        ImGui.textColored(colorLightGreenA.x, colorLightGreenA.y, colorLightGreenA.z, colorLightGreenA.w, String.format("%.1f", (100.0 / ((double)maxSize / count))));
        ImGui.sameLine();
        ImGui.textColored(colorLightGreenA.x, colorLightGreenA.y, colorLightGreenA.z, colorLightGreenA.w, "%%");
        ImGui.text(String.format("\tIndex:\t\t %s", zIndex));
        ImGui.text(String.format("\tCapacity: \t %s", maxSize));
        ImGui.text(String.format("\tLoaded:   \t %s", count));
        if(!hasRoom){
            ImGui.sameLine();
            ImGui.textColored(colorGreenA.x, colorGreenA.y, colorGreenA.z, colorGreenA.w, "Max");
        }
    }

    public void doSpacing(int count){
        String spacing = "";
        for(int i = 0; i < count; i++) {
            spacing += "\n";
        }
        ImGui.text(spacing);
    }

    public boolean createTreeNode(GameObject gameObject, int index){
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(gameObject.getName(),  ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow |
                ImGuiTreeNodeFlags.SpanAvailWidth, gameObject.getName());
        ImGui.popID();

        if(ImGui.beginDragDropSource()){
            ImGui.setDragDropPayload(payloadDragDropType, gameObject);
            ImGui.text(gameObject.getName());
            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()){
            Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
            if(payloadObj != null) System.out.println("Accepted: " + ((GameObject) payloadObj).getName());
            ImGui.endDragDropTarget();
        }

        resolveIfActive(gameObject, index, treeNodeOpen);

        return treeNodeOpen;
    }

    public void resolveIfActive(GameObject gameObject, int index, boolean open){
        if(open){
            editorScene.getMouseControls().setObjectActive(gameObject);
            openNodeMap.put(index, true);
        }else if(openNodeMap.get(index) != null && openNodeMap.get(index)){
            editorScene.getMouseControls().unselectActiveObject(gameObject);
            openNodeMap.put(index, false);
            System.out.println("clear: "  + index);
        }
    }
}
