package main.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;
import imgui.type.ImString;
import main.editor.JImGui;
import main.util.AssetPool;
import main.util.Texture;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import static main.Configuration.*;

public class FileBrowser {
    private File root;
    private File currentPath;

    private Texture tex;
    private int size = 10;
    private boolean display;
    private String selectedPath;
    private int superiorSize  = 20;

    private String path;
    private String name;
    private boolean flipped;
    private int spriteCount;
    private int spriteWidth;
    private int spriteHeight;
    private int spriteSpacing;


    public FileBrowser(){
        this.root = FileSystemView.getFileSystemView().getHomeDirectory();
        this.currentPath = root;
        this.selectedPath = root.getName();
        this.name = "";
    }

    public void display() {
        if (display) {
            ImGui.begin("File Browser");

            if(ImGui.checkbox("flip", flipped)) flipped = !flipped;
            spriteCount = fillInt(spriteCount, "Sprite Count");
            spriteWidth = fillInt(spriteWidth, "Sprite Width");
            spriteHeight = fillInt(spriteHeight, "Sprite Height");
            spriteSpacing = fillInt(spriteSpacing, "Sprite Spacing");
            path = fillString(selectedPath, "path");
            name = fillString(name, "name");

            File[] files = currentPath.listFiles();
            tex = AssetPool.getTexture(backwardsImagePath, false);
            if (ImGui.imageButton(tex.getTexID(), superiorSize, superiorSize)) {
                File previous = currentPath.getParentFile();
                if (previous != null) {
                    currentPath = currentPath.getParentFile();
                }
            }

            ImGui.sameLine();
            tex = AssetPool.getTexture(acceptImagePath, false);
            if (ImGui.imageButton(tex.getTexID(), superiorSize, superiorSize)) {

            }

            ImGui.sameLine();
            tex = AssetPool.getTexture(closeImagePath, false);
            if (ImGui.imageButton(tex.getTexID(), superiorSize, superiorSize)) {
                display = false;
            }

            ImGui.textColored(colorLightGreenA.x, colorLightGreenA.y, colorLightGreenA.z, colorLightGreenA.w, "Path: " + selectedPath);
            ImGui.pushStyleColor(ImGuiCol.Button, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);
            for (File file : files) {
                if (file.isDirectory()) {
                    tex = AssetPool.getTexture(folderImagePath, false);
                } else if (file.getName().substring(file.getName().lastIndexOf(".")).contains("png")) {
                    tex = AssetPool.getTexture(imageImagePath, false);
                } else {
                    tex = AssetPool.getTexture(documentImagePath, false);
                }

                ImGui.image(tex.getTexID(), size, size);
                ImGui.sameLine();

                if (ImGui.button(file.getName())) {
                    if (file.isDirectory()) {
                        currentPath = file;
                    } else {
                        selectedPath = file.getName();
                    }
                }
            }

            ImGui.popStyleColor(1);
            ImGui.end();

        }
    }

    public int fillInt(int val, String name){
        ImInt intVal = new ImInt(val);
        ImGui.inputInt(name, intVal);

        return intVal.get();
    }

    public String fillString(String val, String name){
        ImString stringVal = new ImString(val);
        ImGui.inputText(name, stringVal);

        return stringVal.get();
    }

    public void setDisplay(boolean display){
        this.display = display;
    }
}
