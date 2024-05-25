package main.renderer;

import main.components.Component;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D extends Component {
    private Vector2d to;
    private Vector2d from;
    private Vector3f color;

    public Line2D( Vector2d from, Vector2d to, Vector3f color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    @Override
    public void update(float dt) {

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
