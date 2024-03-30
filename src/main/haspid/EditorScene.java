package main.haspid;

import main.renderer.Shader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class EditorScene extends Scene{

    private int VAO, EBO, VBO;
    private Shader defaultShader;

    private float[] vertexArray = {
             0.5f, -0.5f, 0f,        1f, 0f, 0f, 1f,
            -0.5f,  0.5f, 0f,        0f, 1f, 0f, 1f,
             0.5f,  0.5f, 0f,        0f, 0f, 1f, 1f,
            -0.5f, -0.5f, 0f,        1f, 1f, 0f, 1f
    };

    private int[] elementArrray = {
            2, 1, 0,
            0, 1, 3
    };

    @Override
    public void init() {
        defaultShader = new Shader(defaultShaderPath);
        defaultShader.compile();

        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Generate EBO
        EBO = glGenBuffers();
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArrray.length);
        elementBuffer.put(elementArrray).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int floatSizeBytes = Float.BYTES;
        int positionSize = 3;
        int colorSize = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();
        glBindVertexArray(VAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, elementArrray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
