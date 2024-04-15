package main.Editor;

import main.Editor.ViewPort;
import main.components.Component;
import main.haspid.Camera;
import main.haspid.Window;
import main.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.sql.SQLOutput;

import static main.Configuration.*;

public class GridLines extends Component {

    private static GridLines instance;

    private GridLines(){

    }

    public static GridLines getInstance(){
        if(instance == null) instance = new GridLines();

        return instance;
    }

    @Override
    public void update(float dt) {
        Camera cam = Window.getInstance().getCurrentScene().getCamera();
        int horizontalLines = (int)(uProjectionDimension.y / gridSize);
        int verticalLines = (int)(uProjectionDimension.x /  gridSize);

        for(int i = 0; i < verticalLines; i++){
            DebugDraw.addLine2D(new Vector2f(cam.getPosition().x + (i * gridSize), 0), new Vector2f(cam.getPosition().x + (i * gridSize), uProjectionDimension.y), gridLinesColor, 1);
        }

        for(int i = 0; i < horizontalLines; i++){
            DebugDraw.addLine2D(new Vector2f(cam.getPosition().x, cam.getPosition().y + (i * gridSize)), new Vector2f( uProjectionDimension.x, cam.getPosition().y + (i * gridSize)), gridLinesColor, 1);
        }

    }
}
