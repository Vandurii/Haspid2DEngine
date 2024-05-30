package main.components.physicsComponent;

import main.editor.JImGui;
import main.components.Component;
import main.haspid.Scene;
import main.haspid.Window;
import main.physics.Physics2D;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class Collider extends Component {
    private Vector2d offset = new Vector2d();
    protected transient boolean resetFixtureNextFrame;
    private transient Physics2D physics;
    protected transient Scene scene;

    @Override
    public void init(){
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
        this.scene = Window.getInstance().getCurrentScene();
    }

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

    public void resetFixture(){
        if(physics.isLocked()){
            resetFixtureNextFrame = true;
        }else{
            resetFixtureNextFrame = false;
            RigidBody rigidBody = getParent().getComponent(RigidBody.class);
            if(rigidBody != null) physics.resetCollider(rigidBody, this);
        }
    }
}
