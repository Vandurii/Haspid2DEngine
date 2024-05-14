package main;

import main.util.AudioConfig;
import main.util.SpriteConfig;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class Configuration {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    public static int windowWidth = (int)screenSize.getWidth();
//    public static int windowHeight = (int)screenSize.getHeight();

    public static int windowWidth = 1440;
    public static int windowHeight = 810;

    public static float aspectRatio = (float)windowWidth / (float)windowHeight;
    public static String windowTitle = "Haspid";
    public static Vector3f uProjectionDimension = new Vector3f(1280, 672, 100);


    public static String defaultShaderPath = "assets/shaders/default.glsl";
    public static String line2DShaderPath = "assets/shaders/line2D.glsl";
    public static String idShaderPath = "assets/shaders/idShader.glsl";

    public static String marioImagePath = "assets/images/mario.png";
    public static SpriteConfig firstSpriteSheet = new SpriteConfig("assets/images/spritesheet.png", 16, 16, 26, 0);
    public static SpriteConfig decorationAndBlockConfig = new SpriteConfig("assets/images/decorationsAndBlocks.png", 16, 16, 81, 0);
    public static SpriteConfig gizmosConfig = new SpriteConfig("assets/images/gizmos.png", 24, 48, 3, 0);
    public static SpriteConfig itemsConfig = new SpriteConfig("assets/images/items.png", 16, 16, 33, 0);


    public static AudioConfig mainTheme = new AudioConfig("assets/sounds/main-theme-overworld.ogg", true);
    public static AudioConfig breakBlock = new AudioConfig("assets/sounds/flagpole.ogg", false);
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


    public static float skipCamera = 2;
    public static int cameraSensivity = 80;
    public static float resetCameraSpeed = 0.1f;
    public static float debounceForCamera = 0.0000032f;


    public static float zoom = 1f;
    public static float minZoomValue = 0.05f;
    public static float maxZoomValue = 20f;

    public static float scrollSensivity = 0.1f;

    public static Vector4f hoverGizmoColor = new Vector4f(0f, 0.5f, 0f, 1f);
    public static Vector4f gizmoColor = new Vector4f(0.0f, .0f, 0.0f, 1f);
    public static Vector2f gizmoScale = new Vector2f(30, 60);
    public static int xGizmoXAxis = 10;
    public static int xGizmoYAxis = -9;
    public static int xGizmoRotation = 90;

    public static int yGizmoXAxis = -10;
    public static int yGizmoYAxis = 9;
    public static int yGizmoRotation = 180;


    public static int spriteSize = 2;
    public static int gridSize = 32;
    public static int objectSize = 16;


    public static int minimalWidthForGrid = 400;
    public static int maximalWidthForGrid = 4500;
    public static Vector3f gridLinesColor = new Vector3f(0.2f, 0.2f, 0.2f);


    public static float keyDebounceC = 0.09f;

    public static Vector3f colliderColor = new Vector3f(0.0f, 1f, 0.0f);
    public static Vector3f colorRed = new Vector3f(1, 0, 0);
    public static Vector3f colorGreen = new Vector3f(0, 1, 0);
    public static Vector3f colorBlue = new Vector3f(0, 0, 1);
    public static Vector4f colorRedAlpha = new Vector4f(1, 0, 0, 1);
    public static Vector4f colorGreenAlpha = new Vector4f(0, 1, 0, 1);
    public static Vector4f colorBlueAlpha = new Vector4f(0, 0, 1, 1);

    public static Color clearColor = new Color(60, 60, 60, 1);

    public static Vector4f imGuiColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiButtonColor = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiFrameBackground = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiHeader = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTitleBg = new Vector4f(1f, 0f, 0f, 1);
    public static Vector4f imGuiTabInactive = new Vector4f(0.15f, 0.15f, 0.15f, 1);
    public static Vector4f imGuiTabActive = new Vector4f(0.25f, 0.25f, 0.25f, 1);
    public static Vector4f imGuiMenuBar = new Vector4f(0.25f, 0.25f, 0.25f, 1);

    public static Vector4f mouseHoveColor = new Vector4f(0.2f,0.8f,1f,1);
    public static Vector4f mouseRectColor = new Vector4f(0.04f, 0.04f, 0.04f, 0.4f);

    public static int gridLinesIndex = 1;
    public static int colliderIndex = 2;
    public static int selectorIndex = 4;

    public static float selectorScale = 1.20f;

    public static String levelPath = "level.txt";

    public static Vector2f windowsScale = new Vector2f(windowWidth / uProjectionDimension.x, windowHeight /  uProjectionDimension.y);
}


// todo scanObject problem | collider box | physics with scale
// refactor properties window
// selector scale





