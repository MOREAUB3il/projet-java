package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jeu.Equipe;
import personnage.*;
import ui.ScreenController;

import java.util.Random;

/**
 * Affiche un événement aléatoire (TerrainRuine, Ecclesiastique, Fantome, Mercenaire)
 * sous forme de fenêtre modale JavaFX sans dépendre du Scanner.
 */
public class EventDialog {

    private final ScreenController sc;
    private final FloorPrepScreen retour;
    private final Stage dialog;
    private final VBox contenu = new VBox(14);
    private final Label labelMessage = new Label();

    public EventDialog(ScreenController sc, FloorPrepScreen retour) {
        this.sc = sc;
        this.retour = retour;

        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(sc.getStage());
        dialog.setTitle("Événement !");

        contenu.setPadding(new Insets(30));
        contenu.setAlignment(Pos.CENTER);
        contenu.setStyle("-fx-background-color: #0f0f1a;");
        contenu.setPrefWidth(500);

        labelMessage.setWrapText(true);
        labelMessage.setStyle("-fx-font-size: 13px; -fx-text-fill: #c0c0d8;");
        labelMessage.setMaxWidth(440);

        choisirEtAfficherEvenement();

        Scene scene = new Scene(contenu);
        var cssUrl = getClass().getResource("/ui/style.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        dialog.setScene(scene);
        dialog.setResizable(false);
    }

    public void show() {
        dialog.showAndWait();
    }

    private void choisirEtAfficherEvenement() {
        int choix = new Random().nextInt(4);
        switch (choix) {
            case 0 -> afficherTerrainRuine();
            case 1 -> afficherEcclesiastique();
            case 2 -> afficherFantome();
            case 3 -> afficherMercenaire();
        }
    }

    // ─── Terrain Ruine ────────────────────────────────────────────────────

    private void afficherTerrainRuine() {
        Label titre = titreLabel("🏚️  Ruines Mystérieuses");
        Label desc = new Label("Vous tombez sur les vestiges d'une ancienne structure. L'air est lourd d'histoire... ou de danger.");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #c0c0d8;");

        Button btnFouiller = new Button("🔍  Fouiller les ruines");
        btnFouiller.getStyleClass().add("btn-action");
        Button btnIgnorer = new Button("🚶  Continuer son chemin");
        btnIgnorer.getStyleClass().add("btn-action");

        btnFouiller.setOnAction(e -> {
            int roll = new Random().nextInt(100);
            Equipe eq = sc.getGm().getEquipe();
            if (roll < 40) {
                int or = 15 + new Random().nextInt(11);
                eq.getInventaireCommun().ajouterOr(or);
                labelMessage.setText("💰  Vous trouvez une bourse de " + or + " pièces d'or dans les décombres !");
            } else if (roll < 80) {
                labelMessage.setText("Après une fouille minutieuse, vous ne trouvez rien d'intéressant.");
            } else {
                if (!eq.getMembres().isEmpty()) {
                    Personnage victime = eq.getMembres().get(new Random().nextInt(eq.getMembres().size()));
                    victime.appliquerMaladie();
                    labelMessage.setText("☠️  Un cadavre en décomposition ! " + victime.getNom() + " tombe malade...");
                }
            }
            afficherFermeture();
        });
        btnIgnorer.setOnAction(e -> { labelMessage.setText("Vous continuez sans vous arrêter."); afficherFermeture(); });

        contenu.getChildren().addAll(titre, desc, new HBox(12, btnFouiller, btnIgnorer) {{ setAlignment(Pos.CENTER); }}, labelMessage);
    }

    // ─── Ecclésiastique ───────────────────────────────────────────────────

    private void afficherEcclesiastique() {
        Equipe eq = sc.getGm().getEquipe();
        boolean pretrePresent = eq.getMembres().stream().anyMatch(p -> p instanceof Pretre);
        int coutAmelioSante = pretrePresent ? 5 : 10;
        int coutRetablir    = pretrePresent ? 25 : 30;

        Label titre = titreLabel("✝️  Ecclésiastique");
        Label desc = new Label("Un homme en robes modestes vous propose ses services." +
                (pretrePresent ? "\n🪬  Réduction accordée (Prêtre dans l'équipe) !" : ""));
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #c0c0d8;");

        Button btnAmelio = new Button("💉  Améliorer PV max (+" + coutAmelioSante + "G)");
        btnAmelio.getStyleClass().add("btn-action");
        Button btnRetablir = new Button("💊  Pleine Forme (+" + coutRetablir + "G)");
        btnRetablir.getStyleClass().add("btn-action");
        Button btnDeclin = new Button("🚶  Décliner");
        btnDeclin.getStyleClass().add("btn-danger");

        btnAmelio.setDisable(eq.getInventaireCommun().getOr() < coutAmelioSante);
        btnRetablir.setDisable(eq.getInventaireCommun().getOr() < coutRetablir);

        btnAmelio.setOnAction(e -> {
            if (!eq.getMembres().isEmpty()) {
                eq.getInventaireCommun().retirerOr(coutAmelioSante);
                Personnage cible = eq.getMembres().get(0); // leader par défaut
                int bonus = 5 + new Random().nextInt(6);
                cible.setPvMax(cible.getPvMax() + bonus);
                cible.setPv(cible.getPv() + bonus);
                labelMessage.setText("✨  " + cible.getNom() + " gagne +" + bonus + " PV max !");
            }
            afficherFermeture();
        });
        btnRetablir.setOnAction(e -> {
            if (!eq.getMembres().isEmpty()) {
                eq.getInventaireCommun().retirerOr(coutRetablir);
                Personnage cible = eq.getMembres().get(0);
                cible.setSante(Personnage.SANTE_PLEINE_FORME);
                labelMessage.setText("✨  " + cible.getNom() + " est en pleine forme !");
            }
            afficherFermeture();
        });
        btnDeclin.setOnAction(e -> { labelMessage.setText("Vous remerciez l'ecclésiastique et continuez."); afficherFermeture(); });

        contenu.getChildren().addAll(titre, desc, new HBox(10, btnAmelio, btnRetablir, btnDeclin) {{ setAlignment(Pos.CENTER); }}, labelMessage);
    }

    // ─── Fantome ─────────────────────────────────────────────────────────

    private void afficherFantome() {
        Equipe eq = sc.getGm().getEquipe();
        Label titre = titreLabel("👻  Apparition Spectrale");

        Personnage enchanteur = eq.getMembres().stream()
                .filter(p -> p instanceof Enchanteur).findFirst().orElse(null);

        if (enchanteur != null) {
            int or = 10 + new Random().nextInt(6);
            eq.getInventaireCommun().ajouterOr(or);
            labelMessage.setText("📜  " + enchanteur.getNom() + " dissipe le fantôme ! Vous trouvez " + or + " pièces d'or.");
        } else if (new Random().nextBoolean() && !eq.getMembres().isEmpty()) {
            Personnage victime = eq.getMembres().get(new Random().nextInt(eq.getMembres().size()));
            String sante = victime.getSante();
            String nouvelleSante = sante.equals(Personnage.SANTE_PLEINE_FORME) ? Personnage.SANTE_FATIGUE
                    : sante.equals(Personnage.SANTE_FATIGUE) ? Personnage.SANTE_MALADE
                    : sante.equals(Personnage.SANTE_MALADE) ? Personnage.SANTE_EPUISE
                    : Personnage.SANTE_SUR_LA_FIN;
            victime.setSante(nouvelleSante);
            labelMessage.setText("❄️  Le fantôme affaiblit " + victime.getNom() + " ! (→ " + nouvelleSante + ")");
        } else {
            labelMessage.setText("Le fantôme vous observe... puis disparaît sans bruit.");
        }

        Button btnOk = new Button("OK");
        btnOk.getStyleClass().add("btn-principal");
        btnOk.setOnAction(e -> dialog.close());
        contenu.getChildren().addAll(titre, labelMessage, btnOk);
    }

    // ─── Mercenaire (simplifié) ───────────────────────────────────────────

    private void afficherMercenaire() {
        Equipe eq = sc.getGm().getEquipe();
        Label titre = titreLabel("⚔️  Mercenaire Arrogant");
        Label desc = new Label("\"Alors les p'tits nouveaux ? 20G pour passer, ou on règle ça à l'ancienne !\"");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #c0c0d8; -fx-font-style: italic;");

        Button btnPayer = new Button("💰  Payer 20G");
        btnPayer.getStyleClass().add("btn-action");
        Button btnIgnorer = new Button("🎲  Tenter de l'ignorer");
        btnIgnorer.getStyleClass().add("btn-action");
        Button btnCombat = new Button("⚔️  Se battre");
        btnCombat.getStyleClass().add("btn-danger");

        btnPayer.setDisable(eq.getInventaireCommun().getOr() < 20);

        btnPayer.setOnAction(e -> {
            eq.getInventaireCommun().retirerOr(20);
            labelMessage.setText("Le mercenaire ricane et s'écarte. Vous êtes passés !");
            afficherFermeture();
        });
        btnIgnorer.setOnAction(e -> {
            if (new Random().nextInt(100) < 30) {
                labelMessage.setText("Étonnamment, il vous laisse passer en haussant les épaules.");
            } else {
                labelMessage.setText("\"Pas si vite !\" Le mercenaire exige 20G.\nVous n'avez pas le choix.");
                if (eq.getInventaireCommun().getOr() >= 20) {
                    eq.getInventaireCommun().retirerOr(20);
                }
            }
            afficherFermeture();
        });
        btnCombat.setOnAction(e -> {
            // Combat rapide : résultat aléatoire selon force de l'équipe
            int forceTotale = eq.getMembres().stream().mapToInt(personnage.Personnage::getForce).sum();
            if (forceTotale > 30) {
                int or = 50 + new Random().nextInt(26);
                eq.getInventaireCommun().ajouterOr(or);
                labelMessage.setText("Victoire ! Le mercenaire fuit et laisse " + or + "G derrière lui.");
            } else {
                int orPerdu = eq.getInventaireCommun().getOr() / 4;
                eq.getInventaireCommun().retirerOr(orPerdu);
                labelMessage.setText("Vous résistez mais perdez " + orPerdu + "G dans l'affrontement.");
            }
            afficherFermeture();
        });

        contenu.getChildren().addAll(titre, desc,
                new HBox(10, btnPayer, btnIgnorer, btnCombat) {{ setAlignment(Pos.CENTER); }},
                labelMessage);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private Label titreLabel(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("titre-ecran");
        return l;
    }

    private void afficherFermeture() {
        Button btnContinuer = new Button("✅  Continuer l'aventure");
        btnContinuer.getStyleClass().add("btn-principal");
        btnContinuer.setOnAction(e -> dialog.close());
        if (contenu.getChildren().stream().noneMatch(n -> n instanceof Button && ((Button)n).getText().contains("Continuer"))) {
            contenu.getChildren().add(btnContinuer);
        }
    }
}
