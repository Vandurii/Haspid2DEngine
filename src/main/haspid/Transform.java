package main.haspid;

import main.components.Component;
import main.components.SpriteRenderer;
import org.joml.Vector2d;

public class Transform extends Component {
    private int zIndex;
    private Vector2d scale;
    private double rotation;
    private Vector2d position;

    public Transform(){
        this.position = new Vector2d();
        this.scale = new Vector2d();
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2d position){
        this.position = position;
        this.scale = new Vector2d();
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2d position, Vector2d scale){
        this.position = position;
        this.scale = scale;
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2d position, Vector2d scale, double rotation, int zIndex){
        this.rotation = rotation;
        this.position = position;
        this.scale = scale;
        this.zIndex = zIndex;
    }

    @Override
    public void update(float dt) {
        if(getParent() == null) throw new IllegalStateException("Parent Object is null.");
    }

    @Override
    public Transform copy(){
        Transform t = new Transform(new Vector2d(this.position), new Vector2d(this.scale), rotation, zIndex);
        t.setParent(getParent());

        return t;
    }

    public void copy(Transform to){
        to.position.set(new Vector2d(this.position));
        to.scale.set(new Vector2d(this.scale));
        to.setRotation(rotation);
        to.setZIndex(zIndex);
        to.setParent(getParent());
    }

    public void increaseZIndex(){
        setZIndex(getZIndex() + 1);
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Transform)) return false;
        Transform t = (Transform) o;

        return (this.scale.equals(t.getScale()) && this.position.equals(t.getPosition()) && this.rotation == t.rotation && this.zIndex == t.zIndex);
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = new Vector2d(position.x, position.y);
    }

    public void setPosition(double x, double y){
        this.position = new Vector2d(x, y);
    }

    public Vector2d getScale() {
        return scale;
    }

    public void setScale(Vector2d scale) {
        this.scale = new Vector2d(scale.x, scale.y);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public int getZIndex(){
        return zIndex;
    }

    public void setZIndex(int zIndex){
        if(getParent() != null && this.zIndex != zIndex) {
            this.zIndex = zIndex;
            getParent().getComponent(SpriteRenderer.class).markToRelocate();
        }
    }
}
