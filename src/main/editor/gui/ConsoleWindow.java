package main.editor.gui;

import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public class ConsoleWindow {
    private static List<String> textList = new ArrayList<>();
    private static String temporary = "";

    public void Display(){
        ImGui.begin("Console");

        if(!textList.isEmpty()) {
            for (String str : textList) {
                ImGui.bullet();
                ImGui.sameLine();
                ImGui.textColored(colorGreyA.x, colorGreyA.y, colorGreyA.z, colorGreyA.w, str);
            }
        }
        ImGui.end();
    }

    public static void setInfo(String text){
        textList.add(text);
    }

    public static void clear(){
        textList.clear();
    }

    public static void collect(String str){
        temporary += str;
    }

    public static void next(){
        setInfo(temporary);
        temporary = "";
    }

    public static void space(){
        setInfo("\n");
    }

}
