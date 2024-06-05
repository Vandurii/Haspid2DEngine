package main.components.physicsComponent;

import main.components.Component;
import main.editor.editorControl.EventController;
import main.haspid.Scene;
import main.haspid.Window;
import main.physics.BodyType;
import main.physics.Physics2D;
import main.renderer.Line2D;

import java.util.List;

public abstract class Collider extends Component {
    protected transient Scene scene;
    private transient Physics2D physics;

    protected transient BodyType bodyType;
    protected transient boolean resetFixtureNextFrame;

    @Override
    public void init(){
        this.bodyType = getParent().getComponent(RigidBody.class).getBodyType();
        this.physics = Window.getInstance().getCurrentScene().getPhysics();
        this.scene = Window.getInstance().getCurrentScene();
    }

    public abstract void drawNewLines();
    public abstract boolean resize();

    @Override
    public void update(float dt) {
        if (EventController.collider) {
            boolean resize = resize();
            if (getParent().isDirty() || resize) {
                // Don't handle it, if this it box collider with static body.
                if(!(this instanceof BoxCollider && bodyType == BodyType.Static)) {
                    removeOldLines();
                    drawNewLines();
                }
            }

            if (resetFixtureNextFrame) resetFixture();
        }
    }

    public void removeOldLines(){
        List<Line2D> lineList = getParent().getAllCompThisType(Line2D.class);

        for(Line2D line: lineList){
            line.markToRemove(true);
            scene.removeComponentSafe(getParent(), line);
        }
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
