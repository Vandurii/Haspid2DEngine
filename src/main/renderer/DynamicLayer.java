package main.renderer;

import main.haspid.*;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
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
    private int vertexArraySizeInBytes;
    private ArrayList<Integer> freeSlots;

    public DynamicLayer(int zIndex, String ID) {
        super(zIndex, ID);
        this.freeSlots = new ArrayList<>();
        this.maxBathSize = dynamicLayerInitialBatchSize;
        this.lineListToRender = new Line2D[maxBathSize];
        this.vertexArray = new float[maxBathSize * lineSizeFloat];
    }

    public void init(){
        vertexArray = new float[maxBathSize * lineSizeFloat];

        vertexArraySizeInBytes = vertexArray.length * Float.BYTES;

        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArraySizeInBytes, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 3 * Float.BYTES);
    }

    public void resize(){
        resizeTime = glfwGetTime();
        Console.addLog(new Log(INFO, "Reload layer: " + getID() + " z:" + getzIndex()));

        vertexArraySizeInBytes = vertexArray.length * Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArraySizeInBytes, GL_DYNAMIC_DRAW);

        setDirty(false);
    }

    public void loadDataToVertex(int index){
        Line2D line = lineListToRender[index];
        int offset = index * lineSizeFloat;

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

    public void deleteDataFromVertex(int index){
        // collect free slots, they will be reuse
        freeSlots.add(index);
        int offset = index * lineSizeFloat;

        for(int i = 0; i < lineSizeFloat; i++){
            vertexArray[offset + i] = 0;
        }
    }

    public void draw(){;
        checkIfDirty();

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

        glDrawArrays(GL_LINES, 0, lineCount * pointsInLine);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        line2DShader.detach();;
    }

    public void checkIfDirty(){
        boolean reload = false;
        for(int i = 0; i < lineCount; i++){
            // get line skip if it is null
            Line2D line = lineListToRender[i];
            if(line == null) continue;;

            // reload data from this object if it has changed
            if(line.isDirty()) {
                reload = true;
                Console.addLog(new Log(INFO, line.getID() + ""));
                loadDataToVertex(i);
            }

            // remove data if this object should be removed
            if(line.isMarkedToRemove()){
                reload = true;
                lineListToRender[i] = null;
                deleteDataFromVertex(i);
            }
        }

        // Reload buffer if at least one line has changed.
        // Make new array (new array is lineCount length) and reload buffer
        if(reload) {
            updateTime = glfwGetTime();
            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lineCount * lineSizeFloat));
        }
    }

    public  Line2D[] extendArray(Line2D[] array, int percentage){
        int capacity = array.length + Math.max((array.length / 100 * percentage), minArrayValueToExtend);

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
    public void addLine(Line2D line){
        if(lineCount >= lineListToRender.length){
            Console.addLog(new Log(INFO, "Extend array: size before extend: " + lineListToRender.length));
            lineListToRender = extendArray(lineListToRender, percentageValByExtend);
            Console.addLog(new Log(INFO, "Size after extend: " + lineListToRender.length));
            maxBathSize = lineListToRender.length;
            extendVertexArray();
            resize();
        }

        if(freeSlots.isEmpty()) {
            lineListToRender[lineCount] = line;
            lineCount++;
        }else{
            // reuse free slots, fill the hole
            int index = freeSlots.get(0);
            lineListToRender[index] = line;

            // remove this, because is not longer free
            freeSlots.remove(0);

            Console.addLog(new Log(INFO, "Reused slot: " + index));
        }
    }

    @Override
    public void clearLineList() {
        lineListToRender = new Line2D[maxBathSize];
        vertexArray = new float[maxBathSize * lineSizeFloat];
        lineCount = 0;
    }

    public boolean hasRoom(){
        return lineCount + 1 <= maxBathSize;
    }

    public int getLineCount(){
        // subtract free slots because they will be reused
        return lineCount - freeSlots.size();
    }

    public int getMaxBathSize(){
        return maxBathSize;
    }

    public Line2D[] getLineListToRender(){
        return lineListToRender;
    }
}
