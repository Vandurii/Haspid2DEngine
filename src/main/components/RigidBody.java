package main.components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RigidBody extends Component{

    private int colliderType;
    private float friction;
    private Vector3f velocity;
    private Vector4f tmp;

    public RigidBody(int colliderType, float friction, Vector3f velocity, Vector4f tmp){
        this.colliderType = colliderType;
        this.friction = friction;
        this.velocity = velocity;
        this.tmp = tmp;
    }

    @Override
    public void update(float dt) {

    }
}
