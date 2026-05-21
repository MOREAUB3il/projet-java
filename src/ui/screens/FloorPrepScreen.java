package ui.screens;

import inventaire.Inventaire;
import inventaire.Objet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jeu.Equipe;
import personnage.Personnage;
import ui.GameManager;
import ui.ScreenController;
import ui.components.CharacterCard;
import ui.components.HPBar;

import java.util.List;

public class FloorPrepScreen extends BorderPane {

    private final ScreenController sc;
    private final GameManager gm;

    public FloorPrepScreen(ScreenController sc) {
        this.sc = sc;
        this.gm = sc.getGm();
        getStyleClass().add("root");
        setPadding(new Insets(30));
        construire();
    }

    private void construire() {
        Equipe equipe = gm.getEquipe();

        // ── En-tête ────────────────────────────────────────────────────────
        Label labelEtage = new Label("⚔️  Étage " + gm.getEtage());
        labelEtage.getStyleClass().add("label-etage");

        Label labelJoueur = new Label("Aventurier : " + gm.getNomJoueur());
        labelJoueur.getStyleClass().add("label-info");

        Label labelOr = new Label("💰  Or : " + equipe.getInventaireCommun().getOr());
        labelOr.setStyle("-fx-font-size: 14px; -fx-text-fill: #c9a84c; -fx-font-weight: bold;");

        HBox header = new HBox(30, labelEtage, labelJoueur, labelOr);
        header.setAlignment(Pos.CENTER);
        setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));

        // ── Équipe ─────────────────────────────────────────────────────────
        Label titreEquipe = new Label("Votre équipe :");
        titreEquipe.getStyleClass().add("titre-ecran");

        HBox cartes = new HBox(15);
        cartes.setAlignment(Pos.CENTER);
        for (Personnage p : equipe.getMembres()) {
            CharacterCard card = new CharacterCard(p);
            card.setOnMouseClicked(null); // Pas d'action de sélection ici
            cartes.getChildren().add(card);
        }

        VBox zoneEquipe = new VBox(10, titreEquipe, cartes);
        zoneEquipe.setAlignment(Pos.CENTER);

        // ── Inventaire ─────────────────────────────────────────────────────
        Label titreInv = new Label("Inventaire :");
        titreInv.getStyleClass().add("titre-ecran");

        HBox slots = new HBox(10);
        slots.setAlignment(Pos.CENTER);
        Inventaire inv = equipe.getInventaireCommun();
        for (int i = 0; i < inv.getTailleMaxObjets(); i++) {
            Objet obj = inv.getObjetDansSlot(i);
            Button slotBtn = new Button(obj != null ? obj.getNom() : "[Vide]");
            slotBtn.getStyleClass().add(obj != null ? "btn-action" : "btn-danger");
            slotBtn.setDisable(obj == null);
            slotBtn.setWrapText(true);
            slotBtn.setMaxWidth(130);
            slots.getChildren().add(slotBtn);
        }

        VBox zoneInv = new VBox(10, titreInv, slots);
        zoneInv.setAlignment(Pos.CENTER);

        // ── Actions ────────────────────────────────────────────────────────
        Button btnShop = new Button("🛒  Magasin");
        btnShop.getStyleClass().add("btn-action");
        btnShop.setOnAction(e -> sc.afficher(new ShopScreen(sc)));

        Button btnRecruiter = new Button("👥  Recruter");
        btnRecruiter.getStyleClass().add("btn-action");
        btnRecruiter.setDisable(!equipe.peutAjouterMembre());
        btnRecruiter.setOnAction(e -> sc.afficher(new CharacterSelectScreen(sc)));

        Button btnSauvegarder = new Button("💾  Sauvegarder & Quitter");
        btnSauvegarder.getStyleClass().add("btn-danger");
        btnSauvegarder.setOnAction(e -> {
            gm.sauvegarder();
            sc.afficher(new MainMenuScreen(sc));
        });

        Button btnCombat = new Button("⚔️  ENTRER AU COMBAT !");
        btnCombat.getStyleClass().add("btn-principal");
        btnCombat.setStyle("-fx-font-size: 18px; -fx-padding: 14 40 14 40;");
        btnCombat.setOnAction(e -> {
            gm.lancerCombat();
            sc.afficher(new CombatScreen(sc));
        });

        HBox actionsHaut = new HBox(15, btnShop, btnRecruiter, btnSauvegarder);
        actionsHaut.setAlignment(Pos.CENTER);

        VBox centre = new VBox(25,
                zoneEquipe,
                new Separator(),
                zoneInv,
                new Separator(),
                actionsHaut,
                btnCombat
        );
        centre.setAlignment(Pos.CENTER);
        centre.setPadding(new Insets(10));

        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);

        // ── Événement si étage spécial ─────────────────────────────────────
        if (gm.doitDeclencherEvenement()) {
            javafx.application.Platform.runLater(() ->
                    new EventDialog(sc, this).show()
            );
        }
    }
}
