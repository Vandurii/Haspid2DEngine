package main.physics.components;

import main.components.Component;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2f offset = new Vector2f();

    @Override
    public void update(float dt) {
    }

    public Vector2f getOffset(){
        return offset;
    }
}
