package main.physics.components;

import main.Editor.JImGui;
import main.components.Component;
import main.haspid.Transform;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2f offset = new Vector2f();

    @Override
    public void dearGui(){
        super.dearGui();
        JImGui.drawValue("offset", offset);
    }

    @Override
    public void update(float dt) {
    }

    public void setOffset(Vector2f offset){
        this.offset = offset;
    }

    public Vector2f getOffset(){
        return offset;
    }
}
