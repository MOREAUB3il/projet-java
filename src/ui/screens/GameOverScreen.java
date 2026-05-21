package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.ScreenController;

public class GameOverScreen extends BorderPane {

    public GameOverScreen(ScreenController sc) {
        getStyleClass().add("root");
        setPadding(new Insets(60));

        Label emoji = new Label("💀");
        emoji.setStyle("-fx-font-size: 80px;");

        Label titre = new Label("DÉFAITE...");
        titre.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #ff5555; " +
                "-fx-effect: dropshadow(gaussian, #ff555588, 25, 0.6, 0, 0);");

        Label sousTitre = new Label("Votre équipe a été anéantie à l'étage " + sc.getGm().getEtage());
        sousTitre.setStyle("-fx-font-size: 20px; -fx-text-fill: #a08080;");

        Label message = new Label("L'aventure se termine ici... pour l'instant.");
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: #777799; -fx-font-style: italic;");

        Button btnRecommencer = new Button("🔄  Recommencer");
        btnRecommencer.getStyleClass().add("btn-principal");
        btnRecommencer.setStyle("-fx-font-size: 18px; -fx-padding: 14 40 14 40;");
        btnRecommencer.setOnAction(e -> sc.afficher(new MainMenuScreen(sc)));

        VBox centre = new VBox(22, emoji, titre, sousTitre, message, btnRecommencer);
        centre.setAlignment(Pos.CENTER);
        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);
    }
}
