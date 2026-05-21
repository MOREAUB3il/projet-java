package ui.screens;

import inventaire.Inventaire;
import inventaire.Objet;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import monstre.Monstre;
import personnage.Paladin;
import personnage.Personnage;
import personnage.Pretre;
import ui.GameLog;
import ui.GameManager;
import ui.ScreenController;
import ui.components.CharacterCard;
import ui.components.FloatingText;
import ui.components.MonsterCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombatScreen extends BorderPane {

    // ─── État UI ──────────────────────────────────────────────────────────
    private enum UiMode { CHOISIR_PERSO, CHOISIR_ACTION, CIBLER_MONSTRE, CIBLER_ALLIE, CHOISIR_OBJET, TOUR_MONSTRE }

    private final ScreenController sc;
    private final GameManager gm;

    // Composants
    private final HBox zoneMonstres   = new HBox(15);
    private final HBox zoneEquipe     = new HBox(15);
    private final TextFlow logFlow    = new TextFlow();
    private final ScrollPane logScroll;
    private final HBox zonePA         = new HBox(6);
    private final HBox zoneActions    = new HBox(12);
    private final Label labelStatut   = new Label("Choisissez un personnage à faire agir.");

    // Cartes
    private final Map<Personnage, CharacterCard> cartesPerso   = new HashMap<>();
    private final Map<Monstre, MonsterCard>      cartesMonstre = new HashMap<>();

    // Overlay pour textes flottants (positionnement absolu)
    private final Pane overlayPane = new Pane();

    // État courant
    private UiMode modeActuel = UiMode.CHOISIR_PERSO;
    private Personnage persoActif = null;
    private ActionType actionEnCours = null;

    private enum ActionType { ATTAQUE, ULTIME, OBJET, ECHANGE }

    public CombatScreen(ScreenController sc) {
        this.sc = sc;
        this.gm = sc.getGm();
        getStyleClass().add("root");

        // ── Log de combat ─────────────────────────────────────────────────
        logFlow.setStyle("-fx-padding: 8;");
        logFlow.setPrefWidth(Double.MAX_VALUE);
        logScroll = new ScrollPane(logFlow);
        logScroll.setFitToWidth(true);
        logScroll.setMinHeight(140);
        logScroll.setMaxHeight(180);
        logScroll.getStyleClass().add("combat-log");
        VBox.setVgrow(logScroll, Priority.ALWAYS);

        // Intercepter System.out → log
        GameLog.install();
        GameLog.addListener(this::ajouterLigne);

        construireLayout();
        rafraichirCartes();

        // Callbacks GameManager
        gm.setOnUpdate(this::rafraichirTout);
        gm.setOnVictoire(this::gererVictoire);
        gm.setOnDefaite(this::gererDefaite);

        ajouterLigneStylee("=== DÉBUT DU COMBAT — ÉTAGE " + gm.getEtage() + " ===", "#c9a84c");
    }

    // ─── Construction du layout ────────────────────────────────────────────

    private void construireLayout() {
        // Zone monstres
        zoneMonstres.setAlignment(Pos.CENTER);
        zoneMonstres.setPadding(new Insets(10));
        zoneMonstres.getStyleClass().add("zone-monstres");

        // Zone équipe
        zoneEquipe.setAlignment(Pos.CENTER);
        zoneEquipe.setPadding(new Insets(10));
        zoneEquipe.getStyleClass().add("zone-equipe");

        // Statut / PA
        labelStatut.getStyleClass().add("label-pa");

        zonePA.setAlignment(Pos.CENTER_LEFT);
        zonePA.setPadding(new Insets(4, 8, 4, 8));

        zoneActions.setAlignment(Pos.CENTER);
        zoneActions.setPadding(new Insets(8));

        VBox barreActions = new VBox(6, labelStatut, zonePA, zoneActions);
        barreActions.getStyleClass().add("zone-actions");
        barreActions.setAlignment(Pos.CENTER);

        // En-tête
        Label lblEtage = new Label("COMBAT — ÉTAGE " + gm.getEtage() +
                "    |    " + gm.getNomJoueur());
        lblEtage.getStyleClass().add("label-etage");
        lblEtage.setPadding(new Insets(6, 12, 6, 12));

        Button btnPause = new Button("⏸  Pause");
        btnPause.getStyleClass().add("btn-action");
        btnPause.setOnAction(e -> afficherMenuPause());

        HBox header = new HBox(lblEtage);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(lblEtage, Priority.ALWAYS);
        header.getChildren().add(btnPause);
        header.setPadding(new Insets(6, 12, 6, 12));
        header.setStyle("-fx-background-color: #0a0a18;");

        VBox centre = new VBox(
                zoneMonstres,
                logScroll,
                zoneEquipe
        );
        VBox.setVgrow(logScroll, Priority.ALWAYS);

        // Overlay transparent pour les textes flottants animés
        overlayPane.setMouseTransparent(true);
        overlayPane.setStyle("-fx-background-color: transparent;");
        StackPane stack = new javafx.scene.layout.StackPane(centre, overlayPane);
        stack.setAlignment(Pos.TOP_LEFT);

        setTop(header);
        setCenter(stack);
        setBottom(barreActions);
    }

    // ─── Cartes ────────────────────────────────────────────────────────────

    private void rafraichirCartes() {
        // Monstres
        zoneMonstres.getChildren().clear();
        cartesMonstre.clear();
        for (Monstre m : gm.getMonstres()) {
            MonsterCard card = new MonsterCard(m);
            cartesMonstre.put(m, card);
            card.setOnMouseClicked(e -> cliquerMonstre(m, card));
            zoneMonstres.getChildren().add(card);
        }

        // Équipe
        zoneEquipe.getChildren().clear();
        cartesPerso.clear();
        for (Personnage p : gm.getEquipe().getMembres()) {
            CharacterCard card = new CharacterCard(p);
            cartesPerso.put(p, card);
            card.setOnMouseClicked(e -> cliquerPersonnage(p, card));
            zoneEquipe.getChildren().add(card);
        }
    }

    // ─── Rafraîchissement ──────────────────────────────────────────────────

    private void rafraichirTout() {
        Platform.runLater(() -> {
            // Supprimer les cartes de monstres morts
            List<Monstre> monstresMorts = new ArrayList<>(cartesMonstre.keySet());
            monstresMorts.removeAll(gm.getMonstres());
            for (Monstre m : monstresMorts) {
                MonsterCard card = cartesMonstre.remove(m);
                if (card != null) {
                    javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                            javafx.util.Duration.millis(600), card);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    ft.setOnFinished(ev -> zoneMonstres.getChildren().remove(card));
                    ft.play();
                }
            }

            // Supprimer les cartes de persos morts
            List<Personnage> persosMorts = new ArrayList<>(cartesPerso.keySet());
            persosMorts.removeAll(gm.getEquipe().getMembres());
            for (Personnage p : persosMorts) {
                CharacterCard card = cartesPerso.remove(p);
                if (card != null) {
                    javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                            javafx.util.Duration.millis(600), card);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    ft.setOnFinished(ev -> zoneEquipe.getChildren().remove(card));
                    ft.play();
                }
            }

            // Rafraîchir barres de PV existantes
            for (Map.Entry<Monstre, MonsterCard> e : cartesMonstre.entrySet()) e.getValue().rafraichir();
            for (Map.Entry<Personnage, CharacterCard> e : cartesPerso.entrySet()) e.getValue().rafraichir();

            // Sélection visuelle
            cartesPerso.forEach((p, card) -> card.setSelectionne(p == persoActif));
            cartesMonstre.forEach((m, card) -> card.setCiblable(false));

            rafraichirPA();
            rafraichirBoutonsAction();
        });
    }

    private void rafraichirPA() {
        zonePA.getChildren().clear();
        Label lbl = new Label("PA : ");
        lbl.getStyleClass().add("label-pa");
        zonePA.getChildren().add(lbl);
        int total = 4;
        for (int i = 0; i < total; i++) {
            Pane carre = new Pane();
            carre.getStyleClass().add(i < gm.getPaRestants() ? "pa-indicator" : "pa-indicator-vide");
            zonePA.getChildren().add(carre);
        }
        Label lblPa = new Label("  " + gm.getPaRestants() + "/4 restants");
        lblPa.getStyleClass().add("label-pa");
        zonePA.getChildren().add(lblPa);
    }

    private void rafraichirBoutonsAction() {
        zoneActions.getChildren().clear();

        if (gm.getPaRestants() == 0) {
            // Tour des monstres
            labelStatut.setText("⏳  Tour des monstres...");
            Button btnMonstres = new Button("▶  Tour des Monstres");
            btnMonstres.getStyleClass().add("btn-danger");
            btnMonstres.setStyle("-fx-font-size: 16px; -fx-padding: 12 28 12 28;");
            btnMonstres.setOnAction(e -> {
                btnMonstres.setDisable(true);
                gm.executerTourMonstres();
                rafraichirCartes();
                modeActuel = UiMode.CHOISIR_PERSO;
                persoActif = null;
                rafraichirTout();
            });
            zoneActions.getChildren().add(btnMonstres);
            return;
        }

        switch (modeActuel) {
            case CHOISIR_PERSO -> {
                labelStatut.setText("👆  Cliquez sur un personnage pour le faire agir.");
                Button btnPasser = new Button("⏭  Passer tous les PA");
                btnPasser.getStyleClass().add("btn-action");
                btnPasser.setOnAction(e -> {
                    while (gm.getPaRestants() > 0) gm.passerPA();
                    rafraichirTout();
                });
                zoneActions.getChildren().add(btnPasser);
            }
            case CHOISIR_ACTION -> {
                if (persoActif == null) { modeActuel = UiMode.CHOISIR_PERSO; rafraichirBoutonsAction(); return; }
                labelStatut.setText("À " + persoActif.getNom() + " d'agir.");

                boolean dejaAgi = gm.getPersonnagesAyantAgi().contains(persoActif);

                Button btnAttaque = new Button("⚔️  Attaquer");
                btnAttaque.getStyleClass().add("btn-action");
                btnAttaque.setDisable(dejaAgi || gm.getMonstres().isEmpty());
                btnAttaque.setOnAction(e -> entrerModeCiblage(ActionType.ATTAQUE));

                Button btnUltime = new Button("💥  Ultime");
                btnUltime.getStyleClass().add("btn-action");
                btnUltime.setDisable(dejaAgi || persoActif.getUltLvl() == 0);
                if (persoActif instanceof Pretre) {
                    btnUltime.setOnAction(e -> {
                        gm.utiliserUltime(persoActif, null);
                        rafraichirCartes();
                        modeActuel = UiMode.CHOISIR_PERSO;
                        persoActif = null;
                        rafraichirTout();
                    });
                } else if (persoActif instanceof Paladin) {
                    btnUltime.setOnAction(e -> {
                        gm.utiliserUltime(persoActif, null);
                        rafraichirCartes();
                        modeActuel = UiMode.CHOISIR_PERSO;
                        persoActif = null;
                        rafraichirTout();
                    });
                } else {
                    btnUltime.setOnAction(e -> entrerModeCiblage(ActionType.ULTIME));
                }

                Button btnObjet = new Button("🎒  Objet");
                btnObjet.getStyleClass().add("btn-action");
                btnObjet.setDisable(gm.getEquipe().getInventaireCommun().estVide());
                btnObjet.setOnAction(e -> afficherMenuObjet());

                Button btnEchange = new Button("🔄  Changer place");
                btnEchange.getStyleClass().add("btn-action");
                btnEchange.setDisable(dejaAgi || gm.getEquipe().getMembres().size() < 2);
                btnEchange.setOnAction(e -> { actionEnCours = ActionType.ECHANGE; modeActuel = UiMode.CIBLER_ALLIE; labelStatut.setText("Cliquez sur le personnage avec qui échanger."); rafraichirBoutonsAction(); });

                Button btnPasser = new Button("⏭  Passer ce PA");
                btnPasser.getStyleClass().add("btn-action");
                btnPasser.setOnAction(e -> { gm.passerPA(); modeActuel = UiMode.CHOISIR_PERSO; persoActif = null; rafraichirTout(); });

                Button btnAnnuler = new Button("✖  Annuler");
                btnAnnuler.getStyleClass().add("btn-danger");
                btnAnnuler.setOnAction(e -> { modeActuel = UiMode.CHOISIR_PERSO; persoActif = null; rafraichirTout(); });

                zoneActions.getChildren().addAll(btnAttaque, btnUltime, btnObjet, btnEchange, btnPasser, btnAnnuler);
            }
            case CIBLER_MONSTRE -> {
                labelStatut.setText("🎯  Cliquez sur un monstre pour le cibler.");
                cartesMonstre.forEach((m, card) -> card.setCiblable(true));
                Button btnAnnuler = new Button("✖  Annuler");
                btnAnnuler.getStyleClass().add("btn-danger");
                btnAnnuler.setOnAction(e -> { modeActuel = UiMode.CHOISIR_ACTION; rafraichirTout(); });
                zoneActions.getChildren().add(btnAnnuler);
            }
            case CIBLER_ALLIE -> {
                labelStatut.setText("👥  Cliquez sur un coéquipier pour l'échanger.");
                Button btnAnnuler = new Button("✖  Annuler");
                btnAnnuler.getStyleClass().add("btn-danger");
                btnAnnuler.setOnAction(e -> { modeActuel = UiMode.CHOISIR_ACTION; rafraichirTout(); });
                zoneActions.getChildren().add(btnAnnuler);
            }
            case CHOISIR_OBJET -> {
                labelStatut.setText("🎒  Sélectionnez un objet à utiliser.");
            }
        }
    }

    // ─── Clics ────────────────────────────────────────────────────────────

    private void cliquerPersonnage(Personnage p, CharacterCard card) {
        if (!p.estVivant()) return;

        switch (modeActuel) {
            case CHOISIR_PERSO -> {
                persoActif = p;
                gm.selectionnerAttaquant(p);
                modeActuel = UiMode.CHOISIR_ACTION;
                cartesPerso.forEach((pe, c) -> c.setSelectionne(pe == p));
                rafraichirBoutonsAction();
            }
            case CIBLER_ALLIE -> {
                if (p != persoActif && actionEnCours == ActionType.ECHANGE) {
                    gm.echangerPositions(persoActif, p);
                    rafraichirCartes();
                    modeActuel = UiMode.CHOISIR_PERSO;
                    persoActif = null;
                    rafraichirTout();
                }
            }
        }
    }

    private void cliquerMonstre(Monstre m, MonsterCard card) {
        if (!m.estVivant()) return;
        if (modeActuel != UiMode.CIBLER_MONSTRE || persoActif == null) return;

        // Enregistrer PV avant
        int pvAvant = m.getPv();
        int pvPersoAvant = persoActif.getPv();

        if (actionEnCours == ActionType.ATTAQUE) {
            gm.attaquerMonstre(m);
        } else if (actionEnCours == ActionType.ULTIME) {
            gm.utiliserUltime(persoActif, m);
        }

        // Animations
        int degats = pvAvant - m.getPv();
        if (degats > 0) {
            card.animerDegats();
            FloatingText.afficher(overlayPane, "-" + degats,
                    FloatingText.Type.DEGATS, card.getLayoutX() + 50, card.getLayoutY() - 10);
        }
        int soinRecu = persoActif.getPv() - pvPersoAvant;
        if (soinRecu > 0) {
            CharacterCard cperso = cartesPerso.get(persoActif);
            if (cperso != null) {
                cperso.animerSoin();
                FloatingText.afficher(overlayPane, "+" + soinRecu,
                        FloatingText.Type.SOIN, cperso.getLayoutX() + 50, cperso.getLayoutY() - 10);
            }
        }

        rafraichirCartes();
        modeActuel = UiMode.CHOISIR_PERSO;
        persoActif = null;
        rafraichirTout();
    }

    // ─── Menu objet ────────────────────────────────────────────────────────

    private void afficherMenuObjet() {
        modeActuel = UiMode.CHOISIR_OBJET;
        zoneActions.getChildren().clear();
        labelStatut.setText("🎒  Choisissez un objet :");

        Inventaire inv = gm.getEquipe().getInventaireCommun();
        for (int i = 0; i < inv.getTailleMaxObjets(); i++) {
            final int slot = i;
            Objet obj = inv.getObjetDansSlot(i);
            if (obj == null) continue;

            Button btn = new Button(obj.getNom());
            btn.getStyleClass().add("btn-action");
            btn.setOnAction(e -> selectionnerObjet(slot, obj));
            zoneActions.getChildren().add(btn);
        }

        Button btnAnnuler = new Button("✖  Annuler");
        btnAnnuler.getStyleClass().add("btn-danger");
        btnAnnuler.setOnAction(e -> { modeActuel = UiMode.CHOISIR_ACTION; rafraichirBoutonsAction(); });
        zoneActions.getChildren().add(btnAnnuler);
    }

    private void selectionnerObjet(int slot, Objet obj) {
        switch (obj.getCibleAutorisee()) {
            case "ALLIE" -> {
                // Demander sur quel allié
                zoneActions.getChildren().clear();
                labelStatut.setText("Sur quel allié utiliser " + obj.getNom() + " ?");
                for (Personnage p : gm.getEquipe().getMembres()) {
                    Button btn = new Button(p.getNom().replaceAll("[^\\p{L}\\p{N} ]", "").strip());
                    btn.getStyleClass().add("btn-action");
                    btn.setOnAction(e -> {
                        int pvAvant = p.getPv();
                        gm.utiliserObjet(slot, p);
                        int soin = p.getPv() - pvAvant;
                        if (soin > 0) {
                            CharacterCard card = cartesPerso.get(p);
                            if (card != null) card.animerSoin();
                            FloatingText.afficher(overlayPane, "+" + soin,
                                    FloatingText.Type.SOIN, 300, 200);
                        }
                        rafraichirCartes();
                        modeActuel = UiMode.CHOISIR_PERSO;
                        persoActif = null;
                        rafraichirTout();
                    });
                    zoneActions.getChildren().add(btn);
                }
            }
            case "ENNEMI" -> {
                zoneActions.getChildren().clear();
                labelStatut.setText("Sur quel ennemi utiliser " + obj.getNom() + " ?");
                for (Monstre m : gm.getMonstres()) {
                    Button btn = new Button(m.getNom());
                    btn.getStyleClass().add("btn-danger");
                    btn.setOnAction(e -> {
                        gm.utiliserObjet(slot, m);
                        rafraichirCartes();
                        modeActuel = UiMode.CHOISIR_PERSO;
                        persoActif = null;
                        rafraichirTout();
                    });
                    zoneActions.getChildren().add(btn);
                }
            }
            default -> {
                // SOI_MEME ou non géré
                gm.utiliserObjet(slot, persoActif != null ? persoActif : gm.getEquipe().getLeader());
                rafraichirCartes();
                modeActuel = UiMode.CHOISIR_PERSO;
                persoActif = null;
                rafraichirTout();
            }
        }
        Button btnAnnuler = new Button("✖");
        btnAnnuler.getStyleClass().add("btn-danger");
        btnAnnuler.setOnAction(e -> { modeActuel = UiMode.CHOISIR_ACTION; rafraichirBoutonsAction(); });
        zoneActions.getChildren().add(btnAnnuler);
    }

    // ─── Ciblage ──────────────────────────────────────────────────────────

    private void entrerModeCiblage(ActionType type) {
        actionEnCours = type;
        if (gm.getMonstres().size() == 1) {
            // Ciblage automatique si un seul monstre
            cliquerMonstre(gm.getMonstres().get(0), cartesMonstre.get(gm.getMonstres().get(0)));
        } else {
            modeActuel = UiMode.CIBLER_MONSTRE;
            rafraichirBoutonsAction();
        }
    }

    // ─── Fin de combat ────────────────────────────────────────────────────

    private void gererVictoire() {
        Platform.runLater(() -> {
            GameLog.clearListeners();
            GameLog.uninstall();
            sc.afficher(new VictoireScreen(sc));
        });
    }

    private void gererDefaite() {
        Platform.runLater(() -> {
            GameLog.clearListeners();
            GameLog.uninstall();
            sc.afficher(new GameOverScreen(sc));
        });
    }

    // ─── Pause ────────────────────────────────────────────────────────────

    private void afficherMenuPause() {
        Alert pause = new Alert(Alert.AlertType.NONE);
        pause.setTitle("Pause");
        pause.setHeaderText("Menu Pause");
        ButtonType reprendre = new ButtonType("▶  Reprendre");
        ButtonType sauvegarder = new ButtonType("💾  Sauvegarder & Quitter");
        ButtonType quitter = new ButtonType("✖  Quitter sans sauvegarder");
        pause.getButtonTypes().setAll(reprendre, sauvegarder, quitter);
        pause.showAndWait().ifPresent(btn -> {
            if (btn == sauvegarder) {
                gm.sauvegarder();
                GameLog.clearListeners();
                GameLog.uninstall();
                sc.afficher(new MainMenuScreen(sc));
            } else if (btn == quitter) {
                GameLog.clearListeners();
                GameLog.uninstall();
                sc.afficher(new MainMenuScreen(sc));
            }
        });
    }

    // ─── Log ──────────────────────────────────────────────────────────────

    private void ajouterLigne(String texte) {
        Platform.runLater(() -> {
            Text t = new Text(texte + "\n");
            t.setStyle("-fx-fill: #c0c0d8; -fx-font-size: 13px;");
            logFlow.getChildren().add(t);
            // Autoscroll
            logScroll.setVvalue(1.0);
        });
    }

    private void ajouterLigneStylee(String texte, String couleur) {
        Platform.runLater(() -> {
            Text t = new Text(texte + "\n");
            t.setStyle("-fx-fill: " + couleur + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            logFlow.getChildren().add(t);
            logScroll.setVvalue(1.0);
        });
    }
}
