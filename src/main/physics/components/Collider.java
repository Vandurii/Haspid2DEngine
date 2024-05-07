package main.physics.components;

import main.components.Component;
import main.haspid.Transform;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2f offset = new Vector2f(16, 16);

    @Override
    public void update(float dt) {
//        Vector2f scale = getParent().getTransform().getScale();
//        offset = new Vector2f(scale.x , scale.y);
    }

    public Vector2f getOffset(){
        return offset;
    }
}
