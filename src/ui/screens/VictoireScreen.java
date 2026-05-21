package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.ScreenController;

public class VictoireScreen extends BorderPane {

    public VictoireScreen(ScreenController sc) {
        getStyleClass().add("root");
        setPadding(new Insets(60));

        Label emoji = new Label("🏆");
        emoji.setStyle("-fx-font-size: 80px;");

        Label titre = new Label("VICTOIRE !");
        titre.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #c9a84c; " +
                "-fx-effect: dropshadow(gaussian, #c9a84c88, 25, 0.6, 0, 0);");

        Label etage = new Label("Étage " + (sc.getGm().getEtage() - 1) + " vaincu !");
        etage.setStyle("-fx-font-size: 22px; -fx-text-fill: #a0c4ff;");

        Label or = new Label("💰  Or : " + sc.getGm().getEquipe().getInventaireCommun().getOr());
        or.setStyle("-fx-font-size: 18px; -fx-text-fill: #c9a84c;");

        Button btnContinuer = new Button("⚔️  Continuer l'aventure →");
        btnContinuer.getStyleClass().add("btn-principal");
        btnContinuer.setStyle("-fx-font-size: 20px; -fx-padding: 16 48 16 48;");
        btnContinuer.setOnAction(e -> sc.afficher(new FloorPrepScreen(sc)));

        Button btnMenu = new Button("🏠  Menu Principal");
        btnMenu.getStyleClass().add("btn-action");
        btnMenu.setOnAction(e -> {
            sc.getGm().sauvegarder();
            sc.afficher(new MainMenuScreen(sc));
        });

        VBox centre = new VBox(20, emoji, titre, etage, or, btnContinuer, btnMenu);
        centre.setAlignment(Pos.CENTER);
        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);
    }
}
