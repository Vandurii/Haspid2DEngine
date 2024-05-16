package main.util;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL40.glUniformMatrix4dv;

public class Shader {

    private boolean isUsed;
    private int shaderProgramID;
    private final String filePath;
    private String vertexShaderSource, fragmentShaderSource;


    protected Shader(String filePath){
        this.filePath = filePath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filePath).toAbsolutePath()));

            String token = "#type";
            String regex = token +"( )+[a-zA-Z]*";
            String[] split = source.split(regex);

            int sIndex = source.indexOf(token) + token.length();
            int eIndex = source.indexOf("\n");
            String firstAttrib = source.substring(sIndex, eIndex);

            sIndex = source.indexOf(token, eIndex) + token.length();
            eIndex = source.indexOf("\n", sIndex);
            String secondAttrib = source.substring(sIndex, eIndex);

            if("fragmentShader".equals(firstAttrib.trim()) && "vertexShader".equals(secondAttrib.trim())){
                fragmentShaderSource = split[1];
                vertexShaderSource = split[2];
            }else if("fragmentShader".equals(secondAttrib.trim()) && "vertexShader".equals(firstAttrib.trim())){
                fragmentShaderSource = split[2];
                vertexShaderSource = split[1];
            }else{
                throw new IllegalStateException("Unable to load shader, unexpected type: " + token);
            }

        }catch (IOException e){
            e.printStackTrace();
            throw new IllegalStateException("Unable to open shader file: " + filePath);
        }
    }

    public void compile(){
        int vertexShaderID, fragmentShaderID;

        // Compile vertex shader
        vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexShaderSource);
        glCompileShader(vertexShaderID);
        int success = glGetShaderi(vertexShaderID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH);
            System.out.println("Unable to compile vertex shader: " + filePath);
            System.out.println(glGetShaderInfoLog(vertexShaderID, len));
            throw new IllegalStateException();
        }

        // Compile fragment shader
        fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentShaderSource);
        glCompileShader(fragmentShaderID);
        success = glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH);
            System.out.println("Unable to compile fragment shader: " + filePath);
            System.out.println(glGetShaderInfoLog(fragmentShaderID, len));
            throw new IllegalStateException();
        }

        // link shaders and compile program
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexShaderID);
        glAttachShader(shaderProgramID, fragmentShaderID);
        glLinkProgram(shaderProgramID);
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Unable to link shader program: " + filePath);
            System.out.println(glGetShaderInfoLog(shaderProgramID, len));
            throw new IllegalStateException();
        }

        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        vertexShaderSource = null;
        fragmentShaderSource = null;
    }

    public void use(){
        if(!isUsed) {
            glUseProgram(shaderProgramID);
            isUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        isUsed = false;
    }

    public <T> void uploadValue(String varName, T type){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        if(type instanceof Matrix4f) {
            FloatBuffer matBuff = BufferUtils.createFloatBuffer(16);
            ((Matrix4f)type).get(matBuff);
            glUniformMatrix4fv(varLocation, false, matBuff);
        }else if (type instanceof Matrix3f){
            FloatBuffer matBuff = BufferUtils.createFloatBuffer(9);
            ((Matrix3f)type).get(matBuff);
            glUniformMatrix3fv(varLocation, false, matBuff);
        }else if(type instanceof Matrix4d){
            DoubleBuffer matBuff = BufferUtils.createDoubleBuffer(16);
            ((Matrix4d)type).get(matBuff);
            glUniformMatrix4dv(varLocation, false, matBuff);
        }else if(type instanceof Vector4f value){
            glUniform4f(varLocation, value.x, value.y, value.z, value.w);
        }else if(type instanceof Vector3f value){
            glUniform3f(varLocation, value.x, value.y, value.z);
        }else if(type instanceof Vector2f value){
            glUniform2f(varLocation, value.x, value.y);
        }else if(type instanceof Float value){
            glUniform1f(varLocation, value);
        }else if(type instanceof Integer value){
            glUniform1i(varLocation, value);
        }else if(type instanceof int[]){
            int[] value = (int[]) type;
            glUniform1iv(varLocation, value);
        }else{
            throw new IllegalStateException("Unexpected value in shader class uploadValue method. --> type:" + type.getClass().getSimpleName() );
        }
    }

    public String getFilePath(){
        return  filePath;
    }
}
