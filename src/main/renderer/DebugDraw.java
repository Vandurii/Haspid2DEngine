package main.renderer;

import main.editor.ViewPort;
import main.haspid.Camera;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static boolean sleep;
    private static int VAO, VBO;
    private static float[] vertexArray;
    private static int vertexArrayBytes;
    private static int maxLines;
    private static boolean started;
    private static int pointsInLine;
    private static int pointSizeFloat;
    private static int lineSizeFloat;
    private static Shader line2DShader;
    private static HashMap<Integer, List<Line2D>> lineMap;

    private static void start(){
        maxLines = 500;
        pointsInLine = 2;
        pointSizeFloat = 6;
        lineMap = new HashMap<>();
        lineSizeFloat = pointsInLine * pointSizeFloat;
        vertexArray = new float[maxLines * lineSizeFloat];
        vertexArrayBytes = maxLines * lineSizeFloat * Float.BYTES;
        line2DShader = AssetPool.getShader(line2DShaderPath);

        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArrayBytes, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 3 * Float.BYTES);

        started = true;
    }

    private static void beginFrame(){
        if(!started) start();

        for(List<Line2D> lineList: lineMap.values()) {
            for (int i = 0; i < lineList.size(); ) {
                if (lineList.get(i).beginFrame() < 0) {
                    lineList.remove(i);
                }else{
                    i++;
                }
            }
        }
    }

    public static void draw(){
        if(ViewPort.getInstance().getViewPortWidth() < minimalWidthForGrid || uProjectionDimension.x * zoom > maximalWidthForGrid) return;
        if(sleep) return;;

        setLineWidth();
        beginFrame();

        int offset = 0;
        vertexArray = new float[maxLines* lineSizeFloat];

        List<Integer> zIndexList = new ArrayList<>(lineMap.keySet());
        Collections.sort(zIndexList);

        for(int j = 0; j < zIndexList.size(); j++){
            List<Line2D> list = lineMap.get(zIndexList.get(j));
            for(Line2D line: list) {
                for (int i = 0; i < 2; i++) {
                    Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                    Vector3f color = line.getColor();

                    vertexArray[offset + 0] = position.x;
                    vertexArray[offset + 1] = position.y;
                    vertexArray[offset + 2] = 0;

                    vertexArray[offset + 3] = color.x;
                    vertexArray[offset + 4] = color.y;
                    vertexArray[offset + 5] = color.z;

                    offset += pointSizeFloat;
                }
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
        //print(vertexArray);

        Camera camera = Window.getInstance().getCurrentScene().getCamera();
        line2DShader.use();
        line2DShader.uploadValue("uProjection", camera.getUProjection());
        line2DShader.uploadValue("uView", camera.getUView());

        glBindVertexArray(VAO);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, getMapSize() * lineSizeFloat);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        line2DShader.detach();
    }

    public static void print(float[] array){
        System.out.println("******************** start ***************************");
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i] + " \t");
            if((i + 1) % 6  == 0) System.out.println();
        }
    }

    public static void drawCircle2D(Vector2f centre, float radius, int zIndex){
        drawCircle2D(centre, radius, colorGreen, zIndex);
    }

    public static void drawCircle2D(Vector2f centre, float radius, Vector3f color, int zIndex){
        drawCircle2D(centre, radius, color, 1, zIndex);
    }

    public static void drawCircle2D(Vector2f centre, float radius, Vector3f color, int lifeTime, int zIndex){
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;

        float currentAngle = 0;
        for(int i = 0; i < points.length; i++){
            Vector2f tmp = new Vector2f(0, radius);
            rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(centre);

            currentAngle += increment;
            if(i == 0) continue;

            addLine2D(zIndex, points[i - 1], points[i], color, lifeTime);
        }
        addLine2D(zIndex, points[0], points[points.length - 1], color, lifeTime);
    }

    public static void drawBoxes2D(int zIndex, Vector2f center, Vector2f dimension, float rotation){
        drawBoxes2D(zIndex, center, dimension, rotation, colorGreen);
    }

    public static void drawBoxes2D(int zIndex, Vector2f center, Vector2f dimension, float rotation, Vector3f color){
        drawBoxes2D(zIndex, center, dimension, rotation, color, 1);
    }

    public static void drawBoxes2D(int zIndex, Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifeTime){
        Vector2f min = new Vector2f(center).add(new Vector2f(dimension.x * 0.5f, dimension.y * 0.5f));
        Vector2f max = new Vector2f(center).sub(new Vector2f(new Vector2f(dimension.x * 0.5f, dimension.y * 0.5f)));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y),
                new Vector2f(min.x, max.y)
        };

        if(rotation > 0){
            for(Vector2f ver: vertices){
                rotate(ver, rotation, center);
            }
        }

        addLine2D(zIndex, vertices[0], vertices[1], color,  lifeTime);
        addLine2D(zIndex, vertices[1], vertices[2], color,  lifeTime);
        addLine2D(zIndex, vertices[2], vertices[3], color,  lifeTime);
        addLine2D(zIndex, vertices[3], vertices[0], color,  lifeTime);
    }

    public static void addLine2D(int zIndex, Vector2f from, Vector2f to){
        addLine2D(zIndex, from, to, colorRed, 1);
    }

    public static void addLine2D(int zIndex, Vector2f from, Vector2f to, Vector3f color){
        addLine2D( zIndex, from, to, color, 1);
    }

    public static void addLine2D( int zIndex, Vector2f from, Vector2f to, Vector3f color, int lifeTime){
        if(!started) start();

        if(getMapSize() < maxLines){
            if(!lineMap.containsKey(zIndex)) lineMap.put(zIndex, new ArrayList<>());

            lineMap.get(zIndex).add(new Line2D(zIndex, from, to, color, lifeTime));
        }
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    private static void setLineWidth(){
        float width = 0.8f + ((1920f - ViewPort.getInstance().getViewPortWidth()) / 1920f);
        glLineWidth((float)Math.pow(width, 3));
    }

    public static HashMap<Integer, List<Line2D>> getLineMap(){
        return lineMap;
    }

    public static void sleep(){
        sleep = true;
    }

    public static void yield(){
        clearMap();
        sleep = false;
    }

    public static void resetVertexArray(){
       if(vertexArray != null){
           vertexArray = new float[vertexArray.length];
           clearMap();
           System.out.println("reset");
       }
    }

    public static void clearMap(){
        for(List<Line2D> lineList: lineMap.values()) {
            lineList.clear();
        }
    }

    public static int getMapSize(){
        int size = 0;
        for(List<Line2D> lineList: lineMap.values()) {
            size += lineList.size();
        }

        return size;
    }
}
