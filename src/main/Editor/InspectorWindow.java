package main.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import main.Helper;
import main.haspid.GameObject;
import main.physics.components.BoxCollider;
import main.physics.components.CircleCollider;
import main.physics.components.RigidBody;
import org.joml.Vector2f;

import java.util.List;

import static main.Configuration.*;

public class InspectorWindow {
    private MouseControls mouseControls;

    public InspectorWindow(MouseControls mouseControls){
        this.mouseControls = mouseControls;
    }

    public void display(){
        List<GameObject> activeGameObjectList = mouseControls.getAllActiveObjects();
        if(activeGameObjectList.size() != 1) return;
        GameObject activeGameObj = activeGameObjectList.get(0);

        if(activeGameObj != null){
            ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, imGuiFrameBackground.x, imGuiFrameBackground.y, imGuiFrameBackground.z, imGuiFrameBackground.w);
            ImGui.pushStyleColor(ImGuiCol.Header, imGuiHeader.x, imGuiHeader.y, imGuiHeader.z, imGuiHeader.w);
            ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, imGuiTabInactive.x, imGuiTabInactive.y, imGuiTabInactive.z, imGuiTabInactive.w);
            ImGui.pushStyleColor(ImGuiCol.TabActive, imGuiTabActive.x, imGuiTabActive.y, imGuiTabActive.z, imGuiTabActive.w);
            ImGui.begin("Inspector");

            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("add Rigid Body") && Helper.isNull(activeGameObj.getComponent(RigidBody.class))) activeGameObj.addComponent(new RigidBody());
                if(ImGui.menuItem("add Circle Collider") && Helper.isNull(activeGameObj.getComponent(CircleCollider.class))) activeGameObj.addComponent(new CircleCollider(0));
                if(ImGui.menuItem("add Box Collider") && Helper.isNull(activeGameObj.getComponent(BoxCollider.class))) activeGameObj.addComponent(new BoxCollider(new Vector2f(objectSize)));
                ImGui.endPopup();
            }

            activeGameObj.dearGui();

            ImGui.end();
            ImGui.popStyleColor(5);
        }
    }
}
