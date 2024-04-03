package main.renderer;

import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.Transform;
import main.haspid.Window;

import main.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    private int pointSizeFloat;
    private int pointsInSquare;
    private int squareSizeFloat;
    private int pointsIn2Triangles;

    private int VAO, VBO;
    private int maxBathSize;
    private Shader defaultShader;
    private ArrayList<Attribute> attributes;

    private float[] vertexArray;
    private int vertexArrayBytes;

    private boolean hasRoom;
    private int spriteCount;
    private SpriteRenderer[] spriteListToRender;

    private List<Texture> textureList;
    private int[] uTextures = {0, 1, 2, 3, 4, 5, 6, 7};

    public RenderBatch(int maxBathSize){
        textureList = new ArrayList<>();
        initVertexAttribPointer(false);

        this.hasRoom = true;
        this.maxBathSize = maxBathSize;
        this.attributes = new ArrayList<>();
        this.spriteListToRender = new SpriteRenderer[maxBathSize];

        this.pointsInSquare = 4;
        this.pointsIn2Triangles = 6;
        this.squareSizeFloat = pointsInSquare * pointSizeFloat;
        this.vertexArray = new float[maxBathSize * squareSizeFloat];
        this.vertexArrayBytes = vertexArray.length * Float.BYTES;

        // shader
        this.defaultShader = AssetPool.getShader(defaultShaderPath);
    }

    public void start(){
        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArrayBytes, GL_DYNAMIC_DRAW);

        //Generate EBO
        int EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        int[] elementArray = generateIndices();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArray, GL_STATIC_DRAW);

        initVertexAttribPointer(true);
    }

    public void initVertexAttribPointer(boolean init){
        attributes = new ArrayList<>();
        attributes.add(new Attribute("position", 2));
        attributes.add(new Attribute("color", 4));
        attributes.add(new Attribute("texCords", 2));
        attributes.add(new Attribute("texID", 1));

        pointSizeFloat = 0;
        for(Attribute a: attributes){
            pointSizeFloat += a.size;
        }

        int startFrom = 0;
        int vertexSizeBytes = pointSizeFloat * Float.BYTES;
        if(init){
            for (int i = 0; i < attributes.size(); i++) {
                glVertexAttribPointer(i, attributes.get(i).size, GL_FLOAT, false, vertexSizeBytes, startFrom * Float.BYTES);
                startFrom += attributes.get(i).size;
            }
        }
    }

    public int[] generateIndices(){
        int[] elements = new int[maxBathSize * pointsIn2Triangles];
        for(int i = 0; i < maxBathSize; i++){
            loadElement(elements, i);
        }

        return elements;
    }

    public void loadElement(int[] elements, int index){
        int startFrom = index * pointsIn2Triangles;
        int offset = index * pointsInSquare;

        elements[startFrom + 0] = offset + 3;
        elements[startFrom + 1] = offset + 2;
        elements[startFrom + 2] = offset + 0;
        elements[startFrom + 3] = offset + 0;
        elements[startFrom + 4] = offset + 2;
        elements[startFrom + 5] = offset + 1;
    }

    public void loadVertexArray(int index){
        SpriteRenderer spriteRenderer = spriteListToRender[index];
        Transform transform = spriteRenderer.getParent().getTransform();
        Vector2f[] texCords = spriteRenderer.getSprite().getSpriteCords();
        Vector4f color = spriteRenderer.getSprite().getColor();
        Vector2f position = transform.getPosition();
        Vector2f scale = transform.getScale();
        int offset = index * squareSizeFloat;
        float xAdd = 1f;
        float yAdd = 1f;

        for(int j = 0; j < 4; j++){
            if(j == 1) yAdd = 0f;
            if(j == 2) xAdd = 0f;
            if(j == 3) yAdd = 1f;

            vertexArray[offset + 0] = position.x + (xAdd * scale.x);
            vertexArray[offset + 1] = position.y + (yAdd * scale.y);

            vertexArray[offset + 2] = color.x;
            vertexArray[offset + 3] = color.y;
            vertexArray[offset + 4] = color.z;
            vertexArray[offset + 5] = color.w;;

            vertexArray[offset + 6] = texCords[j].x;
            vertexArray[offset + 7] = texCords[j].y;

            vertexArray[offset + 8] = spriteRenderer.getSprite().getSpriteID();;

            offset += pointSizeFloat;
        }
    }

    public void addSprite(SpriteRenderer spriteRenderer){
        int index = spriteCount;
        spriteListToRender[index] = spriteRenderer;
        if(spriteCount + 1 >= maxBathSize) hasRoom = false;

        if(spriteRenderer.getSprite().hasTexture() && spriteRenderer.getSprite().isIDDefault()) {
            if(!textureList.contains(spriteRenderer.getSprite().getTexture())){
                int id = textureList.size() + 1;
                spriteRenderer.getSprite().setSpriteID(id);
                textureList.add(spriteRenderer.getSprite().getTexture());
            }
        }

        loadVertexArray(index);
        spriteCount++;

       // printPointsValues();
    }

    public void render(){
        Camera camera = Window.getInstance().getCurrentScene().getCamera();

        // For now, rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexArray);

        defaultShader.use();
        defaultShader.uploadValue("uProjection", camera.getUProjection());
        defaultShader.uploadValue("uView", camera.getUView());
        defaultShader.uploadValue("uTextures", uTextures);

        activeAndBindTextures();
        glBindVertexArray(VAO);
        enableAttribArrays();

        glDrawElements(GL_TRIANGLES, spriteCount * pointsIn2Triangles, GL_UNSIGNED_INT, 0);

        unbindTextures();
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
            glDisableVertexAttribArray(i);
        }
    }

    public void activeAndBindTextures(){
        for(int i = 0; i < textureList.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
    }

    public void unbindTextures(){
        for(int i = 0; i < textureList.size(); i++){
            textureList.get(i).unbind();
        }
    }

    public boolean hasRoom(){
        return hasRoom;
    }

    public boolean hasTexture(Texture texture){
        return textureList.contains(texture);
    }

    public boolean hasTextureListRoom(){
        return textureList.size() < 7;
    }


    public void printPointsValues(){
        System.out.println(spriteCount);
        for(int i = 1; i < ((spriteCount + 1) * pointSizeFloat * pointsInSquare) + 1; i++){
            System.out.print(vertexArray[i - 1] + " ");
            if(i % pointSizeFloat == 0) System.out.println();
            if(i % squareSizeFloat == 0) System.out.println();
        }
        System.out.println();
        System.out.println("*****************************");
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