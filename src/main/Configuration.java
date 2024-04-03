package main;

import java.awt.*;

import static java.awt.Color.red;

public class Configuration {
    public static int windowWidth = 960;
    public static int windowHeight = 540;
    public static String windowTitle = "Haspid";
    public static Color clearColor = new Color(150, 150, 150, 1);
    //public static Color clearColor = red;

    public static String defaultShaderPath = "assets/shaders/default.glsl";
    public static String marioImagePath = "assets/images/mario.png";

    public static float uViewX = 32f * 40;
    public static float uViewY = 32f * 21;
    public static float uViewZ = 100f;

    public static float scale = 100f;

    public static int batchSize = 10000;


}
