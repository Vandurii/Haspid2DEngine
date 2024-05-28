package main.renderer;

import main.haspid.Console;
import main.haspid.Log;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.haspid.Log.LogType.WARNING;
import static org.lwjgl.opengl.GL11.glLineWidth;

public abstract class Layer {

    public Layer(int zIndex, String ID){
        this.ID = ID;
        this.zIndex = zIndex;
        this.lineList = new ArrayList<>();
    }

    int VAO;
    protected int zIndex;
    protected String ID;
    float[] vertexArray;
    protected boolean dirty;
    protected boolean disabled;
    protected List<Line2D> lineList;

    public abstract void reload();

    public abstract void draw();

    public void garbage(){};

    public void calculateLineWidth(){
        float width = (float) Math.min(currentZoomValue * lineWidthScala, maxLineWidth);
        glLineWidth(width);
    }

    public void disable(){
        disabled = true;
    }

    public void enable(){
        disabled = false;
    }

    public void clearLineList(){
        lineList.clear();
    }

    public void addLine(Line2D line){
        lineList.add(line);
    }

    public boolean isEnabled(){
        return !disabled;
    }

    public boolean isDirty() {
        return dirty;
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

    public List<Line2D> getLine(){
        return lineList;
    }
}
