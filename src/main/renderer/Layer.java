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

    public abstract void reload();

    public abstract void draw();

    int VAO;
    protected int zIndex;
    protected String ID;
    float[] vertexArray;
    protected boolean dirty;
    protected boolean disabled;
    protected List<Line2D> lineList;

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

//    public boolean contains(Line2D line2D){
//        return lineList.contains(line2D);
//    }

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

    public Line2D findLine(Vector2d from, Vector2d to){
        dirty = true;
        for(Line2D line: lineList){
            if(line.getFrom().x == from.x && line.getFrom().y == from.y && line.getTo().x == to.x && line.getTo().y == to.y){
                return line;
            }
        }

        Console.addLog(new Log(WARNING, String.format("Can't fine this line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y)));
        return null;
    }

    public List<Line2D> getLine(){
        return lineList;
    }
}
