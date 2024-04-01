package main.scene;

import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.GameObject;
import main.haspid.Window;
import main.renderer.Shader;
import main.renderer.Texture;
import main.scene.Scene;
import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class EditorScene extends Scene {

    private int VAO;
    private Shader defaultShader;
    private Texture testTexture;
    private ArrayList<Attribute> attributes;

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
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STATIC_DRAW);

        //Generate EBO
        int EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArrray, GL_STATIC_DRAW);

        initVertexAttribPointer();
    }

    public void initVertexAttribPointer(){
        attributes = new ArrayList<>();
        attributes.add(new Attribute("position", 3));
        attributes.add(new Attribute("color", 4));
        attributes.add(new Attribute("texCoords", 2));

        int size = 0;
        for(Attribute a: attributes){
            size += a.size;
        }

        int startFrom = 0;
        int vertexSizeBytes = size * Float.BYTES;
        for(int i = 0; i < attributes.size(); i++){
            glVertexAttribPointer(i, attributes.get(i).size, GL_FLOAT, false, vertexSizeBytes, startFrom * Float.BYTES);
            startFrom += attributes.get(i).size;
        }
    }

    @Override
    public void update(float dt) {
        camera.setPosition(new Vector2f(camera.getPosition().x, camera.getPosition().y - 0.16f));

        defaultShader.use();
        defaultShader.uploadValue("uProjection", camera.getUProjection());
        defaultShader.uploadValue("uView", camera.getUView());
        defaultShader.uploadValue("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        glBindVertexArray(VAO);
        enableAttribArrays();

        glDrawElements(GL_TRIANGLES, elementArrray.length, GL_UNSIGNED_INT, 0);

        disableAttribArrays();
        glBindVertexArray(0);
        defaultShader.detach();
    }

    public void enableAttribArrays(){
        for(int i = 0; i < attributes.size(); i++){
            glEnableVertexAttribArray(i);
        }
    }

    public void disableAttribArrays(){
        for(int i = 0; i < attributes.size(); i++){
            glDisableVertexAttribArray(0);
        }
    }

    public class Attribute{
        private int size;
        private String name;

        public Attribute(String name, int size){
            this.name = name;
            this.size = size;
        }
    }
}
