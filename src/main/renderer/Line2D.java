package main.renderer;

import main.Configuration;
import main.components.Component;
import main.components.physicsComponent.BoxCollider;
import main.haspid.Console;
import main.haspid.Log;
import main.haspid.Transform;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static main.Configuration.colliderID;
import static main.renderer.DrawMode.Dynamic;

public class Line2D {
    private Vector2d to;
    private Vector2d from;
    private Vector3f color;
    private transient int ID;
    private transient boolean dirty;

    public Line2D( Vector2d from, Vector2d to, Vector3f color) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.dirty = true;
    }


    public boolean isDirty(){
        return dirty;
    }

    public Vector2d getFrom() {
        return from;
    }

    public Vector2d getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setNewValues(Vector2d from, Vector2d to){
        Console.addLog(new Log(Log.LogType.INFO, "The line position value has changed: " + ID));
        this.from.x = from.x;
        this.from.y = from.y;
        this.to.x = to.x;
        this.to.y = to.y;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Line2D line)) return false;

        return this.to.x == line.getTo().x && this.to.y == line.getTo().y && this.from.x == line.getFrom().x && this.from.y == line.getFrom().y &&
                this.color.x == line.getColor().x && this.color.y == line.getColor().y && this.color.z == line.getColor().z;
    }

    @Override
    public int hashCode(){
        return to.hashCode() + from.hashCode() + color.hashCode();
    }
}
