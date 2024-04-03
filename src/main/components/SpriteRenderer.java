package main.components;

import main.haspid.Transform;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Transform lastTransform;
    private Sprite sprite;
    private boolean isDirty;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
    }

    @Override
    public void update(float dt) {
        if(!lastTransform.equals(getParent().getTransform())){
            getParent().getTransform().copy(lastTransform);
            isDirty = true;
        }
    }
    @Override
    public void start(){
        lastTransform = getParent().getTransform().copy();
    }

    public Sprite getSprite(){
        return sprite;
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color){
        if(!color.equals(this.sprite.getColor())) {
            sprite.setColor(color);
            isDirty = true;
        }
    }

    public boolean isDirty(){
        return isDirty;
    }

    public void setClean(){
        isDirty = false;
    }
}
