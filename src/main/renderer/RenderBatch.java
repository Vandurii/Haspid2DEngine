package main.renderer;

import main.components.SpriteRenderer;
import main.haspid.*;

import main.util.Attribute;
import main.util.Shader;
import main.util.Texture;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

    private boolean skip;

    private int pointSizeFloat;
    private int pointsInSquare;
    private int squareSizeFloat;
    private int pointsIn2Triangles;

    private int zIndex;
    private int maxBathSize;
    private int VAO, VBO, EBO;
    private int currentBathSize;
    private ArrayList<Attribute> attributes;

    private float[] vertexArray;
    private int vertexArrayBytes;

    private int spriteCount;
    private SpriteRenderer[] spriteListToRender;

    private double resizeTime;
    private double updateTime;

    private static List<Texture> textureList = new ArrayList<>();
    private int[] uTextures;

    private ArrayList<Integer> freeSlots;

    public RenderBatch(int maxBathSize, int zIndex){
        Console.addLog(new Log(INFO, "Created new render batch: zIndex:" + zIndex));

        this.zIndex = zIndex;
        this.uTextures = texturesSlots;
        this.initVertexAttribPointer(false);

        this.maxBathSize = maxBathSize;
        this.currentBathSize = startBatchSize;
        this.spriteListToRender = new SpriteRenderer[currentBathSize];

        this.pointsInSquare = numberOfPointsInSquare;
        this.pointsIn2Triangles = numberOfPointsIn2Triangles;
        this.squareSizeFloat = pointsInSquare * pointSizeFloat;
        this.vertexArray = new float[currentBathSize * squareSizeFloat];
        this.vertexArrayBytes = vertexArray.length * Float.BYTES;

        this.freeSlots = new ArrayList<>();
    }

    public void init(){
        // Generate VAO
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        //Generate VBO
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArrayBytes, GL_DYNAMIC_DRAW);

        initVertexAttribPointer(true);

        //Generate EBO
        EBO = glGenBuffers();
        resize();
    }

    public void resize(){
        resizeTime = glfwGetTime();
        this.vertexArrayBytes = vertexArray.length * Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexArrayBytes, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        int[] elementArray = generateIndices();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArray, GL_STATIC_DRAW);
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
        int[] elements = new int[currentBathSize * pointsIn2Triangles];
        for(int i = 0; i < currentBathSize; i++){
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

    public void loadDataToVertex(int index){
        SpriteRenderer spriteRenderer = spriteListToRender[index];
        Transform transform = spriteRenderer.getParent().getTransform();

        if(spriteRenderer.isMarkedToRemove()){
            for(int i = 0; i < squareSizeFloat; i++){
                int offset = index * squareSizeFloat;
                vertexArray[offset + i] = 0;
            }
            spriteRenderer.unMarkToRemove();
            spriteListToRender[index] = null;

           if(spriteRenderer.isMarkToRelocate()){
               Renderer.getInstance().addToRelocateList(spriteRenderer);
               spriteRenderer.unMarkToRelocate(false);
           }else{
               spriteRenderer.getParent().removeComponent(spriteRenderer);
           }

            freeSlots.add(index);
        }else {
            Vector2d[] texCords = spriteRenderer.getSpriteCords();
            Vector4f color = spriteRenderer.getColor();
            Vector2d position = transform.getPosition();
            Vector2d scale = transform.getScale();
            boolean isRotated = transform.getRotation() != 0;
            int offset = index * squareSizeFloat;
            float xAdd = 0.5f;
            float yAdd = 0.5f;

            for (int j = 0; j < 4; j++) {
                if (j == 1) yAdd = -0.5f;
                if (j == 2) xAdd = -0.5f;
                if (j == 3) yAdd =  0.5f;

                Vector4d currentPos = new Vector4d(position.x + (xAdd * scale.x), position.y + (yAdd * scale.y), 0, 0);
                if (isRotated) {
                    Matrix4f transformMatrix = new Matrix4f().identity();
                    transformMatrix.translate((float) position.x, (float) position.y, 1f);
                    transformMatrix.rotate((float) Math.toRadians(transform.getRotation()), 0, 0, 1);
                    transformMatrix.scale((float) scale.x, (float) scale.y, 0);
                    currentPos = new Vector4d(xAdd, yAdd, 0, 1).mul(transformMatrix);
                }

                vertexArray[offset + 0] = (float)currentPos.x;
                vertexArray[offset + 1] = (float) currentPos.y;

                vertexArray[offset + 2] = color.x;
                vertexArray[offset + 3] = color.y;
                vertexArray[offset + 4] = color.z;
                vertexArray[offset + 5] = color.w;

                vertexArray[offset + 6] = (float) texCords[j].x;
                vertexArray[offset + 7] = (float) texCords[j].y;

                vertexArray[offset + 8] = spriteRenderer.getTextureSlotInRender();

                vertexArray[offset + 9] = spriteRenderer.getParent().getGameObjectID();

                offset += pointSizeFloat;
            }
        }
    }

    public void addSprite(SpriteRenderer spriteRenderer){
        // check if there is space for new sprite if not then extend the array
        if(spriteCount >= spriteListToRender.length){
            spriteListToRender = extendArray(spriteListToRender, percentageValByExtend);
            currentBathSize = spriteListToRender.length;
            extendVertexArray();
            resize();
        }

        // check if there are free slots to reuse
        int index = spriteCount;
        boolean usedFreeSlots = false;
        if(!freeSlots.isEmpty()) {
            index = freeSlots.get(0);
            freeSlots.remove(0);
            usedFreeSlots = true;
        }

        // Add new sprite to renderList
        spriteListToRender[index] = spriteRenderer;

        // Init texture info if there is a texture.
        initTextureInfo(spriteRenderer);

        // Load date to vertex array.
        loadDataToVertex(index);

        // Increase sprite count if we didn't use free slot
       if(!usedFreeSlots) spriteCount++;
    }

    public static void initTextureInfo(SpriteRenderer spriteRenderer){
        if(spriteRenderer.hasTexture()) {
            // If render contain texture from this object, find it in render get index from it  and set to the object.
            // If doesn't contain then add to it to render then set index from it to the object.
            if(!textureList.contains(spriteRenderer.getTexture())){
                int id = textureList.size() + 1;
                spriteRenderer.setTextureSlotInRender(id);
                textureList.add(spriteRenderer.getTexture());
            }else {
                for (int i = 0; i < textureList.size(); i++) {
                    Texture texture = textureList.get(i);
                    if (texture.equals(spriteRenderer.getTexture())) {
                        spriteRenderer.setTextureSlotInRender(i + 1);
                    }
                }
            }
        }
    }


    public  SpriteRenderer[] extendArray(SpriteRenderer[] array, int percentage){
        int capacity = array.length + Math.max((array.length / 100 * percentage), 200);

        SpriteRenderer[] newArray = new SpriteRenderer[capacity];
        System.arraycopy(array, 0, newArray, 0, array.length);

        return newArray;
    }

    public void extendVertexArray(){
        float[] temporary = vertexArray;
        vertexArray = new float[currentBathSize * squareSizeFloat];
        System.arraycopy(temporary, 0, vertexArray, 0, temporary.length);
    }

    public void render(){
        if(!skip) {
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
    }

    public void reloadIfDirty(){
        boolean reload = false;
        for(int i = 0; i < spriteCount; i++){
            SpriteRenderer spriteRenderer = spriteListToRender[i];
            if(spriteRenderer != null && (spriteRenderer.isDirty() || spriteRenderer.isMarkedToRemove())){
                loadDataToVertex(i);
                spriteRenderer.setClean();
                reload = true;
            }
        }

        if(reload){
            updateTime = glfwGetTime();
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

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }

    public int getzIndex(){
        return zIndex;
    }

    public int getSpriteCount(){
        return  spriteCount;
    }

    public boolean hasRoom(){
        return spriteCount < maxBathSize;
    }

    public boolean hasTexture(Texture texture){
        return textureList.contains(texture);
    }

    public boolean hasTextureListRoom(){
        return textureList.size() < 7;
    }

    public int getMaxBathSize(){
        return maxBathSize;
    }

    public int getCurrentCapacity(){
        return currentBathSize;
    }

    public SpriteRenderer[] getSpriteToRender(){
        return spriteListToRender;
    }

    public static List<Texture> getTextureList(){
        return  textureList;
    }

    public double getResizeTime(){
        return resizeTime;
    }

    public double getUpdateTime(){
        return updateTime;
    }

    public int getPointSizeFloat(){
        return pointSizeFloat;
    }

    public float[] getVertexArray(){
        return vertexArray;
    }

    public void setSkip(boolean skip){
        this.skip = skip;
    }
}