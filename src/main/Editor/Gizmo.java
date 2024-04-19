package main.Editor;

import main.components.Component;
import main.components.Sprite;
import main.components.SpriteRenderer;
import main.haspid.*;
import main.renderer.Renderer;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2f;

import static main.Configuration.*;

public class Gizmo extends Component {

    private static Gizmo instance;

    private int gizmoIndex;
    private MouseListener mouse;
    private GameObject lastActiveObject;
    private SpriteSheet gimzosSheet;

    private SpriteRenderer xAxisSpriteRender;
    private GameObject xAxisBody;
    private Sprite xAxisSprite;
    private int xAxisXPadding;
    private int xAxisYPadding;
    private boolean isXAxisHot;

    private SpriteRenderer yAxisSpriteRender;
    private GameObject yAxisBody;
    private Sprite yAxisSprite;
    private int yAxisXPadding;
    private int yAxisYPadding;
    private boolean isYAxisHot;

    private Gizmo(){
        this.gizmoIndex = 1;
        this.xAxisXPadding = xGizmoXAxis;
        this.xAxisYPadding = xGizmoYAxis;
        this.yAxisXPadding = yGizmoXAxis;
        this.yAxisYPadding = yGizmoYAxis;
        this.mouse = MouseListener.getInstance();
        this.gimzosSheet = AssetPool.getSpriteSheet(gizmosConfig);

        xAxisBody = new GameObject("gizmoXAxis", new Transform(new Vector2f(), gizmoScale), 20);
        xAxisBody.setNonSerializable();
        xAxisBody.setNonTriggerable();

        yAxisBody = new GameObject("gizmoYAxis", new Transform(new Vector2f(), gizmoScale), 20);
        yAxisBody.setNonSerializable();
        yAxisBody.setNonTriggerable();

        Window.getInstance().getCurrentScene().addGameObjectToScene(xAxisBody);
        Window.getInstance().getCurrentScene().addGameObjectToScene(yAxisBody);
    }

    public static Gizmo getInstance(){
        if(instance == null) instance = new Gizmo();

        return  instance;
    }

    public void create(){
        Sprite template = gimzosSheet.getSprite(gizmoIndex);

        xAxisSprite = new Sprite(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        xAxisSpriteRender = new SpriteRenderer(xAxisSprite, xGizmoRotation);
        xAxisSprite.setColor(gizmoColor);
        xAxisBody.addComponent(xAxisSpriteRender);
        xAxisSpriteRender.start();
        Renderer.getInstance().add(xAxisSpriteRender);

        yAxisSprite = new Sprite(template.getTexture(), template.getWidth(), template.getHeight(), template.getSpriteCords());
        yAxisSpriteRender = new SpriteRenderer(yAxisSprite, yGizmoRotation);
        yAxisSprite.setColor(gizmoColor);
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
        GameObject activeObject = MouseControls.getInstance().getActiveGameObject();

        if(activeObject != null && activeObject != lastActiveObject) {
            if(yAxisBody.getComponent(SpriteRenderer.class) == null) create();

        }else if(activeObject == null){
            destroy();
        }

        if(activeObject != null) {
            xAxisBody.getTransform().setPosition(new Vector2f(activeObject.getTransform().getPosition().x + xAxisXPadding, activeObject.getTransform().getPosition().y + xAxisYPadding));
            yAxisBody.getTransform().setPosition(new Vector2f(activeObject.getTransform().getPosition().x + yAxisXPadding, activeObject.getTransform().getPosition().y + yAxisYPadding));
        }

        if(activeObject != null){
            // gizmo - xAxis
            Vector2f xPos = xAxisBody.getTransform().getPosition();
            Vector2f xScale = xAxisBody.getTransform().getScale();

            if(mouse.getWorldX() >= (xPos.x - xScale.y - gridSize) && mouse.getWorldX() <= xPos.x + gridSize && mouse.getWorldY() >= xPos.y - gridSize && mouse.getWorldY() <= xPos.y + xScale.x + gridSize){
                xAxisSprite.setColor(hoverGizmoColor);
                isXAxisHot = true;
            }else{
                xAxisSprite.setColor(gizmoColor);
                isXAxisHot = false;
            }

            // gizmo - yAxis
            Vector2f yPos = yAxisBody.getTransform().getPosition();
            Vector2f yScale = yAxisBody.getTransform().getScale();

            if(mouse.getWorldX() >= yPos.x - yScale.x - gridSize && mouse.getWorldX() <= yPos.x + gridSize && mouse.getWorldY() > yPos.y - yScale.y - gridSize&& mouse.getWorldY() <= yPos.y + gridSize){
                yAxisSprite.setColor(hoverGizmoColor);
                isYAxisHot = true;
            }else{
                yAxisSprite.setColor(gizmoColor);
                isYAxisHot = false;
            }
        }

        lastActiveObject = activeObject;
    }

    public boolean isHot(){
        return isXAxisHot || isYAxisHot;
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

