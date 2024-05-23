package main.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import main.haspid.GameObject;

import java.util.HashMap;
import java.util.List;

import static main.Configuration.*;
import static main.Configuration.imGuiTabActive;

public class SceneHierarchy {
    private EditorScene editorScene;
    private static String payloadDragDropType = "SceneHierarchy";
    private HashMap<Integer, Boolean> openNodeMap;

    public SceneHierarchy(EditorScene editorScene){
        this.editorScene = editorScene;
        openNodeMap = new HashMap<>();
    }

    public void display(){
        ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, imGuiFrameBackground.x, imGuiFrameBackground.y, imGuiFrameBackground.z, imGuiFrameBackground.w);
        ImGui.pushStyleColor(ImGuiCol.Header, imGuiHeader.x, imGuiHeader.y, imGuiHeader.z, imGuiHeader.w);
        ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, imGuiTabInactive.x, imGuiTabInactive.y, imGuiTabInactive.z, imGuiTabInactive.w);
        ImGui.pushStyleColor(ImGuiCol.TabActive, imGuiTabActive.x, imGuiTabActive.y, imGuiTabActive.z, imGuiTabActive.w);
        ImGui.begin("Scene Hierarchy");
        List<GameObject> gameObjectList = editorScene.getSceneObjectList();

        for(int i = 0; i < gameObjectList.size(); i++){
            GameObject gameObject = gameObjectList.get(i);
            if(!gameObject.isSerializable()) continue;

            boolean isTreeNodeOpen = createTreeNode(gameObject, i);

            if(isTreeNodeOpen) ImGui.treePop();
        }
        ImGui.end();
        ImGui.popStyleColor(5);
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
