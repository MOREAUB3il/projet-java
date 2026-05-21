package ui;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ScreenController {

    private final Stage stage;
    private final GameManager gm;

    public ScreenController(Stage stage, GameManager gm) {
        this.stage = stage;
        this.gm = gm;
    }

    public void afficher(Pane ecran) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(ecran, 1200, 750);
            var cssUrl = getClass().getResource("/ui/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
        } else {
            Pane ancien = (Pane) scene.getRoot();
            FadeTransition sortie = new FadeTransition(Duration.millis(200), ancien);
            sortie.setFromValue(1.0);
            sortie.setToValue(0.0);
            Scene finalScene = scene;
            sortie.setOnFinished(e -> {
                finalScene.setRoot(ecran);
                FadeTransition entree = new FadeTransition(Duration.millis(300), ecran);
                entree.setFromValue(0.0);
                entree.setToValue(1.0);
                entree.play();
            });
            sortie.play();
        }
    }

    public GameManager getGm() { return gm; }
    public Stage getStage()    { return stage; }
}
