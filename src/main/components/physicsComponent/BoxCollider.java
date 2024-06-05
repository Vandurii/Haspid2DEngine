package main.components.physicsComponent;

import main.editor.InactiveInEditor;
import main.haspid.GameObject;
import main.haspid.Transform;
import main.physics.BodyType;
import main.renderer.DebugDraw;
import main.renderer.DebugDrawEvents;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;

public class BoxCollider extends Collider {
    private transient Vector2d pos;
    private transient Vector2d scale;
    private transient Vector2d lastScale;
    private transient Transform transform;

    private static ArrayList<BoxCollider> staticSeeneBoxColliders = new ArrayList<>();
    private static ArrayList<Transform> staticColliderData = new ArrayList<>();

    @Override
    public void init(){
        // call super method so that it initialize scene and physic
        super.init();

        this.transform = getParent().getTransform();
        this.scale = transform.getScale();
        this.pos = transform.getPosition();
        this.transform = getParent().getTransform();
        this.lastScale = new Vector2d(scale.x, scale.y);

        if(getParent().isSerializable() && bodyType == BodyType.Static) {
            staticSeeneBoxColliders.add(this);
        }
    }

    @Override
    public void drawNewLines() {
        this.transform = getParent().getTransform();
        this.scale = transform.getScale();
        this.pos = transform.getPosition();
       DebugDraw.addBox(pos, scale, transform.getRotation(), colliderColor, colliderID, colliderZIndex, Dynamic, getParent());
    }

    @Override
    public boolean resize() {
        if(lastScale.x != scale.x || lastScale.y != scale.y){
            lastScale = new Vector2d(scale.x, scale.y);
            resetFixture();
            return true;
        }

        return false;
    }

    public static void build(){
        int places = 2;
        List<BoxCollider> copyList = new ArrayList<>(staticSeeneBoxColliders);
        staticColliderData.clear();
        DebugDraw.notify(DebugDrawEvents.Clear, staticColliderID);

        while(!copyList.isEmpty()) {
            BoxCollider current = copyList.get(0).copy();
            copyList.remove(0);
            Transform currentTransform = current.getParent().getTransform();
            Vector2d currentPos = currentTransform.getPosition();
            Vector2d currentScale = currentTransform.getScale();

            double start = round(currentPos.x, places);
            double end = round(currentPos.x + currentScale.x, places);
            double currentBottomSide = round(currentPos.y, places);

            int count = 0;
            for (int j = 0; count < copyList.size(); j = j >= (copyList.size() - 1) ? 0 : ++j) {
                BoxCollider checked = copyList.get(j);
                Transform checkedTransform = checked.getParent().getTransform();
                Vector2d checkedPos = checkedTransform.getPosition();
                Vector2d checkedScale = checkedTransform.getScale();

                double checkedRightSide = round(checkedPos.x + checkedScale.x, places);
                double checkedLeftSide = round(checkedPos.x, places);
                double checkedBottomSide = round(checkedPos.y, places);

                if (end == checkedLeftSide && checkedBottomSide == currentBottomSide) {
                    end = checkedRightSide;
                    copyList.remove(j);
                    count = 0;
                    continue;
                }

                if (start == checkedRightSide && checkedBottomSide == currentBottomSide) {
                    start = checkedLeftSide;
                    copyList.remove(j);
                    count = 0;
                    continue;
                }
                count++;
            }

            double length = end - start;
            double currentXPos = start + (length / 2) - (currentScale.x / 2);
            Vector2d pos = new Vector2d(currentXPos, currentPos.y);
            Vector2d scale = new Vector2d(length, currentScale.y);
            DebugDraw.addBox(pos, scale, 0, colliderColor, staticColliderID, colliderZIndex, Dynamic, null);
            staticColliderData.add(new Transform(pos, scale));
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void reset(){
        staticSeeneBoxColliders.clear();
    }

    @Override
    public BoxCollider copy(){
        BoxCollider boxCollider = new BoxCollider();
        boxCollider.setParent(getParent());
        return boxCollider;
    }

    @Override
    public void dearGui(){
        super.dearGui();
        dearGui(this);
    }

    public static void removeFromStaticCollierList(BoxCollider boxCollider) {
        staticSeeneBoxColliders.remove(boxCollider);
    }

    public static List<Transform> getColliderData(){
        return staticColliderData;
    }
}
