package main.haspid;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static main.Configuration.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;

    // The window handle
    private long glfwWindow;

    private Window(){}

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

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
//
//        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//        glfwSetKeyCallback(windowPtr, (windowPtr, key, scancode, action, mods) -> {
//            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(windowPtr, true);
//        });
//
//        // Get the thread stack and push a new frame
//
//        try(MemoryStack stack = stackPush()) {
//            IntBuffer pWidht = stack.mallocInt(1); //int*
//            IntBuffer pHeight = stack.mallocInt(1); //int*
//
//            // Get the window size passed to glfwCreateWindow
//            glfwGetWindowSize(windowPtr, pWidht, pHeight);
//
//            // Get the resolution of the primary monitor
//            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//~~
//            // Center the window
//            glfwSetWindowPos(windowPtr, (vidMode.width() - pWidht.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
//        }// the stack frame is popped automatically

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
    }

    private void loop(){
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while(!glfwWindowShouldClose(glfwWindow)){
            // Set the clear color
            glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, clearColor.getAlpha());

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clear the framebuffer
            glfwSwapBuffers(glfwWindow); // swap the color buffers

            // Poll for window events. The key callback above wil only be invoked during this call
            glfwPollEvents();
        }
    }

    public static Window getInstance(){
        if(instance == null) instance = new Window();

        return instance;
    }
}
