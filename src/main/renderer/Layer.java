package main.renderer;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL11.glLineWidth;

public abstract class Layer {
    int VAO;
    private String ID;
    private int zIndex;
    private boolean dirty;
    private boolean disabled;
    protected double resizeTime;
    protected double updateTime;
    protected float[] vertexArray;


    public Layer(int zIndex, String ID){
        this.ID = ID;
        this.zIndex = zIndex;
    }

    public abstract void resize();

    public abstract void draw();

    public abstract void clearLineList();

    public abstract void addLine(Line2D line);

    public void calculateLineWidth(){
        // grid can't be larger the maxWidth
        float width = (float) Math.min(currentZoomValue * lineWidthScala, maxLineWidth);

        // grid can't be taller then 1
        width = Math.max(1, width);
        glLineWidth(width);
    }

    public void disable(){
        disabled = true;
    }

    public void enable(){
        disabled = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isEnabled(){
        return !disabled;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getzIndex() {
        return zIndex;
    }

    public String getID() {
        return ID;
    }

    public double getResizeTime() {
        return resizeTime;
    }

    public double getUpdateTime() {
        return updateTime;
    }

    public float[] getVertexArray(){
        return vertexArray;
    }
}
