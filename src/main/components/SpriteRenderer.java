package main.components;

import imgui.ImGui;
import main.haspid.Transform;
import main.util.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private transient boolean remove;
    private transient boolean isDirty;
    private transient boolean markToRelocate;
    private transient Transform lastTransform;

    private int spriteID;
    private Vector4f color;
    private Texture texture;
    private float width, height;
    private Vector2f[] texCords;

    public SpriteRenderer() {
        this.isDirty = true;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public SpriteRenderer(Vector4f color) {
        this.isDirty = true;
        this.color = color;
        this.texCords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public SpriteRenderer(Texture texture) {
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

    public SpriteRenderer(Vector4f color, Vector2f[] texCords) {
        this.isDirty = true;
        this.color = color;
        this.texCords = texCords;
    }

    public SpriteRenderer(Texture texture, float width, float height, Vector2f[] texCords) {
        this.isDirty = true;
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = texCords;
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(getParent().getTransform())) {
            getParent().getTransform().copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void start() {
        lastTransform = getParent().getTransform().copy();
    }

    public void dearGui() {
        super.dearGui();
        float[] imColor = {color.x, color.y, color.z, color.w};

        if (ImGui.colorEdit4("color Picker", imColor)) {
            color.set(imColor);
            isDirty = true;
        }
    }

    @Override
    public SpriteRenderer copy(){
        return new SpriteRenderer(texture, width, height, getSpriteCords());
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

    public void unmarkToRemove() {
        remove = false;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public boolean isIDDefault() {
        return spriteID == 0;
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

    public Vector2f[] getSpriteCords() {
        return texCords;
    }

    public void setSpriteCords(Vector2f[] texCords) {
        this.texCords = texCords;
    }

    public Texture getTexture() {
        return texture;
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
