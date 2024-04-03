package main.components;

import main.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite {
    private Texture texture;
    private Vector2f[] texCords;
    private Vector4f color;
    private int spriteID;

    public Sprite(Vector4f color){
        this.color = color;
        this.texCords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Texture texture){
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Vector4f color, Vector2f[] texCords) {
        this.color = color;
        this.texCords = texCords;
    }

    public Sprite(Texture texture, Vector2f[] texCords){
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = texCords;
    }

    public Texture getTexture(){
        return  texture;
    }

    public Vector2f[] getSpriteCords(){
        return texCords;
    }

    public boolean hasTexture(){
        return texture != null;
    }

    public int getSpriteID() {
        return spriteID;
    }

    public void setSpriteID(int spriteID) {
        this.spriteID = spriteID;
    }

    public boolean isIDDefault() {
        return spriteID == 0;
    }

    public void setColor(Vector4f color){
        this.color = color;
    }

    public Vector4f getColor(){
        return color;
    }
}
