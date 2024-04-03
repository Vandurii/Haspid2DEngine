package main.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private String filePath;
    private String vertexShaderSource, fragmentShaderSource;
    private boolean isUsed;

    public Shader(String filePath){
        this.filePath = filePath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )*([a-zA-Z])*");
            String eol = "\n";
            String pattern = ("#type");
            int length = pattern.length() + 1;

            int startFirst = source.indexOf(pattern) + length;
            String firstPattern = source.substring(startFirst, source.indexOf(eol));

            int startSecond = source.indexOf(pattern, source.indexOf(eol)) + length;
            String secondPattern = source.substring(startSecond, source.indexOf(eol, startSecond));

            if(firstPattern.trim().equals("vertexShader") && secondPattern.trim().equals("fragmentShader")){
                vertexShaderSource = splitString[1];
                fragmentShaderSource = splitString[2];
            }else if(firstPattern.trim().equals("fragmentShader") && secondPattern.trim().equals("vertexShader")){
                fragmentShaderSource = splitString[1];
                vertexShaderSource = splitString[2];
            }else{
                System.out.println("Unexpected token: ");
                System.out.println(firstPattern);
                System.out.println(secondPattern);
            }
        }catch (IOException e){
            System.out.println("Unable to open shader file: " + filePath);
            e.printStackTrace();
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
            throw new IllegalStateException("Unexpected value in shader class uploadValue method");
        }
    }
}
