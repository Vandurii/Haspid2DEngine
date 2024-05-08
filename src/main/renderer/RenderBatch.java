package main.renderer;

import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.Transform;
import main.haspid.Window;

import main.util.Attribute;
import main.util.Shader;
import main.util.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

    private int pointSizeFloat;
    private int pointsInSquare;
    private int squareSizeFloat;
    private int pointsIn2Triangles;

    private int zIndex;
    private int VAO, VBO;
    private int maxBathSize;
    private ArrayList<Attribute> attributes;

    private float[] vertexArray;
    private int vertexArrayBytes;

    private boolean hasRoom;
    private int spriteCount;
    private SpriteRenderer[] spriteListToRender;

    private List<Texture> textureList;
    private int[] uTextures;

    private ArrayList<Integer> freeSlots;

    public RenderBatch(int maxBathSize, int zIndex){
        System.out.println("Created new render batch: zIndex:" + zIndex);

        this.zIndex = zIndex;
        this.textureList = new ArrayList<>();
        this.uTextures = texturesSlots;
        this.initVertexAttribPointer(false);

        this.hasRoom = true;
        this.maxBathSize = maxBathSize;
        this.spriteListToRender = new SpriteRenderer[maxBathSize];

        this.pointsInSquare = numberOfPointsInSquare;
        this.pointsIn2Triangles = numberOfPointsIn2Triangles;
        this.squareSizeFloat = pointsInSquare * pointSizeFloat;
        this.vertexArray = new float[maxBathSize * squareSizeFloat];
        this.vertexArrayBytes = vertexArray.length * Float.BYTES;

        this.freeSlots = new ArrayList<>();
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
        attributes.add(new Attribute("gameObjID", 1));

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

        if(spriteRenderer.isMarkedToRemove()){
            for(int i = 0; i < squareSizeFloat; i++){
                int offset = index * squareSizeFloat;
                vertexArray[offset + i] = 0;
            }
            spriteRenderer.unmarkToRemove();
            spriteListToRender[index] = null;

           if(spriteRenderer.isMarkToRelocate()){
               Renderer.getInstance().addToRelocateList(spriteRenderer);
               spriteRenderer.unMarkToRelocate(false);
           }else{
               spriteRenderer.getParent().removeComponent(spriteRenderer);
           }

            freeSlots.add(index);
        }else {
            Vector2f[] texCords = spriteRenderer.getSpriteCords();
            Vector4f color = spriteRenderer.getColor();
            Vector2f position = transform.getPosition();
            Vector2f scale = transform.getScale();
            boolean isRotated = transform.getRotation() != 0;
            int offset = index * squareSizeFloat;
            float xAdd = 0.5f;
            float yAdd = 0.5f;

            for (int j = 0; j < 4; j++) {
                if (j == 1) yAdd = -0.5f;
                if (j == 2) xAdd = -0.5f;
                if (j == 3) yAdd =  0.5f;

                Vector4f currentPos = new Vector4f(position.x + (xAdd * scale.x), position.y + (yAdd * scale.y), 0, 0);
                if (isRotated) {
                    Matrix4f transformMatrix = new Matrix4f().identity();
                    transformMatrix.translate(position.x, position.y, 1f);
                    transformMatrix.rotate((float) Math.toRadians(transform.getRotation()), 0, 0, 1);
                    transformMatrix.scale(scale.x, scale.y, 0);
                    currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
                }

                vertexArray[offset + 0] = currentPos.x;
                vertexArray[offset + 1] = currentPos.y;

                vertexArray[offset + 2] = color.x;
                vertexArray[offset + 3] = color.y;
                vertexArray[offset + 4] = color.z;
                vertexArray[offset + 5] = color.w;

                vertexArray[offset + 6] = texCords[j].x;
                vertexArray[offset + 7] = texCords[j].y;

                vertexArray[offset + 8] = spriteRenderer.getSpriteID();

                vertexArray[offset + 9] = spriteRenderer.getParent().getGameObjectID();

                offset += pointSizeFloat;
            }
        }
    }

    public void addSprite(SpriteRenderer spriteRenderer){
        int index = spriteCount;
        boolean usedFreeSlots = false;
        if(!freeSlots.isEmpty()) {
            index = freeSlots.get(0);
            freeSlots.remove(0);
            usedFreeSlots = true;
        }
        spriteListToRender[index] = spriteRenderer;
        if(spriteCount + 1 >= maxBathSize) hasRoom = false;

        if(spriteRenderer.hasTexture() ) {
            if(!textureList.contains(spriteRenderer.getTexture())){
                int id = textureList.size() + 1;
                spriteRenderer.setSpriteID(id);
                textureList.add(spriteRenderer.getTexture());
            }else {
                if(spriteRenderer.isIDDefault()) {
                    for (int i = 0; i < textureList.size(); i++) {
                        Texture texture = textureList.get(i);
                        if (texture.equals(spriteRenderer.getTexture())) {
                            spriteRenderer.setSpriteID(i + 1);
                        }
                    }
                }
            }
        }

        loadVertexArray(index);
       if(!usedFreeSlots) spriteCount++;
      //  printPointsValues();
    }

    public void render(){
        Camera camera = Window.getInstance().getCurrentScene().getCamera();
        Shader shader = Renderer.getInstance().getShader();
        reloadIfDirty();

        shader.use();
        shader.uploadValue("uProjection", camera.getUProjection());
        shader.uploadValue("uView", camera.getUView());
        shader.uploadValue("uTextures", uTextures);

        activeAndBindTextures();
        glBindVertexArray(VAO);
        enableAttribArrays();

        glDrawElements(GL_TRIANGLES, spriteCount * pointsIn2Triangles, GL_UNSIGNED_INT, 0);

        unbindTextures();
        disableAttribArrays();
        glBindVertexArray(0);
        shader.detach();
    }

    public void reloadIfDirty(){
        boolean reload = false;
        for(int i = 0; i < spriteCount; i++){
            SpriteRenderer spriteRenderer = spriteListToRender[i];
            if(spriteRenderer != null && (spriteRenderer.isDirty() || spriteRenderer.isMarkedToRemove())){
                loadVertexArray(i);
                spriteRenderer.setClean();
                reload = true;
            }
        }

        if(reload){
            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, spriteCount * squareSizeFloat));
        }
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

    public int getzIndex(){
        return zIndex;
    }

    public List<Integer> getFreeSlots(){
        return freeSlots;
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
        System.out.println("**** " + spriteCount + " **** [" + zIndex + "]");
        for(int i = 1; i < ((spriteCount + 1) * pointSizeFloat * pointsInSquare) + 1; i++){
            System.out.printf("%.2f \t", vertexArray[i - 1]);
            if(i % pointSizeFloat == 0) System.out.println();
            if(i % squareSizeFloat == 0) System.out.println();
        }
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }

    public List<Texture> getTextureList(){
        return  textureList;
    }
}