package main.components.physicsComponent;

import main.editor.JImGui;
import main.components.Component;
import main.haspid.Scene;
import main.haspid.Window;
import main.physics.Physics2D;
import main.renderer.Line2D;
import org.joml.Vector2d;

import java.util.List;

public abstract class Collider extends Component {
    protected transient Scene scene;
    private transient Physics2D physics;
    private Vector2d offset = new Vector2d();
    protected transient boolean resetFixtureNextFrame;

    @Override
    public void init(){
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
        this.scene = Window.getInstance().getCurrentScene();
    }

    public abstract void updateColliderLines();
    public abstract boolean resize();

    @Override
    public void dearGui(){
        JImGui.drawValue("offset", offset, this.hashCode() + "");
    }

    @Override
    public void update(float dt) {
        if(getParent().isDirty() || resize()) {
            removeOldLines();
        }

        if(resetFixtureNextFrame) resetFixture();
    }

    public void removeOldLines(){
        List<Line2D> lineList = getParent().getAllCompThisType(Line2D.class);

        for(Line2D line: lineList){
            line.markToRemove(true);
            scene.removeComponentSafe(getParent(), line);
        }

        updateColliderLines();
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

    public void setOffset(Vector2d offset){
        this.offset = offset;
    }

    public Vector2d getOffset(){
        return offset;
    }
}
