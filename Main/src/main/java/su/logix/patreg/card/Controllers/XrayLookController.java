package su.logix.patreg.card.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.Contract;

import java.io.File;

public class XrayLookController {

    private static String path;
    private final int STEP = 50;

    @FXML
    public ImageView ivXray;

    private Rectangle2D viewport;
    private double ratio;
    private double startDragX;
    private double startDragY;
    private boolean isImageNotMoved = true;
    private Image image;

    @Contract(pure = true)
    private static String getPath() {
        return path;
    }

    static void setPath(String path) {
        XrayLookController.path = path;
    }

    @FXML
    public void initialize() {
        image = new Image(new File(getPath()).toURI().toString());
        viewport = new Rectangle2D(0, 0, image.getWidth(), image.getHeight());
        ivXray.setViewport(viewport);
        ivXray.setImage(image);
        ratio = image.getWidth() / image.getHeight();
    }

    @FXML
    public void enlarge() {
        viewport = new Rectangle2D(0, 0, viewport.getWidth() - STEP >= image.getWidth() ? viewport.getWidth() - STEP : image.getWidth(),
                viewport.getHeight() - STEP * ratio >= image.getHeight() ? viewport.getHeight() - STEP * ratio : image.getHeight());
        ivXray.setViewport(viewport);
    }

    @FXML
    public void reduce() {
        viewport = new Rectangle2D(0, 0, viewport.getWidth() + STEP, viewport.getHeight() + STEP * ratio);
        ivXray.setViewport(viewport);
    }

    @FXML
    public void mousePressed(MouseEvent mouseEvent) {
        if (isImageNotMoved) {
            startDragX = mouseEvent.getSceneX();
            startDragY = mouseEvent.getSceneY();
            isImageNotMoved = false;
        }
    }

    @FXML
    public void mouseDragged(MouseEvent mouseEvent) {
        ivXray.setTranslateX(mouseEvent.getSceneX() - startDragX);
        ivXray.setTranslateY(mouseEvent.getSceneY() - startDragY);
    }
}
