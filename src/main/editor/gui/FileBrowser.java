package main.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;
import imgui.type.ImString;
import main.editor.EditorScene;
import main.util.AssetPool;
import main.util.SpriteConfig;
import main.util.Texture;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private String currentConfig;
    private List<String> errorList;
    private EditorScene editorScene;
    private String firstError = "The field cant be empty.";
    private String secondError = "The width, height and count value can't be 0";

    public FileBrowser(EditorScene editorScene){
        this.name = "";
        this.currentConfig = "";
        this.editorScene = editorScene;
        this.errorList = new ArrayList<>();
        this.root = FileSystemView.getFileSystemView().getHomeDirectory();
        this.selectedPath = root.getName();
        this.currentPath = root;
    }

    public void display() {
        if (display) {
            ImGui.begin("File Browser");

            if(ImGui.checkbox("flip", flipped)) flipped = !flipped;
            spriteCount = fillInt(spriteCount, "Sprite Count");
            spriteWidth = fillInt(spriteWidth, "Sprite Width");
            spriteHeight = fillInt(spriteHeight, "Sprite Height");
            spriteSpacing = fillInt(spriteSpacing, "Sprite Spacing");
            path = fillString(selectedPath, "Path");
            name = fillString(name, "Name");

            File[] files = currentPath.listFiles();
            ImGui.newLine();

            if(createButton(backwardsImagePath, false)){
                File previous = currentPath.getParentFile();
                if (previous != null) {
                    currentPath = currentPath.getParentFile();
                }
            }

            if(createButton(acceptImagePath, false)){
                if(spriteCount == 0 || spriteWidth == 0 || spriteHeight == 0 || path.isEmpty() || name.isEmpty()){
                    if(!errorList.contains(firstError)) errorList.add(firstError);
                    if(!errorList.contains(secondError)) errorList.add(secondError);
                }else{
                    SpriteConfig spriteConfig = new SpriteConfig(path, spriteWidth, spriteHeight, spriteCount, spriteSpacing, name, flipped);

                    SpriteConfig[] configList = editorScene.gsonReader(spriteConfigPath, SpriteConfig[].class);

                    // check if the file contain the config already
                    boolean found = false;
                    for(int i = 0; i < configList.length; i++){
                        if(configList[i].name.equals(spriteConfig.name)){
                            found = true;
                        }
                    }

                    // add to file the config if the file doesn't contain it already
                    if(!found) {
                        SpriteConfig[] table = {spriteConfig};
                        editorScene.saveResources(spriteConfigPath, table);
                    }

                    reset();
                    display = false;
                }
            }

            if(createButton(closeImagePath, false)){
                reset();
                display = false;
            }

            if(ImGui.beginCombo("##configSelector", "Load from File")){
                SpriteConfig[] configList = editorScene.gsonReader(spriteConfigPath, SpriteConfig[].class);
                if(configList != null) {
                    for (int i = 0; i < configList.length; i++) {
                        SpriteConfig currentConfig = configList[i];
                        if (ImGui.button(currentConfig.name)) {
                            spriteCount = currentConfig.numSprites;
                            spriteWidth = currentConfig.spriteWidth;
                            spriteHeight = currentConfig.spriteHeight;
                            spriteSpacing = currentConfig.spacing;
                            name = currentConfig.name;
                            path = currentConfig.filePath;
                            flipped = currentConfig.flip;
                        }
                    }
                }
                ImGui.endCombo();
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

            for(String error: errorList) {
                ImGui.textColored(colorRedA.x, colorRedA.y, colorRedA.z, colorRedA.w, error);
            }

            ImGui.popStyleColor(1);
            ImGui.end();

        }
    }

    public boolean createButton(String filePath, boolean flipped){
        ImGui.sameLine();
        tex = AssetPool.getTexture(filePath, flipped);
        if (ImGui.imageButton(tex.getTexID(), superiorSize, superiorSize)) {
            return true;
        }

        return false;
    }

    public int fillInt(int val, String name){
        ImInt intVal = new ImInt(val);
        ImGui.inputInt(name, intVal);

        return intVal.get();
    }

    public String fillString(String val, String name){
        ImGui.pushID("##" + this.hashCode());
        ImString stringVal = new ImString(val, 256);
        ImGui.inputText(name, stringVal);

        ImGui.popID();
        return stringVal.get();
    }

    public void setDisplay(boolean display){
        this.display = display;
    }

    public void reset(){
        path = "";
        name = "";
        spriteCount = 0;
        spriteWidth = 0;
        spriteHeight = 0;
        spriteSpacing = 0;
        errorList.clear();
    }
}
