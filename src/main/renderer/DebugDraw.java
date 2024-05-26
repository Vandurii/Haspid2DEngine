package main.renderer;

import main.haspid.Console;
import main.haspid.Log;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.renderer.DrawMode.Dynamic;
import static main.renderer.DrawMode.Static;

public class DebugDraw {

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
                            layer.reload();
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

    public static void addLine2D(Vector2d from, Vector2d to, String name, DrawMode drawMode){
        addLine2D(from, to, debugDefaultColor, name, debugDefaultZIndex, drawMode);
    }

    public static void addLine2D(Vector2d from, Vector2d to, String name, int zIndex, DrawMode drawMode){
        addLine2D(from, to, debugDefaultColor, name, zIndex, drawMode);
    }

    public static void addLine2D(Vector2d from, Vector2d to, Vector3f color, String name, int zIndex, DrawMode drawMode){
        Line2D line = new Line2D(from, to, color);

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
                    //if line list contain this line then return
                   // if(layer.contains(line)) return;
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
            staticLayerList.add(newStaticLayer);
        }else{
            DynamicLayer newDynamicLayer = new DynamicLayer(zIndex, name);
            newDynamicLayer.init();
            newDynamicLayer.addLine(line);
            dynamicLayerList.add(newDynamicLayer);
        }
    }

    public static void addCircle(Vector2d centre, double radius, String destination, DrawMode drawMode){
        addCircle(centre, radius, debugDefaultColor,  destination, debugDefaultZIndex, drawMode);
    }

    public static void addCircle(Vector2d centre, double radius, String destination, int zIndex, DrawMode drawMode){
        addCircle(centre, radius, debugDefaultColor, destination, zIndex, drawMode);
    }

    public static void addCircle(Vector2d centre, double radius, Vector3f color,String destination, int zIndex, DrawMode drawMode){
        Vector2d[] points = new Vector2d[20];
        int increment = 360 / points.length;

        double currentAngle = 0;
        for(int i = 0; i < points.length; i++){
            Vector2d tmp = new Vector2d(0, radius);
            rotate(tmp, currentAngle, new Vector2d());
            points[i] = new Vector2d(tmp).add(centre);

            currentAngle += increment;
            if(i == 0) continue;

            addLine2D(points[i - 1], points[i], color, destination, zIndex, drawMode);
        }
        addLine2D(points[0], points[points.length - 1], color, destination, zIndex, drawMode);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, String destination, DrawMode drawMode){
        addBox(center, dimension, rotation, debugDefaultColor, destination, debugDefaultZIndex, drawMode);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, String destination, int zIndex, DrawMode drawMode){
        addBox(center, dimension, rotation, debugDefaultColor, destination, zIndex, drawMode);
    }

    public static void addBox(Vector2d center, Vector2d dimension, double rotation, Vector3f color, String destination, int zIndex, DrawMode drawMode){

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

        addLine2D(vertices[0], vertices[1], color, destination, zIndex, drawMode);
        addLine2D(vertices[1], vertices[2], color, destination, zIndex, drawMode);
        addLine2D(vertices[2], vertices[3], color, destination, zIndex, drawMode);
        addLine2D(vertices[3], vertices[0], color, destination, zIndex, drawMode);
    }

    public static void getLines(Vector2d center, Vector2d dimension, double rotation, String destination, DrawMode drawMode, Vector2d newCenter, Vector2d newDimension, double newRotation){

        // calculate vertices for searching line
        Vector2d[] vertices = calculateVertices(center, dimension, rotation);
        // calculate vertices for new values
        Vector2d[] newVertices = calculateVertices(newCenter, newDimension, newRotation);

        // select correct type of layer and find searching layer
        Layer layer = null;
        List<? extends Layer> layerList = drawMode == Dynamic ? dynamicLayerList : staticLayerList;
        for(Layer lay: layerList){
          if(lay.getID().equals(destination)){
              layer = lay;
          }
        }

        Vector2d from = vertices[0];
        Vector2d to = vertices[1];
        Line2D firstLine = layer.findLine(from, to);
      //  System.out.println(String.format("line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y));

        from = vertices[1];
        to = vertices[2];
        Line2D secondLine = layer.findLine(from, to);
        //System.out.println(String.format("line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y));


        from = vertices[2];
        to = vertices[3];
        Line2D thirdLine = layer.findLine(from, to);
        //System.out.println(String.format("line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y));

        from = vertices[3];
        to = vertices[0];
        Line2D fourthLine = layer.findLine(from, to);
        //System.out.println(String.format("line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y));

        Vector2d newFrom = newVertices[0];
        Vector2d newTo = newVertices[1];
        firstLine.setNewValues(newFrom, newTo);
        firstLine.setDirty(true);

        newFrom = newVertices[1];
        newTo = newVertices[2];
        secondLine.setNewValues(newFrom, newTo);
        secondLine.setDirty(true);

        newFrom = newVertices[2];
        newTo = newVertices[3];
        thirdLine.setNewValues(newFrom, newTo);
        thirdLine.setDirty(true);

        newFrom = newVertices[3];
        newTo = newVertices[0];
        fourthLine.setNewValues(newFrom, newTo);
        fourthLine.setDirty(true);
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

    private static DrawMode resolveMode(String ID){

        for(Layer layer: staticLayerList){
            if(layer.getID().equals(ID)) return Static;
        }

        for(Layer layer: dynamicLayerList){
            if(layer.getID().equals(ID)) return Dynamic;
        }

        Console.addLog(new Log(Log.LogType.ERROR, "Can't find draw mode for " + ID));
        return null;
    }
}


