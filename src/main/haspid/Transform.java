package main.haspid;

import main.components.Component;
import main.components.SpriteRenderer;
import org.joml.Vector2f;

public class Transform extends Component {
    private Vector2f position;
    private Vector2f scale;
    private float rotation;
    private int zIndex;


    public Transform(){
        this.position = new Vector2f();
        this.scale = new Vector2f();
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2f position){
        this.position = position;
        this.scale = new Vector2f();
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
        this.rotation = 0;
        this.zIndex = 0;
    }

    public Transform(Vector2f position, Vector2f scale, float rotation, int zIndex){
        this.rotation = rotation;
        this.position = position;
        this.scale = scale;
        this.zIndex = zIndex;
    }

    @Override
    public void update(float dt) {

    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setPosition(float x, float y){
        this.position = new Vector2f(x, y);
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getZIndex(){
        return zIndex;
    }

    public void setZIndex(int zIndex){
        if(getParent() != null && this.zIndex != zIndex) {
            getParent().getComponent(SpriteRenderer.class).markToRelocate(true);
            getParent().getComponent(SpriteRenderer.class).markToRemove();
            this.zIndex = zIndex;
        }
    }

    public Transform copy(){
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale), rotation, zIndex);
    }

    public void copy(Transform to){
        to.position.set(new Vector2f(this.position));
        to.scale.set(new Vector2f(this.scale));
        to.setRotation(rotation);
        to.setZIndex(zIndex);
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Transform)) return false;
        Transform t = (Transform) o;

        return (this.scale.equals(t.getScale()) && this.position.equals(t.getPosition()) && this.rotation == t.rotation && this.zIndex == t.zIndex);
    }
}
