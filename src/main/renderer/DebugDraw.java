package main.renderer;

import main.haspid.GameObject;
import main.haspid.Window;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;
import static main.renderer.DrawMode.Static;

public class DebugDraw {

    private static int numberOfLineForCircle = 20;
    private static List<StaticLayer> staticLayerList = new ArrayList<>();
    private static List<DynamicLayer> dynamicLayerList = new ArrayList<>();


    public static void notify(DebugDrawEvents eventType, String ID){
        // select correct list
        List<? extends Layer> layerList = null;
        DrawMode drawMode = resolveMode(ID);
        if(drawMode == Static){
            layerList = staticLayerList;
        }else{
            layerList = dynamicLayerList;
        }

        for(Layer layer: layerList){
            if(layer.getID().equals(ID)){
                switch (eventType){
                    case Clear -> {
                        layer.clearLineList();
                    }
                    case SetDirty -> {
                        layer.setDirty(true);
                    }
                    case Draw -> {
                        // reload if dirty
                        if(layer.isDirty()){
                            layer.resize();
                        }

                        // draw layer
                        layer.draw();
                    }
                    case Disable ->{
                        layer.disable();
                    }
                    case Enable ->{
                        layer.enable();
                    }
                }
            }
        }
    }

    public static boolean request(DebugDrawRequest request, String ID){
        for(StaticLayer staticLayer: staticLayerList){
            if(staticLayer.getID().equals(ID)){
                switch (request){
                    case IsEnabled ->{
                        return staticLayer.isEnabled();
                    }
                }
            }
        }
        return false;
    }

    public static void addLine2D(Vector2d from, Vector2d to, String name, DrawMode drawMode, GameObject parent){
        addLine2D(from, to, debugDefaultColor, name, debugDefaultZIndex, drawMode, parent);
    }

    public static void addLine2D(Vector2d from, Vector2d to, String name, int zIndex, DrawMode drawMode, GameObject parent){
        addLine2D(from, to, debugDefaultColor, name, zIndex, drawMode, parent);
    }

    public static void addLine2D(Vector2d from, Vector2d to, Vector3f color, String name, int zIndex, DrawMode drawMode, GameObject parent){
        Line2D line = new Line2D(from, to, color);

        // when object is not null add line component to it
        if(parent != null){
            Window.getInstance().getCurrentScene().addComponentSafe(parent, line);
        }

        // select the correct list
        List<? extends Layer> layerList = null;
        if(drawMode == Static){
            layerList = staticLayerList;
        }else{
            layerList = dynamicLayerList;
        }

        // search for layer if you find it then add line to it and return
        for(Layer layer: layerList){
            if(layer.getID().equals(name)){
                if(layer.getzIndex() == zIndex) {
                    layer.addLine(line);
                }else{
                    throw new IllegalStateException("Unable to load the line, the z index is wrong");
                }
                return;
            }
        }

        //i f layer wasn't found then create it and add line to it
        if(drawMode == Static) {
            StaticLayer newStaticLayer = new StaticLayer(zIndex, name);
            newStaticLayer.addLine(line);
            newStaticLayer.init();
            staticLayerList.add(newStaticLayer);
        }else{
            DynamicLayer newDynamicLayer = new DynamicLayer(zIndex, name);
            newDynamicLayer.addLine(line);
            newDynamicLayer.init();
            dynamicLayerList.add(newDynamicLayer);
        }
    }

    public static void addCircle(Vector2d centre, double radius, String destination, DrawMode drawMode, GameObject parent){
        addCircle(centre, radius, debugDefaultColor,  destination, debugDefaultZIndex, drawMode, parent);
    }

    public static void addCircle(Vector2d centre, double radius, String destination, int zIndex, DrawMode drawMode, GameObject parent){
        addCircle(centre, radius, debugDefaultColor, destination, zIndex, drawMode, parent);
    }

    public static void addCircle(Vector2d centre, double radius, Vector3f color,String destination, int zIndex, DrawMode drawMode, GameObject parent){
        Vector2d[] points = new Vector2d[numberOfLineForCircle];
        int increment = 360 / points.length;

        double currentAngle = 0;
        for(int i = 0; i < points.length; i++){
            Vector2d tmp = new Vector2d(0, radius);
            rotate(tmp, currentAngle, new Vector2d());
            points[i] = new Vector2d(tmp).add(centre);

            currentAngle += increment;
            if(i == 0) continue;

            addLine2D(points[i - 1], points[i], color, destination, zIndex, drawMode, parent);
        }
        addLine2D(points[0], points[points.length - 1], color, destination, zIndex, drawMode, parent);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, String destination, DrawMode drawMode, GameObject parent){
        addBox(center, dimension, rotation, debugDefaultColor, destination, debugDefaultZIndex, drawMode, parent);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, String destination, int zIndex, DrawMode drawMode, GameObject parent){
        addBox(center, dimension, rotation, debugDefaultColor, destination, zIndex, drawMode, parent);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, Vector3f color, String destination, int zIndex, DrawMode drawMode, GameObject parent){

       // calculate vertices for searching line
        Vector2d[] vertices = calculateVertices(center, dimension, rotation);

        addLine2D(vertices[0], vertices[1], color, destination, zIndex, drawMode, parent);
        addLine2D(vertices[1], vertices[2], color, destination, zIndex, drawMode, parent);
        addLine2D(vertices[2], vertices[3], color, destination, zIndex, drawMode, parent);
        addLine2D(vertices[3], vertices[0], color, destination, zIndex, drawMode, parent);
    }

    private static Vector2d[] calculateVertices(Vector2d center, Vector2d dimension, double rotation){
        Vector2d min = new Vector2d(center).add(new Vector2d(dimension.x * 0.5f, dimension.y * 0.5f));
        Vector2d max = new Vector2d(center).sub(new Vector2d(new Vector2d(dimension.x * 0.5f, dimension.y * 0.5f)));

        Vector2d[] vertices = {
                new Vector2d(min.x, min.y),
                new Vector2d(max.x, min.y),
                new Vector2d(max.x, max.y),
                new Vector2d(min.x, max.y)
        };

        if(rotation > 0){
            for(Vector2d ver: vertices){
                rotate(ver, rotation, center);
            }
        }

        return vertices;
    }

    public static void rotate(Vector2d vec, double angleDeg, Vector2d origin) {
        double x = vec.x - origin.x;
        double y = vec.y - origin.y;

        double cos = Math.cos(Math.toRadians(angleDeg));
        double sin = Math.sin(Math.toRadians(angleDeg));

        double xPrime = (x * cos) - (y * sin);
        double yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static void reset(){
        staticLayerList = new ArrayList<>();
        dynamicLayerList = new ArrayList<>();
    }

    private static DrawMode resolveMode(String ID){

        for(Layer layer: staticLayerList){
            if(layer.getID().equals(ID)) return Static;
        }

        for(Layer layer: dynamicLayerList){
            if(layer.getID().equals(ID)) return Dynamic;
        }

        return null;
    }

    public static List<StaticLayer> getStaticLayerList(){
        return staticLayerList;
    }

    public static List<DynamicLayer> getDynamicLayerList(){
        return dynamicLayerList;
    }
}


