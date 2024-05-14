package main.components.physicsComponent;

import main.editor.JImGui;
import main.components.Component;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2f offset = new Vector2f();

    @Override
    public void dearGui(){
        JImGui.drawValue("offset", offset, this.hashCode() + "");
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
