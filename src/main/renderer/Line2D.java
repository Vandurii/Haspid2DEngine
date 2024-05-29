package main.renderer;

import main.components.Component;

import main.haspid.GameObject;
import org.joml.Vector2d;
import org.joml.Vector3f;

public class Line2D extends Component{
    private Vector2d to;
    private Vector2d from;
    private Vector3f color;
    private static int count;
    private transient int ID;
    private transient boolean dirty;
    private transient boolean remove;
    private transient boolean after;

    public Line2D(Vector2d from, Vector2d to, Vector3f color) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.dirty = true;
        this.ID = count;
        count++;
    }

    @Override
    public void update(float dt) {
        if(getParent() == null) throw new IllegalStateException("parent can't be null");

        if(getParent().isDirty()){
            dirty = true;
        }
    }

//    @Override
//    public Line2D copy(){
//        return new Line2D(new Vector2d(from.x, from.y), new Vector2d(to.x, to.y), new Vector3f(color.x, color.y, color.z));
//    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Line2D line)) return false;

        return this.to.x == line.getTo().x && this.to.y == line.getTo().y && this.from.x == line.getFrom().x && this.from.y == line.getFrom().y &&
                this.color.x == line.getColor().x && this.color.y == line.getColor().y && this.color.z == line.getColor().z;
    }

    @Override
    public int hashCode(){
        return to.hashCode() + from.hashCode() + color.hashCode();
    }

    public void markToRemove(boolean remove){
        this.remove = remove;
    }

    public boolean isDirty(){
        return dirty;
    }

    public boolean isMarkedToRemove(){
        return remove;
    }

    public Vector2d getFrom() {
        return from;
    }

    public Vector2d getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public int getID() {
        return ID;
    }

    public void setAfter(boolean after){
        this.after = after;
    }

    public boolean isAfter(){
        return  after;
    }
}
