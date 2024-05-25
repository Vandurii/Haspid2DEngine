package main.renderer;

import main.haspid.Console;
import main.haspid.Camera;
import main.haspid.Log;
import main.haspid.Window;
import main.util.AssetPool;
import main.util.Shader;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class StaticLayer extends Layer {

    public StaticLayer(int zIndex, String ID) {
        super(zIndex, ID);
    }

    public void reload(){
        Console.addLog(new Log(INFO, "Layer was reloaded: " + ID + " :" + zIndex));

        int numberOfLines = lineList.size();
        vertexArray = new float[numberOfLines * lineSizeFloat];

        int offset = 0;
        for(Line2D line: lineList) {
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
        }

        // Generate VAO
         VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, pointSizeFloat * Float.BYTES, 3 * Float.BYTES);

        setDirty(false);
    }

    public void draw(){
        if(disabled){
            Console.addLog(new Log(INFO, "Can't draw because layer in Disabled: "  + ID + " :" + zIndex));
            return;
        }

        calculateLineWidth();

        glBindVertexArray(VAO);

        int numberOfLines = lineList.size();
        Shader line2DShader = AssetPool.getShader(line2DShaderPath);
        Camera camera = Window.getInstance().getCurrentScene().getCamera();

        line2DShader.use();
        line2DShader.uploadValue("uProjection", camera.getUProjection());
        line2DShader.uploadValue("uView", camera.getUView());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES,  0, numberOfLines  * pointsInLine);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        line2DShader.detach();
    }

    public void printPoint(){
        System.out.println("Start");
        for(int i = 0; i < vertexArray.length; i++){
            System.out.print(String.format("%.1f\t\t",vertexArray[i]));
            if((i + 1) % 3 == 0) System.out.print("\t");
            if((i + 1) % pointSizeFloat == 0) System.out.println();
            if((i + 1) % (pointSizeFloat * pointsInLine) == 0) System.out.println();
        }
    }

}
