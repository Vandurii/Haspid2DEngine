package main.components;

import main.haspid.Transform;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Sprite sprite;
    private transient boolean isDirty;
    private  transient Transform lastTransform;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        isDirty = true;
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

    @Override
    public void dearGui(){
        isDirty = sprite.dearGui();
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

    public void setDirty(){
        isDirty = true;
    }
}
