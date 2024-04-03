package main.components;

import main.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{
    private Vector4f color;
    private Vector2f texCords;
    private Texture texture;
    private int renderID;

    public SpriteRenderer(Vector4f color){
        this.color = color;
    }

    public SpriteRenderer(Texture texture){
        this.color = new Vector4f(1, 1, 1, 1);
        this.texture = texture;
    }

    @Override
    public void update(float dt) {
      //  System.out.println("i am up to date");
    }

    @Override
    public void start(){}

    public Vector4f getColor(){
        return color;
    }

    public Vector2f[] getTextureCords(){
        Vector2f[] texCords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };

        return texCords;
    }

    public Texture getTexture(){
        return texture;
    }

    public boolean hasTexture(){
        return texture != null;
    }

    public int getRenderID() {
        return renderID;
    }

    public void setRenderID(int renderID) {
        this.renderID = renderID;
    }

    public boolean isIDDefault() {
        return renderID == 0;
    }
}
