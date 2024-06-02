package main.components;

import imgui.ImGui;
import main.editor.JImGui;
import main.renderer.Drawable;
import main.util.AssetPool;
import main.util.Texture;
import org.joml.Vector2d;
import org.joml.Vector4f;

import static main.Configuration.*;

public class SpriteRenderer extends Component implements Drawable {
    private transient boolean remove;
    private transient boolean isDirty;
    private transient boolean isHighLighted;
    private transient boolean markToRelocate;

    private Vector4f color;
    private boolean flipped;
    private String texturePath;
    private Vector2d[] texCords;
    private double width, height;
    private transient int spriteID;
    private transient Texture texture;
    private transient  Vector4f originalColor;

    public SpriteRenderer() {
        this.isDirty = true;
        this.color = defaultSpriteColor;
        this.texCords = defaultSpriteCords;
    }

    public SpriteRenderer(Vector4f color) {
        this.isDirty = true;
        this.color = color;
        this.texCords = defaultSpriteCords;
    }

    public SpriteRenderer(Texture texture) {
        this.isDirty = true;
        this.texture = texture;
        this.flipped = texture.isFlipped();
        this.texturePath = texture.getFilePath();
        this.color = defaultSpriteColor;
        this.texCords = defaultSpriteCords;
    }

    public SpriteRenderer(Texture texture, double width, double height, Vector2d[] texCords) {
        this.isDirty = true;
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.texCords = texCords;
        this.color = defaultSpriteColor;
        this.flipped = texture.isFlipped();
        this.texturePath = texture.getFilePath();
    }

    @Override
    public void update(float dt) {
        if(getParent().isDirty()){
            isDirty = true;
        }
    }

    @Override
    public SpriteRenderer copy(){
        Texture tex = texture != null ? texture : AssetPool.getTexture(texturePath, flipped);

        return new SpriteRenderer(tex, width, height, getSpriteCords());
    }

    public void resetColor(){
        isHighLighted = false;
       if(originalColor == null)throw new IllegalStateException("the color is null SG");
        setColor(originalColor);
    }

    public void dearGui() {
        super.dearGui();
        spriteID = (int) JImGui.drawValue("Texture Slot ID:", spriteID);

        float[] imColor = {color.x, color.y, color.z, color.w};

        if (ImGui.colorEdit4("color Picker", imColor)) {
            color.set(imColor);
            isDirty = true;
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public boolean isMarkedToRemove() {
        return remove;
    }

    public boolean isMarkToRelocate() {
        return markToRelocate;
    }

    public void unMarkToRelocate(boolean value) {
        markToRelocate = value;
    }

    public void markToRemove() {
        remove = true;
    }

    public void markToRelocate(){
        remove = true;
        markToRelocate = true;
    }

    public void unMarkToRemove() {
        remove = false;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public boolean isIDDefault() {
        return spriteID == 0;
    }

    public boolean isHighLighted(){
        return isHighLighted;
    }

    public void setClean() {
        isDirty = false;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            isDirty = true;
            this.color = color;
        }
    }

    public Vector2d[] getSpriteCords() {
        return texCords;
    }

    public void setSpriteCords(Vector2d[] texCords) {
        this.texCords = texCords;
    }

    public Texture getTexture() {
        if(texture == null){
            String path = texturePath != null ? texturePath : defaultTexturePath;
            return AssetPool.getTexture(path, flipped);
        }else {
            return texture;
        }
    }

    public int getSpriteID() {
        return spriteID;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setSpriteID(int spriteID) {
        this.spriteID = spriteID;
    }

    public int getTexID() {
        return texture.getTexID();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setHighLight(boolean isHighLighted){
        if(isHighLighted) {
            originalColor = new Vector4f(color.x, color.y, color.z, color.w);
        }

        this.isHighLighted = isHighLighted;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isFlipped(){
        return flipped;
    }

    public String getTexPath(){
        return texturePath;
    }
}
