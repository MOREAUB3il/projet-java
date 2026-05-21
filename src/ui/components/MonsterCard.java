package ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import monstre.Monstre;

public class MonsterCard extends VBox {

    private final Monstre monstre;
    private final HPBar hpBar;
    private final Label labelPv;
    private final Label labelEffets;
    private boolean ciblable = false;

    public MonsterCard(Monstre m) {
        this.monstre = m;
        setSpacing(4);
        setAlignment(Pos.CENTER);
        getStyleClass().add("card-monstre");

        Label emoji = new Label("👹");
        emoji.setStyle("-fx-font-size: 28px;");

        Label labelNom = new Label(m.getNom());
        labelNom.getStyleClass().add("label-nom-monstre");
        labelNom.setWrapText(true);
        labelNom.setAlignment(Pos.CENTER);
        labelNom.setMaxWidth(150);

        hpBar = new HPBar(true);
        hpBar.setMaxWidth(Double.MAX_VALUE);
        hpBar.setRatio(m.getPv(), m.getPvMax());

        labelPv = new Label(m.getPv() + "/" + m.getPvMax() + " PV");
        labelPv.getStyleClass().add("label-pv");

        labelEffets = new Label("");
        labelEffets.setStyle("-fx-font-size: 12px;");

        getChildren().addAll(emoji, labelNom, hpBar, labelPv, labelEffets);
    }

    public void rafraichir() {
        hpBar.setRatio(monstre.getPv(), monstre.getPvMax());
        labelPv.setText(monstre.getPv() + "/" + monstre.getPvMax() + " PV");
        String effets = monstre.afficherEmojisEffets();
        labelEffets.setText(effets.isBlank() ? "" : effets);
    }

    public void setCiblable(boolean c) {
        ciblable = c;
        getStyleClass().removeAll("card-monstre-ciblable");
        if (c) getStyleClass().add("card-monstre-ciblable");
    }

    public void animerDegats() {
        ColorAdjust rouge = new ColorAdjust();
        rouge.setSaturation(1.0);
        rouge.setHue(0.05);
        setEffect(rouge);
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(300), e -> setEffect(null))
        );
        tl.play();
    }

    public Monstre getMonstre() { return monstre; }
}
