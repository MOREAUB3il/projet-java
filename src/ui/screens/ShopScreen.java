package ui.screens;

import inventaire.Inventaire;
import inventaire.Objet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.ScreenController;

import java.util.List;

public class ShopScreen extends BorderPane {

    private static final List<Objet> STOCK = List.of(
            Objet.potionCommune(),
            Objet.potionRare(),
            Objet.potionLegendaire(),
            Objet.collierCommun(),
            Objet.collierRare(),
            Objet.collierLegendaire(),
            Objet.fioleDePoison()
    );

    public ShopScreen(ScreenController sc) {
        getStyleClass().add("root");
        setPadding(new Insets(40));

        Inventaire inv = sc.getGm().getEquipe().getInventaireCommun();

        Label titre = new Label("🛒  Magasin de l'Aventurier");
        titre.getStyleClass().add("titre-ecran");

        Label labelOr = new Label("Votre or : " + inv.getOr() + " 💰");
        labelOr.setStyle("-fx-font-size: 16px; -fx-text-fill: #c9a84c; -fx-font-weight: bold;");

        VBox items = new VBox(12);
        items.setAlignment(Pos.CENTER);
        items.setMaxWidth(600);

        Runnable refresh = () -> {
            items.getChildren().clear();
            labelOr.setText("Votre or : " + inv.getOr() + " 💰");
            for (Objet obj : STOCK) {
                HBox ligne = new HBox(20);
                ligne.setAlignment(Pos.CENTER_LEFT);
                ligne.setPadding(new Insets(10, 15, 10, 15));
                ligne.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");

                Label nom = new Label(obj.getNom());
                nom.setStyle("-fx-font-size: 14px; -fx-text-fill: #e0e0f0; -fx-min-width: 280;");

                Label prix = new Label(obj.getPrix() + " 💰");
                prix.setStyle("-fx-font-size: 13px; -fx-text-fill: #c9a84c; -fx-min-width: 70;");

                Button btnAcheter = new Button("Acheter");
                btnAcheter.getStyleClass().add("btn-action");

                // Vérifier si déjà possédé
                boolean dejaPossede = false;
                for (int i = 0; i < inv.getTailleMaxObjets(); i++) {
                    Objet slot = inv.getObjetDansSlot(i);
                    if (slot != null && slot.getNom().equals(obj.getNom())) { dejaPossede = true; break; }
                }

                if (dejaPossede) {
                    btnAcheter.setText("✅ Possédé");
                    btnAcheter.setDisable(true);
                } else if (inv.getOr() < obj.getPrix()) {
                    btnAcheter.setDisable(true);
                    btnAcheter.setText("Pas assez d'or");
                }

                btnAcheter.setOnAction(e -> {
                    if (inv.retirerOr(obj.getPrix())) {
                        Objet achat = Objet.creerObjetParNom(obj.getNom());
                        if (!inv.ajouterObjet(achat)) {
                            inv.ajouterOr(obj.getPrix()); // remboursement
                        }
                    }
                    // On recrée la vue pour refléter les changements
                    sc.afficher(new ShopScreen(sc));
                });

                ligne.getChildren().addAll(nom, prix, btnAcheter);
                items.getChildren().add(ligne);
            }
        };
        refresh.run();

        Button btnRetour = new Button("← Retour");
        btnRetour.getStyleClass().add("btn-action");
        btnRetour.setOnAction(e -> sc.afficher(new FloorPrepScreen(sc)));

        VBox centre = new VBox(20, titre, labelOr, items, btnRetour);
        centre.setAlignment(Pos.CENTER);
        setCenter(centre);
        BorderPane.setAlignment(centre, Pos.CENTER);
    }
}
