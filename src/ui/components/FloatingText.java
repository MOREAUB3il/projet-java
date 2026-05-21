package ui.components;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class FloatingText {

    public enum Type { DEGATS, SOIN, XP, INFO }

    public static void afficher(Pane parent, String texte, Type type, double x, double y) {
        Label label = new Label(texte);
        String styleClass = switch (type) {
            case DEGATS -> "floating-damage";
            case SOIN   -> "floating-heal";
            case XP     -> "floating-xp";
            case INFO   -> "floating-xp";
        };
        label.getStyleClass().add(styleClass);
        label.setLayoutX(x);
        label.setLayoutY(y);
        parent.getChildren().add(label);

        TranslateTransition monte = new TranslateTransition(Duration.millis(900), label);
        monte.setByY(-70);
        FadeTransition disparait = new FadeTransition(Duration.millis(900), label);
        disparait.setFromValue(1.0);
        disparait.setToValue(0.0);

        ParallelTransition anim = new ParallelTransition(monte, disparait);
        anim.setOnFinished(e -> parent.getChildren().remove(label));
        anim.play();
    }
}
