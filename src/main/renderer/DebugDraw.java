package main.renderer;

import main.haspid.Camera;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

import static main.Configuration.line2DShaderPath;
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

        glLineWidth(2);
        started = true;
    }

    public static void beginFrame(){
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
                Vector3f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                vertexArray[offset + 0] = position.x;
                vertexArray[offset + 1] = position.y;
                vertexArray[offset + 2] = position.z;

                vertexArray[offset + 3] = color.x;
                vertexArray[offset + 4] = color.y;
                vertexArray[offset + 5] = color.z;

                offset += pointSizeFloat;
            }
        }

        // printPointsValues();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0,  linesList.size() * lineSizeFloat));

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

    public static void addLine2D(Vector3f from, Vector3f to){
        addLine2D(from, to, new Vector3f(0, 1, 0));
    }

    public static void addLine2D(Vector3f from, Vector3f to, Vector3f color){
        addLine2D(from, to, color, 5);
    }

    public static void addLine2D(Vector3f from, Vector3f to, Vector3f color, int lifeTime){
        if(linesList.size() < maxLines){
            linesList.add(new Line2D(from, to, color, lifeTime));
        }
    }
}
