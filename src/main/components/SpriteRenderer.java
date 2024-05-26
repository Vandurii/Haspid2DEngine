package main.components;

import imgui.ImGui;
import main.components.physicsComponent.BoxCollider;
import main.haspid.Transform;
import main.renderer.DebDraw;
import main.util.Texture;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static main.Configuration.colliderID;
import static main.renderer.DrawMode.Dynamic;

public class SpriteRenderer extends Component {
    private transient boolean remove;
    private transient boolean isDirty;
    private transient boolean isHighLighted;
    private transient boolean markToRelocate;
    private transient Transform lastTransform;
    private transient Vector2d lastHalfSize;


    private transient int spriteID;
    private Vector4f color;
    private Texture texture;
    private double width, height;
    private Vector2d[] texCords;

    public SpriteRenderer() {
        this.isDirty = true;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = new Vector2d[]{
                new Vector2d(1, 1),
                new Vector2d(1, 0),
                new Vector2d(0, 0),
                new Vector2d(0, 1)
        };
    }

    public SpriteRenderer(Vector4f color) {
        this.isDirty = true;
        this.color = color;
        this.texCords = new Vector2d[]{
                new Vector2d(1, 1),
                new Vector2d(1, 0),
                new Vector2d(0, 0),
                new Vector2d(0, 1)
        };
    }

    public SpriteRenderer(Texture texture) {
        this.isDirty = true;
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCords = new Vector2d[]{
                new Vector2d(1, 1),
                new Vector2d(1, 0),
                new Vector2d(0, 0),
                new Vector2d(0, 1)
        };
    }

    public SpriteRenderer(Vector4f color, Vector2d[] texCords) {
        this.isDirty = true;
        this.color = color;
        this.texCords = texCords;
    }

    public SpriteRenderer(Texture texture, double width, double height, Vector2d[] texCords) {
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
            isDirty = true;

            // line
            BoxCollider  collider = getParent().getComponent(BoxCollider.class);
            if(collider == null) return;

            Transform newTransform = getParent().getTransform();
            Vector2d newPosition = newTransform.getPosition();
            Vector2d newScale = collider.getHalfSize();

            Transform oldTransform = lastTransform;
            Vector2d oldPosition = oldTransform.getPosition();
            Vector2d oldScale = lastHalfSize != null ? lastHalfSize : collider.getHalfSize();

            DebDraw.getLines(oldPosition, new Vector2d(oldScale.x * 2, oldScale.y * 2),  oldTransform.getRotation(), colliderID, Dynamic, newPosition, new Vector2d(newScale.x * 2, newScale.y * 2), newTransform.getRotation());

            lastHalfSize = new Vector2d(collider.getHalfSize().x, collider.getHalfSize().y);
            getParent().getTransform().copy(lastTransform);
        }
    }

    @Override
    public void start() {
        lastTransform = getParent().getTransform().copy();
    }

    public void dearGui() {
       // super.dearGui();
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

    public void resetColor(){
        isHighLighted = false;
        setColor(new Vector4f(1, 1, 1, 1));
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setHighLight(boolean value){
        isHighLighted = value;
    }
}
