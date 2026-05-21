package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ui.screens.MainMenuScreen;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        GameManager gm = new GameManager();
        ScreenController sc = new ScreenController(stage, gm);

        stage.setTitle("Donjon Infini ⚔️");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        sc.afficher(new MainMenuScreen(sc));
        stage.show();

        stage.setOnCloseRequest(e -> {
            gm.disconnect();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
