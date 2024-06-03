package main.editor.editorControl;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.renderer.Renderer;
import main.editor.EditorScene;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import java.util.List;

import static main.Configuration.*;

public class Gizmo extends Component {

    private static int gizmoToolIndex;
    private MouseListener mouse;
    private GameObject lastActiveObject;
    private SpriteSheet gimzosSheet;
    private EditorScene editorScene;
    private KeyListener keyboard;
    private boolean active;

    private GameObject xAxisBody;
    private double xAxisXPadding;
    private double xAxisYPadding;
    private static boolean isXAxisHot;
    private static SpriteRenderer xAxisSpriteRender;

    private GameObject yAxisBody;
    private double yAxisXPadding;
    private double yAxisYPadding;
    private static boolean isYAxisHot;
    private static SpriteRenderer yAxisSpriteRender;

    public Gizmo(EditorScene editorScene){

        this.xAxisXPadding = xGizmoXAxis;
        this.xAxisYPadding = xGizmoYAxis;
        this.yAxisXPadding = yGizmoXAxis;
        this.yAxisYPadding = yGizmoYAxis;
        this.editorScene = editorScene;
        this.mouse = MouseListener.getInstance();
        this.keyboard = KeyListener.getInstance();
        this.gimzosSheet = editorScene.getProperties("gizmo");
        Gizmo.gizmoToolIndex = gizmoStartToolIndex;

        this.xAxisBody = new GameObject("gizmoXAxis");
        this.xAxisBody.addComponent(new Transform(new Vector2d(), gizmoScale, xGizmoRotation, gizmoZIndex));
        this.xAxisBody.setTransformFromItself();
        this.xAxisBody.setNonSerializable();
        this.xAxisBody.setNonTriggerable();

        this.yAxisBody = new GameObject("gizmoYAxis");
        this.yAxisBody.addComponent(new Transform(new Vector2d(), gizmoScale, yGizmoRotation, gizmoZIndex));
        this.yAxisBody.setTransformFromItself();
        this.yAxisBody.setNonSerializable();
        this.yAxisBody.setNonTriggerable();

        editorScene.addObjectToSceneSafe(xAxisBody, yAxisBody);
    }

    public void create(){
        active = true;
        SpriteRenderer template = gimzosSheet.getSprite(gizmoToolIndex);

        xAxisSpriteRender = new SpriteRenderer(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        xAxisSpriteRender.setColor(gizmoColor);
        xAxisBody.addComponent(xAxisSpriteRender);
        xAxisSpriteRender.init();
        Renderer.getInstance().add(xAxisSpriteRender);

        yAxisSpriteRender = new SpriteRenderer(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        yAxisSpriteRender.setColor(gizmoColor);
        yAxisBody.addComponent(yAxisSpriteRender);
        yAxisSpriteRender.init();
        Renderer.getInstance().add(yAxisSpriteRender);
    }

    public void destroy(){
        active = false;
        if(xAxisSpriteRender != null) xAxisSpriteRender.markToRemove();

        if(yAxisSpriteRender != null) yAxisSpriteRender.markToRemove();
    }

    @Override
    public void update(float dt) {

        List<GameObject> gameObjectList = MouseControls.getAllActiveObjects();
        GameObject activeObject = null;

         if(gameObjectList.size() == 1){
            activeObject = gameObjectList.get(0);
        }

        SpriteRenderer spriteRenderer = null;
        if(activeObject != null) spriteRenderer = activeObject.getComponent(SpriteRenderer.class);

        if(gameObjectList.size() == 1 && spriteRenderer != null && spriteRenderer.isHighLighted()) spriteRenderer.resetColor();

        if(gameObjectList.size() == 1 && activeObject != lastActiveObject) {
            if(yAxisBody.getComponent(SpriteRenderer.class) == null) create();
        }else if(gameObjectList.size() != 1 || MouseControls.hasDraggingObject()){
            destroy();
        }

        if(activeObject != null) {
            Vector2d aPos = activeObject.getTransform().getPosition();
            Vector2d aScale = activeObject.getTransform().getScale();

            xAxisBody.getTransform().setPosition(new Vector2d(aPos.x + xAxisXPadding + (aScale.x / 2), aPos.y + xAxisYPadding));
            yAxisBody.getTransform().setPosition(new Vector2d(aPos.x + yAxisXPadding, aPos.y + yAxisYPadding + (aScale.y / 2)));
        }

        if(activeObject != null){
            Vector2d aScale = activeObject.getTransform().getScale();

            // gizmo - xAxis
            Vector2d xPos = xAxisBody.getTransform().getPosition();
            Vector2d xScale = new Vector2d(xAxisBody.getTransform().getScale().x * 2, xAxisBody.getTransform().getScale().y * 2);

            if(mouse.getWorldX() >= (xPos.x - xScale.y - aScale.x) && mouse.getWorldX() <= xPos.x + aScale.x + xScale.x && mouse.getWorldY() >= xPos.y - aScale.y - xScale.y && mouse.getWorldY() <= xPos.y + xScale.x + aScale.y){
                xAxisSpriteRender.setColor(hoverGizmoColor);
                isXAxisHot = true;
            }else{
                xAxisSpriteRender.setColor(gizmoColor);
                isXAxisHot = false;
            }

            // gizmo - yAxis
            Vector2d yPos = yAxisBody.getTransform().getPosition();
            Vector2d yScale = new Vector2d(yAxisBody.getTransform().getScale().x * 2, yAxisBody.getTransform().getScale().y * 2);

            if(mouse.getWorldX() >= yPos.x - yScale.x - aScale.x && mouse.getWorldX() <= yPos.x + aScale.x + xScale.x && mouse.getWorldY() > yPos.y - yScale.y - aScale.y && mouse.getWorldY() <= yPos.y + aScale.y + xScale.y){
                yAxisSpriteRender.setColor(hoverGizmoColor);
                isYAxisHot = true;
            }else{
                yAxisSpriteRender.setColor(gizmoColor);
                isYAxisHot = false;
            }
        }

        lastActiveObject = activeObject;
    }

    public static boolean isHot(){
        return isXAxisHot() || isYAxisHot();
    }

    public static boolean isXAxisHot(){
        return isXAxisHot && !xAxisSpriteRender.isMarkedToRemove();
    }

    public static boolean isYAxisHot(){
        return isYAxisHot && !yAxisSpriteRender.isMarkedToRemove();
    }

    public boolean isGizmoActive(){
        return active;
    }

    public void setGizmoToolIndex(int index){
        Gizmo.gizmoToolIndex = index;
        destroy();
        create();
    }

    public static int getGizmoToolIndex(){
        return gizmoToolIndex;
    }
}