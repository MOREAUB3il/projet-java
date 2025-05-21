package event;

import java.util.*;
import inventaire.Objet;
import jeu.Equipe;
import jeu.Main;
import inventaire.Inventaire;

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
        stock.add(Objet.fioleDePoison());
    }

    public void ouvrir(Scanner scanner, Inventaire inventaire,Equipe equipeDuJoueur,int etage,String nomJoueur ) {
        System.out.println(Main.ANSI_CYAN+ Main.ANSI_BOLD + "=== Bienvenue au magasin ! ===" + Main.ANSI_RESET);

        while (true) {
            System.out.println(Main.ANSI_YELLOW + "Ton or : " + inventaire.getOr() + Main.ANSI_RESET);
            System.out.println("Objets disponibles :");

            for (int i = 0; i < stock.size(); i++) {
                Objet o = stock.get(i);
                System.out.println((i + 1) + ". " + o.getNom() + " - Prix : " + Main.ANSI_YELLOW+ o.getPrix() + " or" + Main.ANSI_RESET );
            }
            System.out.println("0. Quitter le magasin");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();

            if (choix == 0) break;
            if (choix < 1 || choix > stock.size()) {
                System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET);
                continue;
            }

            Objet choisi = stock.get(choix - 1);
            if (inventaire.getOr() >= choisi.getPrix()) {
                inventaire.setOr(inventaire.getOr() - choisi.getPrix());
                inventaire.ajouterObjet(choisi);
                System.out.println(Main.ANSI_GREEN + "Tu as acheté : " + choisi.getNom() + Main.ANSI_RESET);
            } else {
                System.out.println(Main.ANSI_YELLOW + "Pas assez d’or." + Main.ANSI_RESET);
            }
        }
    }
}
