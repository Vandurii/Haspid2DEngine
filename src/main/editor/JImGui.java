package main.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.*;

import java.awt.*;

public class JImGui {
    private static int defaultWidth = 80;

    public static <T> Object drawValue(String label, T type, String hashName){
        return drawValue(label, type ,hashName, 0, defaultWidth);
    }

    public static <T> Object drawValue(String label, T type, String hashName, float resetValue, float columnWidth){
        String name = "##" + hashName + label;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        if(type instanceof Vector2f || type instanceof Vector3f || type instanceof Vector4f || type instanceof Vector2d || type instanceof Vector3d || type instanceof Vector4d){
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
            vec(type, resetValue);
            ImGui.popStyleVar();
        }else if(type instanceof Integer){
            int[] valArr = {(int)type};
            ImGui.dragInt(name, valArr, 0.1f);
            pop();
            return valArr[0];
        }else if(type instanceof Float){
            float[] valArr = {(float)type};
            ImGui.dragFloat(name, valArr, 0.1f);
            pop();
            return valArr[0];
        }else if(type instanceof Double){
            double dType = (double) type;
            float[] valArr = {(float)dType};
            ImGui.dragFloat(name, valArr, 0.1f);
            pop();
            return valArr[0];
        }else if(type instanceof String){
            String str  = (String) type;
            ImString outString = new ImString(str, 256);
            ImGui.inputText(name, outString);
            pop();
            return outString.get();
        }
        pop();

        return 0;
    }

    public static void pop(){
        ImGui.columns(1);
        ImGui.popID();
    }

    public static <T> void vec(T values, float resetValue){
        Color normal = new Color(0, 0, 0, 0);
        Color hover = new Color(0, 0, 0, 0);
        Color active = new Color(0, 0, 0, 0);
        String name = "";

        int count = 0;
        if(values instanceof  Vector2f || values instanceof Vector2d) count = 2;
        if(values instanceof  Vector3f || values instanceof Vector3d) count = 3;
        if(values instanceof  Vector4f || values instanceof Vector4d) count = 4;

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - (buttonSize.x * count)) / count;

        for(int i = 0; i < count; i++) {

            if (i == 0) {
                normal = new Color(0.8f, 0.1f, 0.15f, 1.0f);
                hover = new Color(0.9f, 0.2f, 0.2f, 1.0f);
                active = new Color(0.8f, 0.1f, 0.15f, 1.0f);
                name = "X";
            } else if (i == 1) {
                normal = new Color(0.2f, 0.7f, 0.2f, 1.0f);
                hover = new Color(0.3f, 0.8f, 0.3f, 1.0f);
                active = new Color(0.2f, 0.7f, 0.2f, 1.0f);
                name = "Y";
            } else if(i == 2){
                name = "Z";
                normal = new Color(0.1f, 0.25f, 0.8f, 1.0f);
                hover = new Color(0.2f, 0.35f, 0.9f, 1.0f);
                active = new Color(0.1f, 0.25f, 0.8f, 1.0f);
            }else if(i == 3){
                name = "W";
                normal = new Color(0.2f, 0.1f, 0.3f, 1.0f);
                hover = new Color(0.4f, 0.3f, 0.5f, 1.0f);
                active = new Color(0.2f, 0.1f, 0.3f, 1.0f);
            }

            ImGui.pushItemWidth(widthEach);

            ImGui.pushStyleColor(ImGuiCol.Button, normal.getRed(), normal.getGreen(), normal.getBlue(), normal.getAlpha());
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hover.getRed(), hover.getGreen(), hover.getBlue(), hover.getAlpha());
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, active.getRed(), active.getGreen(), active.getBlue(), active.getAlpha());

            if (ImGui.button(name, buttonSize.x, buttonSize.y)) {
                if(values instanceof Vector2f || values instanceof Vector3f || values instanceof Vector4f) {
                    setVecF(values, i, resetValue);
                }else if(values instanceof Vector2d || values instanceof Vector3d || values instanceof Vector4d){
                    setVecD(values, i, resetValue);
                }
            }

            float[] vecValues = {};
            if(values instanceof Vector2f || values instanceof Vector3f || values instanceof Vector4f) {
                if(count == 2) vecValues = new float[]{((Vector2f) values).get(i)};
                if(count == 3) vecValues = new float[]{((Vector3f) values).get(i)};
                if(count == 4) vecValues = new float[]{((Vector4f) values).get(i)};
            }else if(values instanceof Vector2d || values instanceof Vector3d || values instanceof Vector4d){
                if(count == 2) vecValues = new float[]{(float) ((Vector2d) values).get(i)};
                if(count == 3) vecValues = new float[]{(float) ((Vector3d) values).get(i)};
                if(count == 4) vecValues = new float[]{(float) ((Vector4d) values).get(i)};
            }

            ImGui.popStyleColor(3);
            ImGui.sameLine();

            ImGui.dragFloat("##" + name, vecValues, 2f, Integer.MAX_VALUE, Integer.MAX_VALUE, "%.2f");
            ImGui.popItemWidth();
            ImGui.sameLine();

            if(values instanceof Vector2f || values instanceof Vector3f || values instanceof Vector4f) {
                setVecF(values, i, vecValues[0]);
            }else if(values instanceof Vector2d || values instanceof Vector3d || values instanceof Vector4d){
                setVecD(values, i, vecValues[0]);
            }
        }
        ImGui.nextColumn();
    }

    public static <T> void setVecF(T type, int index, float value){
        if(type instanceof Vector2f vec2){
            float[] v = {vec2.x, vec2.y};
            v[index] = value;
            vec2.set(v);
        }else if(type instanceof Vector3f vec3){
            float[] v = {vec3.x, vec3.y, vec3.z};
            v[index] = value;
            vec3.set(v);
        }else if(type instanceof Vector4f vec4){
            float[] v = {vec4.x, vec4.y, vec4.z, vec4.w};
            v[index] = value;
            vec4.set(v);
        }
    }

    public static <T> void setVecD(T type, int index, float value){
        if(type instanceof Vector2d vec2){
            double[] v = {vec2.x, vec2.y};
            v[index] = value;
            vec2.set(v);
        }else if(type instanceof Vector3d vec3){
            double[] v = {vec3.x, vec3.y, vec3.z};
            v[index] = value;
            vec3.set(v);
        }else if(type instanceof Vector4d vec4){
            double[] v = {vec4.x, vec4.y, vec4.z, vec4.w};
            v[index] = value;
            vec4.set(v);
        }
    }
}
