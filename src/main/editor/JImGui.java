package main.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import main.components.Component;
import org.joml.*;

import java.awt.*;

import static main.Configuration.*;

public class JImGui {
    public static <T> Object drawValue(String label, T type){
        return drawValue(label, type, 0);
    }

    public static <T> Object drawValue(String label, T type , float resetValue){
        String name = "##" + label;
        ImGui.pushID(EditorScene.generateID());

        ImGui.columns(2);
        ImGui.text(label);
        ImGui.nextColumn();

        if(Component.getSupportedVectors().contains(type.getClass())){
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, inspectorXSpacing, inspectorYSpacing);

            if(type instanceof Vector2d[] vector2ds){
                for(Vector2d v2d: vector2ds){
                    vec(v2d, resetValue);
                }
            }else {
                vec(type, resetValue);
            }

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
        }else if(type instanceof String str){
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

        for(int i = 0; i < count; i++) {

            if (i == 0) {
                name = firstButtonName;
                normal = firstButNormal;
                hover = firstButHover;
                active = firstButActive;
            } else if (i == 1) {
                name = secondButtonName;
                normal = secondButNormal;
                hover = secondButHover;
                active = secondButActive;
            } else if(i == 2){
                name = thirdButtonName;
                normal = thirdButNormal;
                hover = thirdButHover;
                active = thirdButActive;
            }else if(i == 3){
                name = fourthButtonName;
                normal = fourthButNormal;
                hover = fourthButHover;
                active = fourthButActive;
            }

            ImGui.pushItemWidth(vectorButtonWidth);

            ImGui.pushStyleColor(ImGuiCol.Button, normal.getRed(), normal.getGreen(), normal.getBlue(), normal.getAlpha());
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hover.getRed(), hover.getGreen(), hover.getBlue(), hover.getAlpha());
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, active.getRed(), active.getGreen(), active.getBlue(), active.getAlpha());

            ImGui.pushID(EditorScene.generateID());
            if (ImGui.button(name, buttonSize.x, buttonSize.y)) {
                if(values instanceof Vector2f || values instanceof Vector3f || values instanceof Vector4f) {
                    setVecF(values, i, resetValue);
                }else if(values instanceof Vector2d || values instanceof Vector3d || values instanceof Vector4d){
                    setVecD(values, i, resetValue);
                }
            }
            ImGui.popID();

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
