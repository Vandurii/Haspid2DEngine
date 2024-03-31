package main.haspid;

import main.renderer.Shader;
import main.renderer.Texture;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class EditorScene extends Scene{

    private int VAO, EBO, VBO;
    private Shader defaultShader;
    private Texture testTexture;

    private float[] vertexArray = {
             scale, 0f,    0f,       1f, 0f, 0f, 1f,    1f, 1f,
             0f,    scale, 0f,       0f, 1f, 0f, 1f,    0f, 0f,
             scale, scale, 0f,       0f, 0f, 1f, 1f,    1f, 0f,
             0f,    0f,    0f,       1f, 1f, 0f, 1f,    0f, 1f
    };

    private int[] elementArrray = {
            2, 1, 0,
            0, 1, 3
    };

    @Override
    public void init() {
        testTexture = new Texture(marioImagePath);
        camera = new Camera(new Vector2f());
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
        int texCoordsSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + texCoordsSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, texCoordsSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * floatSizeBytes);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        camera.setPosition(new Vector2f(camera.getPosition().x, camera.getPosition().y - 0.16f) );
        defaultShader.use();
        defaultShader.uploadValue("uProjection", camera.getUProjection());
        defaultShader.uploadValue("uView", camera.getUView());

        defaultShader.uploadValue("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        glBindVertexArray(VAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, elementArrray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        //glDisableVertexAttribArray(2);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
