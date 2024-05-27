package main.renderer;

import main.haspid.*;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.Arrays;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static main.haspid.Log.LogType.WARNING;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DynamicLayer extends Layer {
    private int VBO;
    private int lineCount;
    private int maxBathSize;
    private Line2D lineListToRender[];

    public DynamicLayer(int zIndex, String ID) {
        super(zIndex, ID);
        this.maxBathSize = dynamicLayerInitialBatchSize;
        this.lineListToRender = new Line2D[maxBathSize];
        this.vertexArray = new float[maxBathSize * lineSizeFloat];
    }

    public void init(){
        vertexArray = new float[maxBathSize * lineSizeFloat];
        reload();
    }

    public void reload(){
        System.out.println("realod");
        Console.addLog(new Log(INFO, "Reload layer: " + ID + " z:" + zIndex));

        int vertexArraySizeInBytes = vertexArray.length * Float.BYTES;

        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArraySizeInBytes, GL_DYNAMIC_DRAW);

        //Generate EBO
        int EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        int[] elementArray = generateIndices();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArray, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 3 * Float.BYTES);

        setDirty(false);
    }

    public void loadVertex(int index){
        Line2D line = lineListToRender[index];
        int offset = index * pointSizeFloat * pointsInLine;

        for (int i = 0; i < 2; i++) {
            Vector2d position = i == 0 ? line.getFrom() : line.getTo();
            Vector3f color = line.getColor();

            vertexArray[offset + 0] = (float) position.x;
            vertexArray[offset + 1] = (float) position.y;
            vertexArray[offset + 2] = 0;

            vertexArray[offset + 3] = color.x;
            vertexArray[offset + 4] = color.y;
            vertexArray[offset + 5] = color.z;

            offset += pointSizeFloat;
        }

        line.setDirty(false);
    }

    public int[] generateIndices(){
        int[] elements = new int[maxBathSize * pointsInLine];
        for(int i = 0; i < (maxBathSize); i++){
            elements[i] = i;
        }

        return elements;
    }

    public void draw(){
        checkIfDirty();

        if(disabled){
            Console.addLog(new Log(INFO, "Can't draw because layer in Disabled: "  + ID + " :" + zIndex));
            return;
        }

        //  set the grid width
        calculateLineWidth();

        Shader line2DShader = AssetPool.getShader(line2DShaderPath);
        Camera camera = Window.getInstance().getCurrentScene().getCamera();

        glBindVertexArray(VAO);

        line2DShader.use();
        line2DShader.uploadValue("uProjection", camera.getUProjection());
        line2DShader.uploadValue("uView", camera.getUView());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_LINES, maxBathSize  * pointsInLine, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        line2DShader.detach();;
    }

    public void checkIfDirty(){
        boolean reload = false;
        for(int i = 0; i < lineCount; i++){
            if(lineListToRender[i].isDirty()) {
                Console.addLog(new Log(INFO, "Reloaded line: " + i));
                reload = true;
                loadVertex(i);
            }
        }

        if(reload) {
            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lineCount * lineSizeFloat));
        }
    }

    public  Line2D[] extendArray(Line2D[] array, int percentage){
        int minValueToExtends = 10;
        int capacity = array.length + Math.max((array.length / 100 * percentage), minValueToExtends);

        Line2D[] newArray = new Line2D[capacity];
        System.arraycopy(array, 0, newArray, 0, array.length);

        return newArray;
    }

    public void extendVertexArray(){
        float[] temporary = vertexArray;
        vertexArray = new float[maxBathSize * lineSizeFloat];
        System.arraycopy(temporary, 0, vertexArray, 0, temporary.length);
    }

    @Override
    public Line2D findLine(Vector2d from, Vector2d to){
        for(int i = 0; i < lineCount; i++){
            Line2D line = lineListToRender[i];
            if(line.getFrom().x == from.x && line.getFrom().y == from.y && line.getTo().x == to.x && line.getTo().y == to.y){
                return line;
            }
        }

        Console.addLog(new Log(WARNING, String.format("Can't fine this line: \t from: %.2f  %.2f \t to: %.2f  %.2f", from.x, from.y, to.x, to.y)));
        return null;
    }

    @Override
    public void addLine(Line2D line){
        if(lineCount >= lineListToRender.length){
            Console.addLog(new Log(INFO, "Extend array: size before extend: " + lineListToRender.length));
            lineListToRender = extendArray(lineListToRender, 20);
            Console.addLog(new Log(INFO, "Size after extend: " + lineListToRender.length));
            maxBathSize = lineListToRender.length;
            extendVertexArray();
            reload();
        }

        lineListToRender[lineCount] = line;
        lineCount++;
    }

    public void printPoint(){
        if(vertexArray == null){
            Console.addLog(new Log(WARNING, "Can't print values because vetex array is null: " + ID));
            return;
        }

        System.out.println("Start *********************************************************");
        for(int i = 0; i < vertexArray.length; i++){
            System.out.print(String.format("%.1f\t\t",vertexArray[i]));
            if((i + 1) % 3 == 0) System.out.print("\t");
            if((i + 1) % pointSizeFloat == 0) System.out.println();
            if((i + 1) % (pointSizeFloat * pointsInLine) == 0) System.out.println();
        }
    }
}
