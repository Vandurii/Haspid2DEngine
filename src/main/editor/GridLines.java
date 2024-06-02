package main.editor;

import main.components.Component;
import main.editor.gui.ViewPort;
import main.haspid.Camera;
import main.haspid.Window;
import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.*;
import static main.renderer.DebugDraw.request;
import static main.renderer.DebugDrawEvents.*;
import static main.renderer.DebugDrawRequest.IsEnabled;
import static main.renderer.DrawMode.Static;

public class GridLines extends Component {
    private transient boolean display;
    private transient double lastZoom;
    private transient Vector2d lastCamPos;

    private transient Camera camera;
    private transient ViewPort viewPort;
    private transient EditorScene editorScene;

    public GridLines(EditorScene editorScene){
        this.display = true;
        this.editorScene = editorScene;
        this.viewPort = ViewPort.getInstance();
        this.camera = Window.getInstance().getCurrentScene().getCamera();
    }

    @Override
    public void update(float dt) {
        if(display) {
            // disable grid lines when screen is to narrow or dimension to large
            if (viewPort.getViewPortWidth() < minimalViewPortWidthForGrid || uProjectionDimension.x * currentZoomValue > maximalProjectionWidthForGrid) {
                DebugDraw.notify(Disable, gridID);
                DebugDraw.notify(Disable, colliderID);
                return;
            } else if (!request(IsEnabled, gridID)) {
                DebugDraw.notify(Enable, gridID);
                DebugDraw.notify(Enable, colliderID);
            }

            // get current camera position in the world
            Vector2d camPos = camera.getPosition();
            double camX = camPos.x;
            double camY = camPos.y;

            // recalculate lines when projection or camera position has changed
            if (currentZoomValue != lastZoom || camX != lastCamPos.x || camY != lastCamPos.y) {

                //destroy old lines before add new
                DebugDraw.notify(Clear, gridID);

                // calculate how much lines fit into the screen
                int horizontalLines = (int) ((uProjectionDimension.y * currentZoomValue) / gridSize) + 1;
                int verticalLines = (int) ((uProjectionDimension.x * currentZoomValue) / gridSize) + 1;

                // add vertical lines
                for (int i = 0; i < verticalLines; i++) {
                    DebugDraw.addLine2D(new Vector2d(camX + (i * (gridSize)), camY), new Vector2d(camX + (i * gridSize), camY + (uProjectionDimension.y * currentZoomValue)), gridLinesColor, gridID, gridLinesZIndex, Static, getParent());
                }

                // add horizontal lines
                for (int i = 0; i < horizontalLines; i++) {
                    DebugDraw.addLine2D(new Vector2d(camX, camY + (i * gridSize)), new Vector2d(camX + (uProjectionDimension.x * currentZoomValue), camY + (i * gridSize)), gridLinesColor, gridID, gridLinesZIndex, Static, getParent());
                }

                // save last values
                lastZoom = currentZoomValue;
                lastCamPos = new Vector2d(camX, camY);

                DebugDraw.notify(SetDirty, gridID);
            }
        }
    }

    public boolean shouldDisplay(){
        return  display;
    }

    public void setDisplay(boolean display){
        if(!display){
            DebugDraw.notify(Clear, gridID);
        }else{
            // set last zoom to 0 so that debug draws lines
            lastZoom = 0;
        }
        this.display = display;
    }
}
