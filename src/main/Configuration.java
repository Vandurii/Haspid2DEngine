package main;

import main.util.SpriteConfig;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class Configuration {
    public static float aspectRatio = 16f / 9f;
    public static int windowWidth = 1344;
    public static int windowHeight = 756;
    public static String windowTitle = "Haspid";
    public static Vector3f uProjectionDimension = new Vector3f(1280, 672, 100);


    public static String defaultShaderPath = "assets/shaders/default.glsl";
    public static String line2DShaderPath = "assets/shaders/line2D.glsl";
    public static String idShaderPath = "assets/shaders/idShader.glsl";

    public static String marioImagePath = "assets/images/mario.png";
    public static SpriteConfig firstSpriteSheet = new SpriteConfig("assets/images/spritesheet.png", 16, 16, 26, 0);
    public static SpriteConfig decorationAndBlockConfig = new SpriteConfig("assets/images/decorationsAndBlocks.png", 16, 16, 81, 0);


    public static int batchSize = 10000;
    public static int numberOfPointsInSquare = 4;
    public static int numberOfPointsIn2Triangles = 6;
    public static int[]  texturesSlots = {0, 1, 2, 3, 4, 5, 6, 7};


    public static int spriteSize = 2;
    public static int gridSize = 32;


    public static Vector3f colorRed = new Vector3f(1, 0, 0);
    public static Vector3f colorGreen = new Vector3f(0, 1, 0);
    public static Vector3f colorBlue = new Vector3f(0, 0, 1);
    public static Vector4f colorRedAlpha = new Vector4f(1, 0, 0, 1);
    public static Vector4f colorGreenAlpha = new Vector4f(0, 1, 0, 1);
    public static Vector4f colorBlueAlpha = new Vector4f(0, 0, 1, 1);

    public static Vector3f gridLinesColor = new Vector3f(0.2f, 0.2f, 0.2f);

    public static Color clearColor = new Color(60, 60, 60, 1);

    public static Vector4f imGuiColor = new Vector4f(0.15f, 0.15f, 0.15f, 1);

    public static String levelPath = "level.txt";

    public static Vector2f windowsScale = new Vector2f(windowWidth / uProjectionDimension.x, windowHeight /  uProjectionDimension.y);

    public static float skipCamera = 2;
    public static int cameraSensivity = 128;
    public static float resetCameraSpeed = 0.1f;
    public static float debounceForCamera = 0.0000032f;

    public static float zoom = 1f;
    public static float zoomForKeys = 0.1f;
    public static float minZoomValue = 0.05f;
    public static float maxZoomValue = 20f;

    public static float scrollSensivity = 0.1f;

    public static int minimalWidthForGrid = 400;
    public static int maximalWidthForGrid = 4500;

}
