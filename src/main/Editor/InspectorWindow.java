package main.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import main.Helper;
import main.haspid.GameObject;
import main.physics.components.BoxCollider;
import main.physics.components.CircleCollider;
import main.physics.components.RigidBody;

import static main.Configuration.imGuiColor;

public class InspectorWindow {
    private MouseControls mouseControls;

    public InspectorWindow(MouseControls mouseControls){
        this.mouseControls = mouseControls;
    }

    public void display(){
        GameObject activeGameObj = mouseControls.getActiveGameObject();

        if(activeGameObj != null){
            ImGui.pushStyleColor(ImGuiCol.WindowBg, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
            ImGui.begin("Inspector");
            mouseControls.getActiveGameObject().dearGui();

            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("add Rigid Body") && Helper.isNull(activeGameObj.getComponent(RigidBody.class))) activeGameObj.addComponent(new RigidBody());
                if(ImGui.menuItem("add Circle Collider") && Helper.isNull(activeGameObj.getComponent(CircleCollider.class))) activeGameObj.addComponent(new CircleCollider());
                if(ImGui.menuItem("add Box Collider") && Helper.isNull(activeGameObj.getComponent(BoxCollider.class))) activeGameObj.addComponent(new BoxCollider());
                ImGui.endPopup();
            }

            ImGui.end();
            ImGui.popStyleColor(1);
        }
    }
}
