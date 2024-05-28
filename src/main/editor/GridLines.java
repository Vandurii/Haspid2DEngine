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
    private transient double lastZoom;
    private transient Vector2d lastCamPos;

    private transient Camera camera;
    private transient ViewPort viewPort;

    public GridLines(){
        this.camera = Window.getInstance().getCurrentScene().getCamera();
        this.viewPort = ViewPort.getInstance();
    }

    @Override
    public void update(float dt) {
        // disable grid lines when screen is to narrow or dimension to large
        if(viewPort.getViewPortWidth() < minimalViewPortWidthForGrid || uProjectionDimension.x * currentZoomValue > maximalProjectionWidthForGrid){
            DebugDraw.notify(Disable, gridID);
            return;
        }else if(!request(IsEnabled, gridID)){
            DebugDraw.notify(Enable, gridID);
        }

        // get current camera position in the world
        Vector2d camPos = camera.getPosition();
        double camX = camPos.x;
        double camY = camPos.y;

        // recalculate lines when projection or camera position has changed
        if(currentZoomValue != lastZoom || camX != lastCamPos.x || camY != lastCamPos.y) {

            //destroy old lines before add new
            DebugDraw.notify(Clear, gridID);

            // calculate how much lines fit into the screen
            int horizontalLines = (int) ((uProjectionDimension.y * currentZoomValue) / gridSize) + 1;
            int verticalLines = (int) ((uProjectionDimension.x * currentZoomValue) / gridSize) + 1;

            // add vertical lines
            for (int i = 0; i < verticalLines; i++) {
                DebugDraw.addLine2D(new Vector2d(camX + (i * (gridSize)), camY), new Vector2d(camX + (i * gridSize), camY + (uProjectionDimension.y * currentZoomValue)), gridLinesColor, gridID, gridLinesZIndex, Static, null);
            }

            // add horizontal lines
            for (int i = 0; i < horizontalLines; i++) {
                DebugDraw.addLine2D(new Vector2d(camX,camY + (i * gridSize)), new Vector2d(camX + (uProjectionDimension.x * currentZoomValue), camY + (i * gridSize)), gridLinesColor, gridID, gridLinesZIndex, Static, null);
            }

            // save last values
            lastZoom = currentZoomValue;
            lastCamPos = new Vector2d(camX, camY);

            DebugDraw.notify(SetDirty, gridID);
        }
    }
}
