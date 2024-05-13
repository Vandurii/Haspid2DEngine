package main.editor;

import main.components.Component;
import main.haspid.Camera;
import main.haspid.Window;
import main.renderer.DebugDraw;
import org.joml.Vector2f;

import static main.Configuration.*;

public class GridLines extends Component {

    public GridLines(){}

    @Override
    public void update(float dt) {
        Camera cam = Window.getInstance().getCurrentScene().getCamera();
        int horizontalLines = (int)((uProjectionDimension.y * zoom) / gridSize) + 1;
        int verticalLines = (int)((uProjectionDimension.x * zoom) /  gridSize) + 1;

        for(int i = 0; i < verticalLines; i++){
            DebugDraw.addLine2D(gridLinesIndex, new Vector2f(cam.getPosition().x + (i * (gridSize)), cam.getPosition().y), new Vector2f(cam.getPosition().x + (i * gridSize), cam.getPosition().y + (uProjectionDimension.y * zoom)), gridLinesColor, 1);
        }

        for(int i = 0; i < horizontalLines; i++){
            DebugDraw.addLine2D(gridLinesIndex, new Vector2f(cam.getPosition().x, cam.getPosition().y + (i * gridSize)), new Vector2f(cam.getPosition().x + (uProjectionDimension.x * zoom), cam.getPosition().y + (i * gridSize)), gridLinesColor, 1);
        }
    }
}
