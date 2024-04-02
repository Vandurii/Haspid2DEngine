package main.components;

import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Vector4f color;

    public SpriteRenderer(Vector4f color){
        this.color = color;
    }

    @Override
    public void update(float dt) {
      //  System.out.println("i am up to date");
    }

    @Override
    public void start(){

    }

    public Vector4f getColor(){
        return color;
    }
}
