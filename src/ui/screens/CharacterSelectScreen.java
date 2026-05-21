package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.GameManager;
import ui.ScreenController;

import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterSelectScreen extends BorderPane {

    private static final Map<String, String> CLASSES = new LinkedHashMap<>() {{
        put("Assassin",    "🗡️  Assassin     — DPS furtif, ignore la défense ennemie");
        put("Barbare",     "🪓  Barbare       — DPS brutal, saignement, vol de vie");
        put("Chevalier",   "⚔️  Chevalier     — Tank solide, attaque proportionnelle aux PV");
        put("Enchanteur",  "📜  Enchanteur    — Support, réduit la force ennemie");
        put("Necromancien","☠️  Nécromancien  — Mage, dégâts basés sur les PV ennemis");
        put("Paladin",     "🔨  Paladin       — Tank offensif, utilise sa défense comme attaque");
        put("Pretre",      "🪬  Prêtre        — Support, soins de zone, purification");
        put("Pyromancien", "🔥  Pyromancien   — Mage, brûlures et dégâts de zone");
    }};

    private final ScreenController sc;
    private final VBox boutons = new VBox(8);

    public CharacterSelectScreen(ScreenController sc) {
        this.sc = sc;
        getStyleClass().add("root");
        setPadding(new Insets(30));

        // ── Titre ──────────────────────────────────────────────────────────
        Label titre = new Label("Choisissez votre personnage initial");
        titre.getStyleClass().add("titre-ecran");

        Label info = new Label("Un seul personnage de chaque classe par équipe.");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #8888aa;");

        VBox header = new VBox(6, titre, info);
        header.setAlignment(Pos.CENTER);

        // ── Liste des classes ──────────────────────────────────────────────
        boutons.setAlignment(Pos.CENTER_LEFT);
        boutons.setMaxWidth(500);
        construireBoutons();

        VBox centre = new VBox(20, boutons);
        centre.setAlignment(Pos.CENTER);

        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));
        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);
    }

    private void construireBoutons() {
        boutons.getChildren().clear();
        GameManager gm = sc.getGm();

        for (Map.Entry<String, String> entry : CLASSES.entrySet()) {
            String classe = entry.getKey();
            String description = entry.getValue();
            boolean pris = gm.getEquipe() != null && gm.getEquipe().classeEstPresente(classe);

            Button btn = new Button(description + (pris ? "   ✅" : ""));
            btn.getStyleClass().add("btn-classe");
            if (pris) btn.getStyleClass().add("btn-classe-pris");
            btn.setDisable(pris);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(e -> {
                boolean ajoute = gm.ajouterPersonnage(classe);
                if (ajoute) {
                    construireBoutons();
                    // Passer à la préparation dès le premier perso choisi
                    sc.afficher(new FloorPrepScreen(sc));
                }
            });
            boutons.getChildren().add(btn);
        }
    }
}
