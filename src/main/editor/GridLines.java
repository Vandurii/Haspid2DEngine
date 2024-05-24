package main.editor;

import main.components.Component;
import main.haspid.Camera;
import main.haspid.Window;
import main.renderer.DebugDraw;
import org.joml.Vector2d;

import static main.Configuration.*;

public class GridLines extends Component {

    public GridLines(){}

    @Override
    public void update(float dt) {
        Camera cam = Window.getInstance().getCurrentScene().getCamera();
        int horizontalLines = (int)((uProjectionDimension.y * currentZoomValue) / gridSize) + 1;
        int verticalLines = (int)((uProjectionDimension.x * currentZoomValue) /  gridSize) + 1;

        for(int i = 0; i < verticalLines; i++){
           DebugDraw.addLine2D(gridLinesZIndex, new Vector2d(cam.getPosition().x + (i * (gridSize)), cam.getPosition().y), new Vector2d(cam.getPosition().x + (i * gridSize), cam.getPosition().y + (uProjectionDimension.y * currentZoomValue)), gridLinesColor, 1);
        }

        for(int i = 0; i < horizontalLines; i++){
            DebugDraw.addLine2D(gridLinesZIndex, new Vector2d(cam.getPosition().x, cam.getPosition().y + (i * gridSize)), new Vector2d(cam.getPosition().x + (uProjectionDimension.x * currentZoomValue), cam.getPosition().y + (i * gridSize)), gridLinesColor, 1);
        }
    }
}
