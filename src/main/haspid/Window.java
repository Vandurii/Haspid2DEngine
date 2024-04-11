package main.haspid;

import main.scene.EditorScene;
import main.scene.GameScene;
import main.scene.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static main.Configuration.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;

    private static long glfwWindow;
    private static Scene currentScene;

    private Window(){}

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        currentScene.save();
        currentScene.end();
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();;
    }

    private void init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        glfwWindow = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
        if(glfwWindow == NULL)  throw new RuntimeException("Failed to create te GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            setWidth(newWidth);
            setHeight(newHeight);
            glViewport(0, 0, windowWidth,windowHeight);
        });

//        glfwSetWindowSizeCallback(glfwWindow, new GLFWWindowSizeCallback() {
//            @Override
//            public void invoke(long window, int argWidth, int argHeight) {
//                resizeWindow(argWidth, argHeight);
//            }
//        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        changeScene(new EditorScene());
    }

    private void loop(){
        float lastFrameTime = -1;

        // Run the rendering loop until the user has attempted to close the window.
        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll for window events. The key callback above wil only be invoked during this call
            glfwPollEvents();

            // Set the clear color
            glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, clearColor.getAlpha());

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clear the framebuffer

            float beginTime = (float) glfwGetTime();
            float deltaTime = beginTime - lastFrameTime;
            if(lastFrameTime == -1) deltaTime = 1f / 60f;
            lastFrameTime = beginTime;
            //System.out.println(1 / deltaTime + "FPS");
            currentScene.update(deltaTime);

            glfwSwapBuffers(glfwWindow); // swap the color buffers

            if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_1)){
                changeScene(new EditorScene());
            }else if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_2)){
                changeScene(new GameScene());
            }
        }
    }

    public static Window getInstance(){
        if(instance == null) instance = new Window();

        return instance;
    }

    public void changeScene(Scene scene){
        currentScene = scene;
        currentScene.init();
        currentScene.start();
    }

    public Scene getCurrentScene(){
        return currentScene;
    }

    public long getGlfwWindow(){
        return  glfwWindow;
    }

    public void setWidth(int width){
        windowWidth = width;
    }

    public void setHeight(int height){
        windowHeight = height;
    }
}
