package main.components;

import imgui.ImGui;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.marioImagePath;

public class Sprite {
    private Texture texture;
    private Vector2f[] texCords;
    private Vector4f color;
    private int spriteID;
    private transient boolean isDirty;
    private float width, height;

    public Sprite(Vector4f color){
        this.isDirty = true;
        this.color = color;
        this.texCords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Texture texture){
        this.isDirty = true;
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
        this.isDirty = true;
        this.color = color;
        this.texCords = texCords;
    }

    public Sprite(Texture texture, float width, float height, Vector2f[] texCords){
        this.width = width;
        this.height = height;
        this.isDirty = true;
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
        if(!this.color.equals(color)) {
            isDirty = true;
            this.color = color;
        }
    }

    public Vector4f getColor(){
        return color;
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

    public void dearGui(){
        float[] imColor = {color.x, color.y, color.z, color.w};

        if(ImGui.colorPicker4("color Picker", imColor)){
            color.set(imColor);
            isDirty = true;
        }
    }

    public int getTexID(){
        return texture.getTexID();
    }

    public float getWidth(){
        return  width;
    }

    public float getHeight(){
        return  height;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
    }
}
