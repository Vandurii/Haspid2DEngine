package main;

import main.util.AudioConfig;
import main.util.SpriteConfig;
import org.joml.*;

import java.awt.*;

public class Configuration {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    public static int windowWidth = (int)screenSize.getWidth();
//    public static int windowHeight = (int)screenSize.getHeight();

    public static int windowWidth = 1440;
    public static int windowHeight = 810;

    public static float aspectRatio = (float)windowWidth / (float)windowHeight;
    public static String windowTitle = "Haspid";
    public static Vector3d uProjectionDimension = new Vector3d(128, 67.2, 100);


    public static String defaultShaderPath = "assets/shaders/default.glsl";
    public static String line2DShaderPath = "assets/shaders/line2D.glsl";
    public static String idShaderPath = "assets/shaders/idShader.glsl";

    public static int standardSpriteSize = 16;
    public static String marioImagePath = "assets/images/mario.png";
    public static SpriteConfig smallFormConfig = new SpriteConfig("assets/images/smallForm.png", standardSpriteSize, standardSpriteSize, 26, 0);
    public static SpriteConfig decorationAndBlockConfig = new SpriteConfig("assets/images/decorationsAndBlocks.png", standardSpriteSize, standardSpriteSize, 81, 0);
    public static SpriteConfig gizmosConfig = new SpriteConfig("assets/images/gizmos.png", 24, 48, 3, 0);
    public static SpriteConfig itemsConfig = new SpriteConfig("assets/images/items.png", standardSpriteSize, standardSpriteSize, 34, 0);
    public static SpriteConfig bigFormConfig = new SpriteConfig("assets/images/bigForm.png", 16, 32, 42, 0);
    public static SpriteConfig pipesConfig = new SpriteConfig("assets/images/pipes.png", 32, 32, 6, 0);
    public static SpriteConfig turtleConfig = new SpriteConfig("assets/images/turtle.png", 16, 24, 4, 0);// todo
    public static SpriteConfig iconConfig = new SpriteConfig("assets/images/icons.png", 32, 32, 16, 0);// todo

    public static AudioConfig mainTheme = new AudioConfig("assets/sounds/main-theme-overworld.ogg", true);
    public static AudioConfig breakBlock = new AudioConfig("assets/sounds/break_block.ogg", false);
    public static AudioConfig bump = new AudioConfig("assets/sounds/bump.ogg", false);
    public static AudioConfig coin = new AudioConfig("assets/sounds/coin.ogg", false);
    public static AudioConfig gameOver = new AudioConfig("assets/sounds/gameover.ogg", false);
    public static AudioConfig jumpSmall = new AudioConfig("assets/sounds/jump-small.ogg", false);
    public static AudioConfig marioDie = new AudioConfig("assets/sounds/mario_die.ogg", false);
    public static AudioConfig pipe = new AudioConfig("assets/sounds/pipe.ogg", false);
    public static AudioConfig powerUp = new AudioConfig("assets/sounds/powerup.ogg", false);
    public static AudioConfig powerUpAppears = new AudioConfig("assets/sounds/powerup_appears.ogg", false);
    public static AudioConfig stageClear = new AudioConfig("assets/sounds/stage_clear.ogg", false);
    public static AudioConfig stomp = new AudioConfig("assets/sounds/stomp.ogg", false);
    public static AudioConfig kick = new AudioConfig("assets/sounds/kick.ogg", false);
    public static AudioConfig invincible = new AudioConfig("assets/sounds/invincible.ogg", false);


    public static int batchSize = 10000;
    public static int numberOfPointsInSquare = 4;
    public static int numberOfPointsIn2Triangles = 6;
    public static int[]  texturesSlots = {0, 1, 2, 3, 4, 5, 6, 7};
  //  public static int[]  texturesSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};


    public static double skipCamera = 2;
    public static double cameraSensivity = 8.0;
    public static double resetCameraSpeed = 0.1f;
    public static double debounceForCamera = 0.0000032f;


    public static double zoom = 1f;
    public static double minZoomValue = 0.05f;
    public static double maxZoomValue = 20f;

    public static double scrollSensivity = 0.1f;

    public static Vector4f hoverGizmoColor = new Vector4f(0f, 0.5f, 0f, 1f);
    public static Vector4f gizmoColor = new Vector4f(0.0f, .0f, 0.0f, 1f);
    public static Vector2d gizmoScale = new Vector2d(3.0, 6.0);
    public static double xGizmoXAxis = 1.0;
    public static double xGizmoYAxis = -0.9;
    public static double xGizmoRotation = 90;

    public static double yGizmoXAxis = -1.0;
    public static double yGizmoYAxis = 0.9;
    public static double yGizmoRotation = 180;


    public static double gridSize = uProjectionDimension.x / 40;;
    public static double spriteSize = gridSize / standardSpriteSize;
    public static double objectHalfSize = gridSize / 2;


    public static double minimalWidthForGrid = gridSize * 12.5;
    public static double maximalWidthForGrid = gridSize * 140;
    public static Vector3f gridLinesColor = new Vector3f(0.2f, 0.2f, 0.2f);


    public static double keyDebounceC = 0.09f;

    public static Vector3f colliderColor = new Vector3f(0.0f, 1f, 0.0f);
    public static Vector3f colorRed = new Vector3f(1, 0, 0);
    public static Vector3f colorGreen = new Vector3f(0, 1, 0);
    public static Vector3f colorBlue = new Vector3f(0, 0, 1);
    public static Vector4f colorRedAlpha = new Vector4f(1, 0, 0, 1);
    public static Vector4f colorGreenAlpha = new Vector4f(0, 1, 0, 1);
    public static Vector4f colorBlueAlpha = new Vector4f(0, 0, 1, 1);

    public static Color editorClearColor = new Color(60, 60, 60, 1);
    public static Color gameClearColor = new Color(92, 148, 255, 1);
    public static Color currentClearColor = editorClearColor;

    public static Vector4f imGuiColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiButtonColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiFrameBackground = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiHeader = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTitleBg = new Vector4f(1f, 0f, 0f, 1);
    public static Vector4f imGuiTabInactive = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTabActive = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiMenuBar = new Vector4f(0.25f, 0.25f, 0.25f, 1);

    public static Vector4f mouseHoveColor = new Vector4f(0.2f,0.2f,0.2f,0.0f);
    public static Vector4f mouseRectColor = new Vector4f(0.04f, 0.04f, 0.04f, 0.4f);

    public static int gridLinesIndex = 2;
    public static int colliderIndex = 3;
    public static int selectorIndex = 4;

    public static double selectorScale = 0;

    public static double pillboxWidth = (objectHalfSize * 2) * 0.85f;
    public static double pillboxHeight = (objectHalfSize * 2) * 1.43f;

    public static String levelPath = "level.txt";

    public static Vector2d windowsScale = new Vector2d(windowWidth / uProjectionDimension.x, windowHeight /  uProjectionDimension.y);

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


    // Game Camera
    public static double xAxisMargin = 30;
    public static double yAxisMargin = 5;
}








