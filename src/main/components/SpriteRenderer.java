package main.components;

public class SpriteRenderer extends Component{
    private Sprite sprite;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
    }

    @Override
    public void update(float dt) {}

    @Override
    public void start(){}

    public Sprite getSprite(){
        return sprite;
    }
}
