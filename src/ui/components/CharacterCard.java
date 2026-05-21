package ui.components;

import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import personnage.Personnage;

public class CharacterCard extends VBox {

    private final Personnage personnage;
    private final HPBar hpBar;
    private final Label labelNom;
    private final Label labelPv;
    private final Label labelEffets;
    private boolean selectionne = false;

    public CharacterCard(Personnage p) {
        this.personnage = p;
        setSpacing(4);
        setAlignment(Pos.CENTER);
        getStyleClass().add("card-personnage");

        // Emoji de classe
        Label emoji = new Label(getEmoji(p));
        emoji.setStyle("-fx-font-size: 26px;");

        labelNom = new Label(p.getNom().replaceAll("[^\\p{L}\\p{N} ]", "").strip());
        labelNom.getStyleClass().add("label-nom-perso");
        labelNom.setWrapText(true);
        labelNom.setAlignment(Pos.CENTER);

        Label labelClasse = new Label(p.getClass().getSimpleName());
        labelClasse.getStyleClass().add("label-classe");

        hpBar = new HPBar(false);
        hpBar.setMaxWidth(Double.MAX_VALUE);
        hpBar.setRatio(p.getPv(), p.getPvMax());

        labelPv = new Label(p.getPv() + "/" + p.getPvMax() + " PV");
        labelPv.getStyleClass().add("label-pv");

        labelEffets = new Label("");
        labelEffets.setStyle("-fx-font-size: 12px;");

        getChildren().addAll(emoji, labelNom, labelClasse, hpBar, labelPv, labelEffets);

        if (!p.estVivant()) getStyleClass().add("card-personnage-mort");
    }

    public void rafraichir() {
        hpBar.setRatio(personnage.getPv(), personnage.getPvMax());
        labelPv.setText(personnage.getPv() + "/" + personnage.getPvMax() + " PV");
        String effets = personnage.afficherEmojisEffets();
        labelEffets.setText(effets.isBlank() ? "" : effets);

        getStyleClass().removeAll("card-personnage-mort");
        if (!personnage.estVivant()) getStyleClass().add("card-personnage-mort");
    }

    public void setSelectionne(boolean s) {
        selectionne = s;
        getStyleClass().removeAll("card-personnage-selectionne");
        if (s) getStyleClass().add("card-personnage-selectionne");
    }

    /** Fait clignoter la carte en rouge (coup reçu). */
    public void animerDegats() {
        ColorAdjust rouge = new ColorAdjust();
        rouge.setHue(0.1);
        rouge.setSaturation(0.8);
        setEffect(rouge);
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(300), e -> setEffect(null))
        );
        tl.play();
    }

    /** Fait clignoter la carte en vert (soin reçu). */
    public void animerSoin() {
        ColorAdjust vert = new ColorAdjust();
        vert.setHue(-0.3);
        vert.setSaturation(0.8);
        setEffect(vert);
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(300), e -> setEffect(null))
        );
        tl.play();
    }

    public Personnage getPersonnage() { return personnage; }

    private String getEmoji(Personnage p) {
        return switch (p.getClass().getSimpleName()) {
            case "Assassin"    -> "🗡️";
            case "Barbare"     -> "🪓";
            case "Chevalier"   -> "⚔️";
            case "Enchanteur"  -> "📜";
            case "Necromancien"-> "☠️";
            case "Paladin"     -> "🔨";
            case "Pretre"      -> "🪬";
            case "Pyromancien" -> "🔥";
            default            -> "🧙";
        };
    }
}
