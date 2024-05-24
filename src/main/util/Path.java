package main.util;

import main.Configuration;

public class Path {
    private static String imagePrefix = Configuration.imagesPrefix;
    private static String sysPrefix = Configuration.systemPrefix;
    private static String shaderPrefix = Configuration.shaderPrefix;
    private static String soundsPrefix = Configuration.soundsPrefix;

    public static String fromImages(String string){
        return imagePrefix + string;
    }

    public static String fromSys(String string){
        return sysPrefix + string;
    }

    public static String fromShaders(String string){
        return shaderPrefix + string;
    }

    public static String fromSounds(String string){
        return soundsPrefix + string;
    }
}
