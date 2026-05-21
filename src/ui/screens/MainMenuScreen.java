package ui.screens;

import db.Database.SauvegardeInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.GameManager;
import ui.ScreenController;

import java.util.List;

public class MainMenuScreen extends BorderPane {

    public MainMenuScreen(ScreenController sc) {
        getStyleClass().add("root");
        setPadding(new Insets(40));

        // ── Titre ──────────────────────────────────────────────────────────
        Label titre = new Label("⚔️  DONJON INFINI  ⚔️");
        titre.getStyleClass().add("titre-principal");

        Label sous = new Label("Aventure, Combat, Survie");
        sous.setStyle("-fx-font-size: 16px; -fx-text-fill: #8888aa;");

        VBox header = new VBox(10, titre, sous);
        header.setAlignment(Pos.CENTER);

        // ── Saisie pseudo ──────────────────────────────────────────────────
        Label labelNom = new Label("Entrez votre pseudo :");
        labelNom.getStyleClass().add("label-info");

        TextField champNom = new TextField();
        champNom.getStyleClass().add("text-field-rpg");
        champNom.setPromptText("Votre nom d'aventurier...");

        // ── Sauvegardes ────────────────────────────────────────────────────
        VBox listeSauvegardes = new VBox(8);
        listeSauvegardes.setAlignment(Pos.CENTER);

        Label labelSauv = new Label("Parties sauvegardées :");
        labelSauv.getStyleClass().add("label-info");
        labelSauv.setVisible(false);
        listeSauvegardes.getChildren().add(labelSauv);

        // ── Bouton charger / chercher ──────────────────────────────────────
        Button btnChercher = new Button("🔍  Chercher mes sauvegardes");
        btnChercher.getStyleClass().add("btn-action");
        btnChercher.setOnAction(e -> {
            String nom = champNom.getText().strip();
            if (nom.isEmpty()) return;
            listeSauvegardes.getChildren().clear();

            List<SauvegardeInfo> sauvegardes = sc.getGm().getSauvegardes(nom);
            if (sauvegardes.isEmpty()) {
                Label aucune = new Label("Aucune sauvegarde trouvée pour « " + nom + " ».");
                aucune.getStyleClass().add("label-info");
                listeSauvegardes.getChildren().add(aucune);
            } else {
                labelSauv.setVisible(true);
                listeSauvegardes.getChildren().add(labelSauv);
                for (SauvegardeInfo info : sauvegardes) {
                    Button btnSauv = new Button(
                            "📁  Étage " + info.numEtage + "   |   " + info.dateCreation);
                    btnSauv.getStyleClass().add("save-item");
                    btnSauv.setOnAction(ev -> {
                        sc.getGm().chargerPartie(nom, info.id);
                        sc.afficher(new FloorPrepScreen(sc));
                    });
                    listeSauvegardes.getChildren().add(btnSauv);
                }
            }
        });

        // ── Nouvelle partie ────────────────────────────────────────────────
        Button btnNouveau = new Button("⚔️  Nouvelle Partie");
        btnNouveau.getStyleClass().add("btn-principal");
        btnNouveau.setOnAction(e -> {
            String nom = champNom.getText().strip();
            if (nom.isEmpty()) {
                champNom.setStyle("-fx-border-color: #ff5555;");
                return;
            }
            sc.getGm().demarrerNouvellePartie(nom);
            sc.afficher(new CharacterSelectScreen(sc));
        });

        champNom.setOnAction(e -> btnNouveau.fire());

        // ── Mise en page ──────────────────────────────────────────────────
        VBox centre = new VBox(20,
                labelNom, champNom,
                new HBox(10, btnChercher, btnNouveau) {{ setAlignment(Pos.CENTER); }},
                listeSauvegardes
        );
        centre.setAlignment(Pos.CENTER);
        centre.setMaxWidth(500);

        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(60, 0, 40, 0));

        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);

        // ── Pied de page ──────────────────────────────────────────────────
        Label footer = new Label("Appuyez sur Entrée après votre pseudo pour lancer une nouvelle partie.");
        footer.setStyle("-fx-font-size: 12px; -fx-text-fill: #555577;");
        setBottom(footer);
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(20, 0, 10, 0));
    }
}
