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
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public class FileDialog {
    private File[] disks;
    private  File[] files;
    private File currentPath;
    private String selectedPath;
    private File selectedDisk;

    private Texture tex;
    private int iconSize;
    private int buttonSize;
    private String editButtonText;

    private String configPath;
    private String configName;
    private boolean flipped;
    private int spriteCount;
    private int spriteWidth;
    private int spriteHeight;
    private int spriteSpacing;

    private String firstError;
    private String secondError;
    private List<String> errorList;

    private boolean editMode;
    private boolean display;
    private EditorScene editorScene;

    public FileDialog(EditorScene editorScene){
        this.iconSize = 10;
        this.buttonSize = 20;
        this.editButtonText = "edit";

        this.errorList = new ArrayList<>();
        this.firstError = "The field can't be empty.";
        this.secondError = "The width, height and count value can't be 0";

        this.configName = "";
        this.selectedPath = "";

        // load available disk and home directory to table
        int diskCount = File.listRoots().length;
        this.disks = new File[diskCount + 1];
        for(int i = 0; i < diskCount; i++){
            disks[i] = File.listRoots()[i];
        }
        this.disks[disks.length - 1] = FileSystemView.getFileSystemView().getHomeDirectory();

        this.currentPath = disks[0];
        this.selectedDisk = disks[0];
        this.files = currentPath.listFiles();

        this.editorScene = editorScene;
    }

    public void display() {
        if (display) {
            files = currentPath.listFiles();

            if(files == null){
                files = selectedDisk.listFiles();
            }

            editButtonText = editMode ? "save" : "edit";

            ImGui.begin("File Browser");

            if(ImGui.checkbox("flip", flipped)) flipped = !flipped;
            spriteCount = fillInt(spriteCount, "Sprite Count");
            spriteWidth = fillInt(spriteWidth, "Sprite Width");
            spriteHeight = fillInt(spriteHeight, "Sprite Height");
            spriteSpacing = fillInt(spriteSpacing, "Sprite Spacing");
            configPath = fillString(selectedPath, "Path");
            configName = fillString(configName, "Name");

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
                            configName = currentConfig.name;
                            selectedPath = currentConfig.filePath;
                            flipped = currentConfig.flip;
                        }
                    }
                }
                ImGui.endCombo();
            }

            ImGui.sameLine();

            if(ImGui.button(editButtonText)){
                editMode = !editMode;
            }

            if(editMode) {
                SpriteConfig[] configList = editorScene.gsonReader(spriteConfigPath, SpriteConfig[].class);
                if (configList != null) {
                    for (int i = 0; i < configList.length; i++) {
                        SpriteConfig currentConfig = configList[i];
                        if(currentConfig != null) {
                            if (ImGui.button("Delete me: " + currentConfig.name)) {
                                SpriteConfig[] sprites = {currentConfig};
                                editorScene.removeResource(spriteConfigPath, sprites);
                            }
                        }
                    }
                }
            }

            ImGui.newLine();

            if(createButton(backwardsImagePath, false)){
                File previous = currentPath.getParentFile();
                if (previous != null) {
                    currentPath = currentPath.getParentFile();
                }
            }

            if(createButton(acceptImagePath, false)){
                if(spriteCount == 0 || spriteWidth == 0 || spriteHeight == 0 || configPath.isEmpty() || configName.isEmpty()){
                    if(!errorList.contains(firstError)) errorList.add(firstError);
                    if(!errorList.contains(secondError)) errorList.add(secondError);
                }else{
                    SpriteConfig spriteConfig = new SpriteConfig(configPath, spriteWidth, spriteHeight, spriteCount, spriteSpacing, configName, flipped);
                    editorScene.addProperties(AssetPool.getSpriteSheet(spriteConfig));

                    System.out.println(spriteConfig);
                    SpriteConfig[] table = {spriteConfig};
                    editorScene.saveResources(resourcePath, table);
                    editorScene.saveResources(spriteConfigPath, table);
                    reset();
                    display = false;
                }
            }

            if(createButton(closeImagePath, false)){
                reset();
                display = false;
            }

            if(ImGui.beginCombo("##diskSelector", selectedDisk.getPath())){
                for(int i = 0; i < disks.length; i++){
                    if(ImGui.button(disks[i].getPath())){
                        selectedDisk = disks[i];
                        currentPath = selectedDisk;
                    }
                }
                ImGui.endCombo();
            }

            ImGui.textColored(colorLightGreenA.x, colorLightGreenA.y, colorLightGreenA.z, colorLightGreenA.w, "Path: " + selectedPath);
            ImGui.pushStyleColor(ImGuiCol.Button, imGuiColor.x, imGuiColor.y, imGuiColor.z, imGuiColor.w);

            for (File file : files) {
                if (file.isDirectory()) {
                    tex = AssetPool.getTexture(folderImagePath, false);
                } else if (file.getName().contains("png") && file.getName().substring(file.getName().lastIndexOf(".")).contains("png")) {
                    tex = AssetPool.getTexture(imageImagePath, false);
                } else {
                    tex = AssetPool.getTexture(documentImagePath, false);
                }

                ImGui.image(tex.getTexID(), iconSize, iconSize);
                ImGui.sameLine();

                if (ImGui.button(file.getName())) {
                    if (file.isDirectory()) {
                        currentPath = file;
                    } else {
                        selectedPath = file.getAbsolutePath();
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
        if (ImGui.imageButton(tex.getTexID(), buttonSize, buttonSize)) {
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
        configPath = "";
        configName = "";
        spriteCount = 0;
        spriteWidth = 0;
        spriteHeight = 0;
        spriteSpacing = 0;
        errorList.clear();
    }
}
