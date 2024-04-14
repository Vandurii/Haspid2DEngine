package main.renderer;

import main.haspid.Camera;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int VAO, VBO;
    private static float[] vertexArray;
    private static int vertexArrayBytes;
    private static int maxLines;
    private static boolean started;
    private static int pointsInLine;
    private static int pointSizeFloat;
    private static int lineSizeFloat;
    private static ArrayList<Line2D> linesList;
    private static Shader line2DShader;
    static float[] cVertexArray;

    private static void start(){
        maxLines = 500;
        pointsInLine = 2;
        pointSizeFloat = 6;
        linesList = new ArrayList<>();
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

        glLineWidth(1);
        started = true;
    }

    private static void beginFrame(){
        if(!started) start();

        for(int i = 0; i < linesList.size(); i++){
            if(linesList.get(i).beginFrame() < 0){
                linesList.remove(i);
                i--;
            }
        }
    }

    public static void draw(){
        beginFrame();

        int offset = 0;
        for(Line2D line: linesList){

            for(int i = 0; i < 2; i++){
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

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        cVertexArray = Arrays.copyOfRange(vertexArray, 0,  linesList.size() * lineSizeFloat);
        glBufferSubData(GL_ARRAY_BUFFER, 0, cVertexArray);

        Camera camera = Window.getInstance().getCurrentScene().getCamera();
        line2DShader.use();
        line2DShader.uploadValue("uProjection", camera.getUProjection());
        line2DShader.uploadValue("uView", camera.getUView());

        glBindVertexArray(VAO);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, linesList.size() * lineSizeFloat);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        line2DShader.detach();
    }

    public static void drawCircle2D(Vector2f centre, float radius){
        drawCircle2D(centre, radius, colorGreen);
    }

    public static void drawCircle2D(Vector2f centre, float radius, Vector3f color){
        drawCircle2D(centre, radius, color, 1);
    }

    public static void drawCircle2D(Vector2f centre, float radius, Vector3f color, int lifeTime){
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;

        float currentAngle = 0;
        for(int i = 0; i < points.length; i++){
            Vector2f tmp = new Vector2f(0, radius);
            rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(centre);

            currentAngle += increment;
            if(i == 0) continue;

            addLine2D(points[i - 1], points[i], color, lifeTime);
        }
        addLine2D(points[0], points[points.length - 1], color, lifeTime);
    }

    public static void drawBoxes2D(Vector2f center, Vector2f dimension, float rotation){
        drawBoxes2D(center, dimension, rotation, colorGreen);
    }

    public static void drawBoxes2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color){
        drawBoxes2D(center, dimension, rotation, color, 1);
    }

    public static void drawBoxes2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifeTime){
        Vector2f min = new Vector2f(center).add(new Vector2f(dimension.mul(0.5f)));
        Vector2f max = new Vector2f(center).sub(new Vector2f(dimension.mul(0.5f)));

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

        addLine2D(vertices[0], vertices[1], color,  lifeTime);
        addLine2D(vertices[1], vertices[2], color,  lifeTime);
        addLine2D(vertices[2], vertices[3], color,  lifeTime);
        addLine2D(vertices[3], vertices[0], color,  lifeTime);
    }

    public static void addLine2D(Vector2f from, Vector2f to){
        addLine2D(from, to, colorRed);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifeTime){
        if(!started) start();
        if(linesList.size() < maxLines){
            linesList.add(new Line2D(from, to, color, lifeTime));
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

    public static List<Line2D> getLineList(){
        return linesList;
    }

    public static void printValues() {
        System.out.println("******************");
        System.out.println(Arrays.toString(cVertexArray));
    }
}
