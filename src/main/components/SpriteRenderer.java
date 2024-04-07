package main.components;

import main.haspid.Transform;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Sprite sprite;
    private  transient Transform lastTransform;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
    }

    @Override
    public void update(float dt) {
        if(!lastTransform.equals(getParent().getTransform())){
            getParent().getTransform().copy(lastTransform);
            sprite.setDirty();
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
        sprite.setDirty();
    }

    public void setColor(Vector4f color){
        sprite.setColor(color);
    }
}
