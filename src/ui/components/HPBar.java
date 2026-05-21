package ui.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class HPBar extends StackPane {

    private final ProgressBar bar;
    private final DoubleProperty ratio = new SimpleDoubleProperty(1.0);
    private final boolean estMonstre;

    public HPBar(boolean estMonstre) {
        this.estMonstre = estMonstre;
        bar = new ProgressBar(1.0);
        bar.getStyleClass().add(estMonstre ? "hp-bar-monstre" : "hp-bar-hero");
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.progressProperty().bind(ratio);
        ratio.addListener((obs, old, nv) -> updateColor(nv.doubleValue()));
        getChildren().add(bar);
        setMaxWidth(Double.MAX_VALUE);
    }

    public void setRatio(double pvActuels, double pvMax) {
        if (pvMax <= 0) { ratio.set(0); return; }
        double r = Math.max(0, Math.min(1, pvActuels / pvMax));
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(ratio, r, javafx.animation.Interpolator.EASE_BOTH))
        );
        tl.play();
    }

    private void updateColor(double r) {
        if (estMonstre) return;
        String color;
        if (r > 0.6)      color = "#4caf50";
        else if (r > 0.3) color = "#ff9800";
        else               color = "#f44336";
        bar.setStyle("-fx-accent: " + color + ";");
    }
}
