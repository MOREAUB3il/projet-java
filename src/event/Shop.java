package event;
import java.util.*;

import inventaire.Objet;
import personnage.Personnage;

public class Shop {
    private List<Objet> stock;

    public Shop() {
        stock = new ArrayList<>();
        genererObjets();
    }

    private void genererObjets() {
        stock.add(Objet.potionCommune());
        stock.add(Objet.potionRare());
        stock.add(Objet.potionLegendaire());
        stock.add(Objet.collierCommun());
        stock.add(Objet.collierRare());
        stock.add(Objet.collierLegendaire());
    }

    public void ouvrir(Personnage joueur, Scanner scanner, List<Objet> inventaire) {
        System.out.println("Bienvenue au magasin !");

        while (true) {
            System.out.println("Ton or : " + joueur.getOr());
            System.out.println("Objets disponibles :");

            for (int i = 0; i < stock.size(); i++) {
                Objet o = stock.get(i);
                System.out.println((i + 1) + ". " + o.getNom() + " - Prix : " + o.getPrix());
            }
            System.out.println("0. Quitter le magasin");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();

            if (choix == 0) break;
            if (choix < 1 || choix > stock.size()) {
                System.out.println("Choix invalide.");
                continue;
            }

            Objet choisi = stock.get(choix - 1);
            if (joueur.getOr() >= choisi.getPrix()) {
                joueur.setOr(joueur.getOr() - choisi.getPrix());
                inventaire.add(choisi);
                System.out.println("Tu as acheté : " + choisi.getNom());
            } else {
                System.out.println("Pas assez d’or.");
            }
        }
    }
}
