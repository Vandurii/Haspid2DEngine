package main.editor;

import imgui.ImGui;
import main.editor.editorControl.MouseControls;
import main.haspid.GameObject;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.CircleCollider;
import main.components.physicsComponent.RigidBody;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;

public class InspectorWindow {

    public void display(){
        List<GameObject> activeGameObjectList = MouseControls.getAllActiveObjects();
        if(activeGameObjectList.size() != 1) return;
        GameObject activeGameObj = activeGameObjectList.get(0);

        if(activeGameObj != null){
            ImGui.begin("Inspector");
            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("add Rigid Body") && activeGameObj.getComponent(RigidBody.class) == null) activeGameObj.addComponent(new RigidBody());
                if(ImGui.menuItem("add Circle Collider") && activeGameObj.getComponent(CircleCollider.class) == null) activeGameObj.addComponent(new CircleCollider(0));
                if(ImGui.menuItem("add Box Collider") && activeGameObj.getComponent(BoxCollider.class) == null) activeGameObj.addComponent(new BoxCollider(new Vector2d(objectHalfSize)));
                ImGui.endPopup();
            }

            activeGameObj.updateDearGui();
            ImGui.end();
        }
    }
}
