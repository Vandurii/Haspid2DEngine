package main.haspid;

import main.physics.events.EventSystem;
import main.physics.events.Observer;
import main.physics.events.Event;
import main.renderer.FrameBuffer;
import main.renderer.IDBuffer;
import main.renderer.Renderer;
import main.editor.EditorScene;
import main.game.GameScene;
import main.util.AssetPool;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static main.Configuration.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static Window instance;

    private long glfwWindow;
    private long audioContext;
    private long audioDevice;
    private Scene currentScene;
    private Scene newScene;
    private FrameBuffer frameBuffer;
    private IDBuffer idBuffer;
    private Renderer renderer;

    private Window(){
        EventSystem.addObserver(this);
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        if(currentScene instanceof EditorScene) currentScene.saveSceneObject();
        currentScene.disposeDearGui();

        // Free audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // Create the window
        glfwWindow = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
        if(glfwWindow == NULL)  throw new RuntimeException("Failed to create te GLFW window");

        // icon
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer imageByteBuffer = stbi_load(iconPath, width, height, channels, 4);

        GLFWImage icon = GLFWImage.malloc();
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
        icon.set(width.get(), height.get(), imageByteBuffer);
        imageBuffer.put(0, icon);

        glfwSetWindowIcon(glfwWindow, imageBuffer);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            setWidth(newWidth);
            setHeight(newHeight);
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
                    (vidmode.width() - pWidth.get(0)) / 2 + shiftXAxis ,
                    (vidmode.height() - pHeight.get(0)) / 2  - shiftYAxis
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Create audio context
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if(!alCapabilities.OpenAL10) throw new IllegalStateException("Unable to initialize audio context.");

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
        reloadScene(new EditorScene());
    }

    private void loop(){
        float lastFrameTime = -1;

        // Run the rendering loop until the user has attempted to close the window.
        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll for window events. The key callback above wil only be invoked during this call
            glfwPollEvents();

            MouseListener.getInstance().startFrame();

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

            idBuffer.unbind();
            glEnable(GL_BLEND);

            // Bind frame buffer and write to it.
            frameBuffer.bind();
            renderer.replaceShader(AssetPool.getShader(defaultShaderPath));
            glClearColor(currentClearColor.getRed() / 255f, currentClearColor.getGreen() / 255f, currentClearColor.getBlue() / 255f, currentClearColor.getAlpha());

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clear the framebuffer

            getCurrentScene().render(deltaTime, false);
            frameBuffer.unBind();

            // swap the color buffers
            glfwSwapBuffers(glfwWindow);

            MouseListener.getInstance().endFrame();

            if(newScene != null){
                currentScene.disposeDearGui();
                reloadScene(newScene);
                newScene = null;
            }
        }
    }

    private void reloadScene(Scene scene){
        currentScene = scene;
        currentScene.init();
        currentScene.start();
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        switch (event.getEventType()){
            case GameEngineStart -> {
                currentScene.destroy();
                currentScene.saveSceneObject();
                newScene = new GameScene();
                currentClearColor = gameClearColor;
            }
            case GameEngineStop -> {
                currentScene.destroy();
                newScene = new EditorScene();
                currentClearColor = editorClearColor;
            }
            case Reload -> {
                currentScene.destroy();
                newScene = new GameScene();
                currentClearColor = gameClearColor;
            }
            case SaveLevel -> currentScene.saveSceneObject();
            case LoadLevel -> currentScene.loadSceneObject();
        }
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

    public IDBuffer getIdBuffer(){
        return idBuffer;
    }

    public static Window getInstance(){
        if(instance == null) instance = new Window();

        return instance;
    }
}
