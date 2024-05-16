package main.components.physicsComponent;

import main.editor.JImGui;
import main.components.Component;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2d offset = new Vector2d();

    @Override
    public void dearGui(){
        JImGui.drawValue("offset", offset, this.hashCode() + "");
    }

    @Override
    public void update(float dt) {
    }

    public void setOffset(Vector2d offset){
        this.offset = offset;
    }

    public Vector2d getOffset(){
        return offset;
    }
}
