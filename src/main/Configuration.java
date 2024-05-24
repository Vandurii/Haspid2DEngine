package main;

import main.util.AudioConfig;
import main.util.Path;
import main.util.SpriteConfig;
import org.joml.*;

import java.awt.Color;

public class Configuration {
    //******************************************************************************************************************
    //                                                === RESOURCES ===
    //******************************************************************************************************************

    //=====================
    // Paths
    //=====================
    public static String imagesPrefix = "assets/images/";
    public static String systemPrefix = imagesPrefix + "sys/";
    public static String shaderPrefix = "assets/shaders/";
    public static String soundsPrefix = "assets/sounds/";
    public static String levelPath = "level.txt";



    //=====================
    // Images
    //=====================
    public static String iconPath = Path.fromSys("haspidIcon.png");
    public static String marioImagePath = Path.fromImages("mario.png");



    //=====================
    // Shaders
    //=====================
    public static String defaultShaderPath = Path.fromShaders("default.glsl");
    public static String line2DShaderPath = Path.fromShaders("line2D.glsl");
    public static String idShaderPath = Path.fromShaders("idShader.glsl");



    //=====================
    // Sprite Sheet Config
    //=====================
    public static int standardSpriteSize = 16;
    public static SpriteConfig smallFormConfig = new SpriteConfig(Path.fromImages("smallForm.png"), standardSpriteSize, standardSpriteSize, 26, 0, true);
    public static SpriteConfig decorationAndBlockConfig = new SpriteConfig(Path.fromImages("decorationsAndBlocks.png"), standardSpriteSize, standardSpriteSize, 81, 0, true);
    public static SpriteConfig gizmosConfig = new SpriteConfig(Path.fromImages("gizmos.png"), 24, 48, 3, 0, true);
    public static SpriteConfig itemsConfig = new SpriteConfig(Path.fromImages("items.png"), standardSpriteSize, standardSpriteSize, 34, 0, true);
    public static SpriteConfig bigFormConfig = new SpriteConfig(Path.fromImages("bigForm.png"), 16, 32, 42, 0, true);
    public static SpriteConfig pipesConfig = new SpriteConfig(Path.fromImages("pipes.png"), 32, 32, 6, 0, true);
    public static SpriteConfig turtleConfig = new SpriteConfig(Path.fromImages("turtle.png"), 16, 24, 4, 0, true);// todo
    public static SpriteConfig iconConfig = new SpriteConfig(Path.fromImages("icons.png"), 32, 32, 16, 0, true);// todo



    //=====================
    // Audio Config
    //=====================
    public static AudioConfig mainTheme = new AudioConfig(Path.fromSounds("mainThemeOverworld.ogg"), true);
    public static AudioConfig breakBlock = new AudioConfig(Path.fromSounds("breakBlock.ogg"), false);
    public static AudioConfig bump = new AudioConfig(Path.fromSounds("bump.ogg"), false);
    public static AudioConfig coin = new AudioConfig(Path.fromSounds("coin.ogg"), false);
    public static AudioConfig gameOver = new AudioConfig(Path.fromSounds("gameOver.ogg"), false);
    public static AudioConfig jumpSmall = new AudioConfig(Path.fromSounds("jumpSmall.ogg"), false);
    public static AudioConfig marioDie = new AudioConfig(Path.fromSounds("marioDie.ogg"), false);
    public static AudioConfig pipe = new AudioConfig(Path.fromSounds("pipe.ogg"), false);
    public static AudioConfig powerUp = new AudioConfig(Path.fromSounds("powerUp.ogg"), false);
    public static AudioConfig powerUpAppears = new AudioConfig(Path.fromSounds("powerUpAppears.ogg"), false);
    public static AudioConfig stageClear = new AudioConfig(Path.fromSounds("stageClear.ogg"), false);
    public static AudioConfig stomp = new AudioConfig(Path.fromSounds("stomp.ogg"), false);
    public static AudioConfig kick = new AudioConfig(Path.fromSounds("kick.ogg"), false);
    public static AudioConfig invincible = new AudioConfig(Path.fromSounds("invincible.ogg"), false);



    //=====================
    // Colors
    //=====================
    public static Vector3f colorRed = new Vector3f(1, 0, 0);
    public static Vector3f colorGreen = new Vector3f(0, 1, 0);
    public static Vector3f colorBlue = new Vector3f(0, 0, 1);
    public static Vector4f colorRedAlpha = new Vector4f(1, 0, 0, 1);
    public static Vector4f colorGreenAlpha = new Vector4f(0, 1, 0, 1);
    public static Vector4f colorBlueAlpha = new Vector4f(0, 0, 1, 1);



    //******************************************************************************************************************
    //                                         === Window && Projection ===
    //******************************************************************************************************************

    //=====================
    // Window Settings
    //=====================
    public static int windowWidth = 1440;
    public static int windowHeight = 810;
    public static String windowTitle = "Haspid";
    public static float aspectRatio = (float)windowWidth / (float)windowHeight;



    //=====================
    // Projection
    //=====================
    public static Vector3d uProjectionDimension = new Vector3d(128, 67.2, 100);
    public static Vector2d windowsScale = new Vector2d(windowWidth / uProjectionDimension.x, windowHeight /  uProjectionDimension.y);



    //******************************************************************************************************************
    //                                              === EDITOR ===
    //******************************************************************************************************************

    //=====================
    // Editor Scene Settings
    //=====================
    public static Color editorClearColor = new Color(60, 60, 60, 1);
    public static double keyDebounceC = 0.1f;



    //=====================
    // Gizmo Settings
    //=====================
    public static Vector4f hoverGizmoColor = new Vector4f(0f, 0.5f, 0f, 1f);
    public static Vector4f gizmoColor = new Vector4f(0.0f, .0f, 0.0f, 1f);
    public static Vector2d gizmoScale = new Vector2d(3.0, 6.0);

    public static double xGizmoXAxis = 1.0;
    public static double xGizmoYAxis = -0.9;
    public static double xGizmoRotation = 90;

    public static double yGizmoXAxis = -1.0;
    public static double yGizmoYAxis = 0.9;
    public static double yGizmoRotation = 180;



    //=====================
    // Grid Settings
    //=====================
    public static int gridLinesZIndex = 2;
    public static double gridSize = uProjectionDimension.x / 40;;
    public static double minimalWidthForGrid = gridSize * 12.5;
    public static double maximalWidthForGrid = gridSize * 140;
    public static Vector3f gridLinesColor = new Vector3f(0.2f, 0.2f, 0.2f);



    //=====================
    // Selector Settings
    //=====================
    public static int selectorZIndex = 4;
    public static double selectorScale = 0;
    public static Vector4f selectorHoverColor = new Vector4f(0.2f,0.2f,0.2f,0.0f);
    public static Vector4f selectorBorderColor = new Vector4f(0.04f, 0.04f, 0.04f, 0.4f);



    //=====================
    // Editor Camera
    //=====================
    public static double skipCamera = 2;
    public static double cameraSensivity = 8.0;
    public static double resetCameraSpeed = 0.1f;
    public static double debounceForCamera = 0.0000032f;

    public static double zoom = 1f;
    public static double minZoomValue = 0.05f;
    public static double maxZoomValue = 20f;

    public static double scrollSensivity = 0.1f;



    //******************************************************************************************************************
    //                                                === GAME ===
    //******************************************************************************************************************

    //=====================
    // Game Scene Settings
    //=====================
    public static Color gameClearColor = new Color(92, 148, 255, 1);



    //=====================
    // Game Camera
    //=====================
    public static double xAxisMargin = 30;
    public static double yAxisMargin = 30;



    //=====================
    // Collider
    //=====================
    public static int colliderZIndex = 3;
    public static double pillboxWidth = 0;
    public static double pillboxHeight = 0;
    public static Vector3f colliderColor = new Vector3f(0.0f, 1f, 0.0f);



    //=====================
    // Rendering Settings
    //=====================
    public static int batchSize = 10000;
    public static int numberOfPointsInSquare = 4;
    public static int numberOfPointsIn2Triangles = 6;
    public static int[]  texturesSlots = {0, 1, 2, 3, 4, 5, 6, 7};



    //******************************************************************************************************************
    //                                              === ENGINE ===
    //******************************************************************************************************************

    //=====================
    // Engine Settings
    //=====================
    public static Color currentClearColor = editorClearColor;
    public static double spriteSize = gridSize / standardSpriteSize;
    public static double objectHalfSize = gridSize / 2;



    //******************************************************************************************************************
    //                                                === GUI ===
    //******************************************************************************************************************

    //=====================
    // Dear Gui Settings
    //=====================
    public static Vector4f imGuiColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiButtonColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiFrameBackground = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiHeader = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTitleBg = new Vector4f(1f, 0f, 0f, 1);
    public static Vector4f imGuiTabInactive = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTabActive = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiMenuBar = new Vector4f(0.25f, 0.25f, 0.25f, 1);



    //=====================
    // Main Menu Bar
    //=====================
    public static float menuBarHeight = 9;
    public static float menuBarButtonSpacing = 2;
    public static float menuBarButtonSize = menuBarHeight * 3;

    public static Color exitNormal = new Color(0.20f, 0.20f, 0.20f, 1f);
    public static Color exitHover = new Color(0.9f, 0.2f, 0.2f, 1.0f);
    public static Color exitActive = new Color(0.8f, 0.1f, 0.15f, 1.0f);

    public static Color maximizeNormal = new Color(0.20f, 0.20f, 0.20f, 1f);
    public static Color maximizeHover = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    public static Color maximizeActive = new Color(0.2f, 0.7f, 0.2f, 1.0f);

    public static Color minimizeNormal = new Color(0.20f, 0.20f, 0.20f, 1f);
    public static Color minimizeHover = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    public static Color minimizeActive = new Color(0.2f, 0.7f, 0.2f, 1.0f);

    public static int iconScale = 11;
}








