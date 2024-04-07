package main;

import main.util.SpriteConfig;
import org.joml.Vector3f;

import java.awt.*;

public class Configuration {
    public static int windowWidth = 960;
    public static int windowHeight = 540;
    public static String windowTitle = "Haspid";
    public static Color clearColor = new Color(150, 150, 150, 1);
    //public static Color clearColor = red;

    public static String defaultShaderPath = "assets/shaders/default.glsl";
    public static String marioImagePath = "assets/images/mario.png";
    public static SpriteConfig firstSpriteSheet = new SpriteConfig("assets/images/spritesheet.png", 16, 16, 26, 0);

    public static String  pathToImGuiConfigFile = "dearGui.ini";

    public static String levelPath = "level.txt";

    public static Vector3f uProjectionDimension = new Vector3f(32 * 40, 32 * 21, 100);

    public static int batchSize = 10000;
    public static int numberOfPointsInSquare = 4;
    public static int numberOfPointsIn2Triangles = 6;
    public static int[]  texturesSlots = {0, 1, 2, 3, 4, 5, 6, 7};
}
