package main.haspid;

import main.renderer.DebugDraw;
import main.renderer.FrameBuffer;
import main.renderer.IDBuffer;
import main.renderer.Renderer;
import main.scene.EditorScene;
import main.scene.GameScene;
import main.scene.Scene;
import main.util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.awt.SystemColor.window;
import static main.Configuration.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;

    private static long glfwWindow;
    private static Scene currentScene;
    private static FrameBuffer frameBuffer;
    private static IDBuffer idBuffer;
    private static Renderer renderer;

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
          //  glViewport(0, 0, windowWidth,windowHeight);
         //   DebugDraw.printValues();
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

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

        renderer = Renderer.getInstance();
        frameBuffer = new FrameBuffer(windowWidth, windowHeight);
        idBuffer = new IDBuffer(windowWidth, windowHeight);
        changeScene(new EditorScene());
    }

    private void loop(){
        float lastFrameTime = -1;

        // Run the rendering loop until the user has attempted to close the window.
        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll for window events. The key callback above wil only be invoked during this call
            glfwPollEvents();

            // delta time
            float beginTime = (float) glfwGetTime();
            float deltaTime = beginTime - lastFrameTime;
            if(lastFrameTime == -1) deltaTime = 1f / 60f;
            lastFrameTime = beginTime;

            // Update current scene
            currentScene.update(deltaTime);

            //Bind id buffer and write to it.
            glDisable(GL_BLEND);
            idBuffer.bind();

            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderer.replaceShader(AssetPool.getShader(idShaderPath));
            currentScene.render(deltaTime, true);

            if(MouseListener.getInstance().isButtonPressed(GLFW_MOUSE_BUTTON_1)){
                int x = (int) MouseListener.getInstance().getScreenX();
                int y = (int) MouseListener.getInstance().getScreenY();
                System.out.println(idBuffer.readIDFromPixel(x, y));
            }

            idBuffer.unbind();
            glEnable(GL_BLEND);

            // Bind frame buffer and write to it.
            frameBuffer.bind();
            renderer.replaceShader(AssetPool.getShader(defaultShaderPath));
            glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, clearColor.getAlpha());

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clear the framebuffer

            getCurrentScene().render(deltaTime, false);
            frameBuffer.unBind();

            // swap the color buffers
            glfwSwapBuffers(glfwWindow);
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

    public FrameBuffer getFrameBuffer(){
        return  frameBuffer;
    }
}
