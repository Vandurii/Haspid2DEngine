package main.components;

import main.haspid.Transform;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Sprite sprite;
    private float rotation;
    private transient boolean remove;
    private transient Transform lastTransform;
    private transient boolean isDirty;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    public SpriteRenderer(Sprite sprite, float rotation){
        this.sprite = sprite;
        this.rotation = rotation;
        this.isDirty = true;
    }

    @Override
    public void update(float dt) {
        if(sprite.isDirty()){
            isDirty = true;
            sprite.setClean();
        }

        if(!lastTransform.equals(getParent().getTransform())){
            getParent().getTransform().copy(lastTransform);
            isDirty = true;
        }
    }
    @Override
    public void start(){
        lastTransform = getParent().getTransform().copy();
    }

    @Override
    public void dearGui(){
       sprite.dearGui();
    }

    public Sprite getSprite(){
        return sprite;
    }

    public boolean hasSprite(){
        return  sprite != null;
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        isDirty = true;
    }

    public boolean isDirty(){
        return isDirty;
    }

    public void setClean(){
        isDirty = false;
    }

    public void setDirty(){
        isDirty = true;
    }

    public void destroySprite(){
        sprite = null;
    }

    public void markToRemove(){
        remove = true;
    }

    public void unmarkToRemove(){
        remove = false;
    }

    public boolean isMarkedToRemove(){
        return remove;
    }

    public void setColor(Vector4f color){
        sprite.setColor(color);
    }

    public float getRotation() {
        return rotation;
    }

    public void setIsRotated(float rotation) {
        this.rotation = rotation;
    }
}
