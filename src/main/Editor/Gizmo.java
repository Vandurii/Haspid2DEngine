package main.Editor;

import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.renderer.Renderer;
import main.scene.EditorScene;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2f;

import static main.Configuration.*;

public class Gizmo extends Component {

    private int gizmoIndex;
    private MouseListener mouse;
    private GameObject lastActiveObject;
    private SpriteSheet gimzosSheet;
    private EditorScene editorScene;

    private SpriteRenderer xAxisSpriteRender;
    private GameObject xAxisBody;
    private int xAxisXPadding;
    private int xAxisYPadding;
    private boolean isXAxisHot;

    private SpriteRenderer yAxisSpriteRender;
    private GameObject yAxisBody;
    private int yAxisXPadding;
    private int yAxisYPadding;
    private boolean isYAxisHot;

    public Gizmo(EditorScene editorScene){

        this.gizmoIndex = 1;
        this.xAxisXPadding = xGizmoXAxis;
        this.xAxisYPadding = xGizmoYAxis;
        this.yAxisXPadding = yGizmoXAxis;
        this.yAxisYPadding = yGizmoYAxis;
        this.editorScene = editorScene;
        this.mouse = MouseListener.getInstance();
        this.gimzosSheet = AssetPool.getSpriteSheet(gizmosConfig);

        this.xAxisBody = new GameObject("gizmoXAxis");
        this.xAxisBody.addComponent(new Transform(new Vector2f(), gizmoScale, xGizmoRotation, 20));
        this.xAxisBody.setTransformFromItself();
        this.xAxisBody.setNonSerializable();
        this.xAxisBody.setNonTriggerable();

        this.yAxisBody = new GameObject("gizmoYAxis");
        this.yAxisBody.addComponent(new Transform(new Vector2f(), gizmoScale, yGizmoRotation, 20));
        this.yAxisBody.setTransformFromItself();
        this.yAxisBody.setNonSerializable();
        this.yAxisBody.setNonTriggerable();

        editorScene.addGameObjectToScene(xAxisBody, yAxisBody);
    }

    public void create(){
        SpriteRenderer template = gimzosSheet.getSprite(gizmoIndex);

        xAxisSpriteRender = new SpriteRenderer(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        xAxisSpriteRender.setColor(gizmoColor);
        xAxisBody.addComponent(xAxisSpriteRender);
        xAxisSpriteRender.start();
        Renderer.getInstance().add(xAxisSpriteRender);

        yAxisSpriteRender = new SpriteRenderer(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        yAxisSpriteRender.setColor(gizmoColor);
        yAxisBody.addComponent(yAxisSpriteRender);
        yAxisSpriteRender.start();
        Renderer.getInstance().add(yAxisSpriteRender);
    }

    public void destroy(){
        SpriteRenderer xAxisRender = xAxisBody.getComponent(SpriteRenderer.class);
        if(xAxisRender != null) xAxisRender.markToRemove();

        SpriteRenderer yAxisRender = yAxisBody.getComponent(SpriteRenderer.class);
        if(yAxisRender != null) yAxisRender.markToRemove();
    }

    @Override
    public void update(float dt) {
        GameObject activeObject = editorScene.getActiveGameObject();

        if(activeObject != null && activeObject != lastActiveObject) {
            if(yAxisBody.getComponent(SpriteRenderer.class) == null) create();

        }else if(activeObject == null){
            destroy();
        }

        if(activeObject != null) {
            Vector2f aPos = activeObject.getTransform().getPosition();
            Vector2f aScale = activeObject.getTransform().getScale();

            xAxisBody.getTransform().setPosition(new Vector2f(aPos.x + xAxisXPadding + aScale.x, aPos.y + xAxisYPadding));
            yAxisBody.getTransform().setPosition(new Vector2f(aPos.x + yAxisXPadding, aPos.y + yAxisYPadding + aScale.y));
        }

        if(activeObject != null){
            Vector2f aScale = activeObject.getTransform().getScale();

            // gizmo - xAxis
            Vector2f xPos = xAxisBody.getTransform().getPosition();
            Vector2f xScale = xAxisBody.getTransform().getScale();

            if(mouse.getWorldX() >= (xPos.x - xScale.y - aScale.x) && mouse.getWorldX() <= xPos.x + aScale.x && mouse.getWorldY() >= xPos.y - aScale.y && mouse.getWorldY() <= xPos.y + xScale.x + aScale.y){
                xAxisSpriteRender.setColor(hoverGizmoColor);
                isXAxisHot = true;
            }else{
                xAxisSpriteRender.setColor(gizmoColor);
                isXAxisHot = false;
            }

            // gizmo - yAxis
            Vector2f yPos = yAxisBody.getTransform().getPosition();
            Vector2f yScale = yAxisBody.getTransform().getScale();

            if(mouse.getWorldX() >= yPos.x - yScale.x - aScale.x && mouse.getWorldX() <= yPos.x + aScale.x && mouse.getWorldY() > yPos.y - yScale.y - aScale.y && mouse.getWorldY() <= yPos.y + aScale.y){
                yAxisSpriteRender.setColor(hoverGizmoColor);
                isYAxisHot = true;
            }else{
                yAxisSpriteRender.setColor(gizmoColor);
                isYAxisHot = false;
            }
        }

        lastActiveObject = activeObject;
    }

    public boolean isHot(){
        return isXAxisHot || isYAxisHot;
    }

    public boolean isXAxisHot(){
        return isXAxisHot;
    }

    public boolean isYAxisHot(){
        return isYAxisHot;
    }

    public void setGizmoIndex(int index){
        this.gizmoIndex = index;
        destroy();
        create();
    }

    public int getGizmoIndex(){
        return gizmoIndex;
    }
}