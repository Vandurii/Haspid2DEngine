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
    public static SpriteConfig smallFormConfig = new SpriteConfig(Path.fromImages("smallForm.png"), standardSpriteSize, standardSpriteSize, 26, 0, "smallForm", true);
    public static SpriteConfig decorationAndBlockConfig = new SpriteConfig(Path.fromImages("decorationsAndBlocks.png"), standardSpriteSize, standardSpriteSize, 81, 0, "blocks", true);
    public static SpriteConfig gizmosConfig = new SpriteConfig(Path.fromImages("gizmos.png"), 24, 48, 3, 0, "gizmo", true);
    public static SpriteConfig itemsConfig = new SpriteConfig(Path.fromImages("items.png"), standardSpriteSize, standardSpriteSize, 34, 0, "items", true);
    public static SpriteConfig bigFormConfig = new SpriteConfig(Path.fromImages("bigForm.png"), 16, 32, 42, 0, "bigForm", true);
    public static SpriteConfig pipesConfig = new SpriteConfig(Path.fromImages("pipes.png"), 32, 32, 6, 0, "pipes", true);
    public static SpriteConfig turtleConfig = new SpriteConfig(Path.fromImages("turtle.png"), 16, 24, 4, 0, "turtle",true);// todo
    public static SpriteConfig iconConfig = new SpriteConfig(Path.fromImages("icons.png"), 32, 32, 16, 0, "icons", true);// todo



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

    public static Vector4f colorRedA = new Vector4f(1, 0, 0, 1);
    public static Vector4f colorLightRedA = new Vector4f(1f, 0.5f, 0.5f, 1f);
    public static Vector4f colorGreenA = new Vector4f(0, 1, 0, 1);
    public static Vector4f colorLightGreenA = new Vector4f(0.6f, 0.85f, 0.5f, 1f);
    public static Vector4f colorBlueA = new Vector4f(0, 0, 1, 1);
    public static Vector4f colorCyanA = new Vector4f(0f, 0.9f, 0.9f, 1f);
    public static Vector4f colorGreyA = new Vector4f(0.7f, 0.7f, 0.7f, 1f);
    public static Vector4f colorOrangeA = new Vector4f(0.9f, 0.7f, 0.1f, 1f);

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

    public static int shiftXAxis = 1750;
    public static int shiftYAxis = 50;



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
    public static double keyDebounceC = 0.01f;
    public static double keyLongCooldown = 0.01;
    public static boolean isConsoleEnabled = true;
    public static double consoleDelay = 5;
    public static String consoleIntro = "\n" +
            "\n" +
            "\n" +
            "================================\n" +
            "      *** CONSOLE INFO ***\n" +
            "================================";



    //=====================
    // Mouse Control
    //=====================
    public static double placeObjectCooldown = 0.03;
    public static int pixOffsetByCheckingSquare = 2;



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
    // Debug Draw
    //=====================
    public static int maxLineWidth = 2;
    public static int lineWidthScala = 2;
    public static int pointsInLine = 2;
    public static int pointSizeFloat = 6;
    public static int debugDefaultZIndex = 0;
    public static String rayCastID = "rayCast";
    public static int dynamicLayerInitialBatchSize = 10;
    public static int lineSizeFloat = pointsInLine * pointSizeFloat;
    public static Vector3f debugDefaultColor = new Vector3f(0, 1, 0);



    //=====================
    // Grid Settings
    //=====================
    public static String gridID = "grid";
    public static int gridLinesZIndex = 2;
    public static double gridSize = uProjectionDimension.x / 40;;
    public static double minimalViewPortWidthForGrid = 400;
    public static double maximalProjectionWidthForGrid = gridSize * 140;
    public static Vector3f gridLinesColor = new Vector3f(0.2f, 0.2f, 0.2f);



    //=====================
    // Selector Settings
    //=====================
    public static String selectorID = "selector";
    public static int selectorZIndex = 4;
    public static double selectorScale = 0;
    public static Vector4f selectorHoverColor = new Vector4f(0.2f,0.2f,0.2f,0.0f);
    public static Vector4f selectorBorderColor = new Vector4f(0.04f, 0.04f, 0.04f, 0.4f);



    //=====================
    // Editor Camera
    //=====================
    public static double equalizeCameraBy = 2;
    public static double cameraSensitivity = 5;
    public static double resetCameraPosSpeed = 0.1;
    public static double resetCameraZoomSpeed = 0.3;

    public static double currentZoomValue = 1;
    public static double equalizeZoomBy = 0.1;
    public static double minZoomValue = 0.05;
    public static double maxZoomValue = 20;

    public static double scrollSensitivity = 0.1;



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
    public static String colliderID = "collider";
    public static Vector3f colliderColor = new Vector3f(1.0f, 1f, 1.0f);



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
    // Theme Color
    //=====================
    public static float alpha = 1;
    public static float zeroColVal = 0;
    public static float firstColVal = 0.15f;
    public static float secondColVal = 0.10f;
    public static float thirdColVal = 0.20f;
    public static float fourthColVal = 0.30f;
    public static float sixthColVal = 1;

    public static Color white = new Color(0.2f, 0.7f, 0.2f, alpha);



    //=====================
    // Dear Gui Settings
    //=====================

    public static Vector4f imGuiColor = new Vector4f(firstColVal, firstColVal, firstColVal, alpha);
    public static Vector4f imGuiFrameBackground = new Vector4f(secondColVal, secondColVal, secondColVal, alpha);

    public static Vector4f imGuiButtonColor = new Vector4f(thirdColVal, thirdColVal, thirdColVal, alpha);
    public static Vector4f imGuiButtonHovColor = new Vector4f(sixthColVal, sixthColVal, sixthColVal, alpha);
    public static Vector4f imGuiButtonActiveColor = new Vector4f(zeroColVal, zeroColVal, zeroColVal, alpha);

    public static Vector4f imGuiHeader = new Vector4f(secondColVal, secondColVal, secondColVal, alpha);
    public static Vector4f imGuiHeaderHov = new Vector4f(fourthColVal, fourthColVal, fourthColVal, alpha);
    public static Vector4f imGuiHeaderActive = new Vector4f(zeroColVal, zeroColVal, zeroColVal, alpha);

    public static Vector4f imGuiTabColor = new Vector4f(fourthColVal, fourthColVal, fourthColVal, alpha);
    public static Vector4f imGuiTabHovColor = new Vector4f(secondColVal, secondColVal, secondColVal, alpha);
    public static Vector4f imGuiTabActive = new Vector4f(firstColVal, firstColVal, firstColVal, alpha);
    public static Vector4f imGuiTabUnfocusedActive = new Vector4f(thirdColVal, thirdColVal, thirdColVal, alpha);



    //=====================
    // Inspector
    //=====================
    public static int inspectorXSpacing = 3;
    public static int inspectorYSpacing = 2;
    public static int vectorButtonWidth = 100;

    public static String firstButtonName = "X";
    public static Color firstButNormal = new Color(0.8f, 0.1f, 0.15f, 1.0f);
    public static Color firstButHover = new Color(0.9f, 0.2f, 0.2f, 1.0f);
    public static Color firstButActive = new Color(0.8f, 0.1f, 0.15f, 1.0f);

    public static String secondButtonName = "Y";
    public static Color secondButNormal = new Color(0.2f, 0.7f, 0.2f, 1.0f);
    public static Color secondButHover = new Color(0.3f, 0.8f, 0.3f, 1.0f);
    public static Color secondButActive = new Color(0.2f, 0.7f, 0.2f, 1.0f);

    public static String thirdButtonName = "Z";
    public static Color thirdButNormal = new Color(0.1f, 0.25f, 0.8f, 1.0f);
    public static Color thirdButHover = new Color(0.2f, 0.35f, 0.9f, 1.0f);
    public static Color thirdButActive = new Color(0.1f, 0.25f, 0.8f, 1.0f);

    public static String fourthButtonName = "W";
    public static Color fourthButNormal = new Color(0.2f, 0.1f, 0.3f, 1.0f);
    public static Color fourthButHover = new Color(0.4f, 0.3f, 0.5f, 1.0f);
    public static Color fourthButActive = new Color(0.2f, 0.1f, 0.3f, 1.0f);



    //=====================
    // Main Menu Bar
    //=====================
    public static float menuBarHeight = 9;
    public static float menuBarButtonSpacing = 2;
    public static float menuBarButtonSize = menuBarHeight * 3;

    public static Color exitNormal = new Color(thirdColVal, thirdColVal, thirdColVal, alpha);
    public static Color exitHover = new Color(0.9f, 0.2f, 0.2f, alpha);
    public static Color exitActive = new Color(0.8f, 0.1f, 0.15f, alpha);

    public static Color maximizeNormal = new Color(thirdColVal, thirdColVal, thirdColVal, alpha);
    public static Color maximizeHover = new Color(0.8f, 0.8f, 0.8f, alpha);
    public static Color maximizeActive = white;

    public static Color minimizeNormal = new Color(thirdColVal, thirdColVal, thirdColVal, alpha);
    public static Color minimizeHover = new Color(0.8f, 0.8f, 0.8f, alpha);
    public static Color minimizeActive = white;

    public static int iconScale = 11;
}








