package event;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
        if (Objet.potionCommune() != null) stock.add(Objet.potionCommune());
        if (Objet.potionRare() != null) stock.add(Objet.potionRare());
        if (Objet.potionLegendaire() != null) stock.add(Objet.potionLegendaire());
        if (Objet.collierCommun() != null) stock.add(Objet.collierCommun());
        if (Objet.collierRare() != null) stock.add(Objet.collierRare());
        if (Objet.collierLegendaire() != null) stock.add(Objet.collierLegendaire());
        if (Objet.fioleDePoison() != null) stock.add(Objet.fioleDePoison());
    }

    public void ouvrir(Scanner scanner, Inventaire inventaireEquipe, 
                       Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu, int idSauvegardeActivePourMenu) {
        
        System.out.println(Main.ANSI_CYAN + Main.ANSI_BOLD + "=== Bienvenue au magasin ! ===" + Main.ANSI_RESET);

        boolean continuerShopping = true;
        while (continuerShopping && Main.continuerJeuGlobal) {
            System.out.println(Main.ANSI_YELLOW + "\nTon or : " + inventaireEquipe.getOr() + Main.ANSI_RESET);
            System.out.println("Objets disponibles :");

            for (int i = 0; i < stock.size(); i++) {
                Objet o = stock.get(i);
                if (o != null) { 
                    System.out.println("  " + (i + 1) + ". " + o.getNom() + " - Prix : " + Main.ANSI_YELLOW + o.getPrix() + " or" + Main.ANSI_RESET);
                }
            }
            System.out.println("  0. Quitter le magasin");


            int choix = Main.lireIntAvecMenuPause(scanner, 
                                                 "Choix ('M' menu) : ", 
                                                 equipePourMenu, 
                                                 etagePourMenu, 
                                                 nomJoueurPourMenu, 
                                                 idSauvegardeActivePourMenu);

            if (!Main.continuerJeuGlobal || choix == -999) {
                continuerShopping = false; 
                break; 
            }

            if (choix == 0) {
                continuerShopping = false; 
                break;
            }

            if (choix >= 1 && choix <= stock.size()) {
                Objet objetChoisi = stock.get(choix - 1);
                if (objetChoisi != null) {
                    if (inventaireEquipe.getOr() >= objetChoisi.getPrix()) {
                        if (inventaireEquipe.retirerOr(objetChoisi.getPrix())) {
                            if (inventaireEquipe.ajouterObjet(objetChoisi)) {
                                System.out.println(Main.ANSI_GREEN + "Tu as acheté : " + objetChoisi.getNom() + Main.ANSI_RESET);
                            } else {
                                inventaireEquipe.ajouterOr(objetChoisi.getPrix()); 
                                System.out.println(Main.ANSI_RED + "Achat annulé, impossible d'ajouter l'objet à l'inventaire." + Main.ANSI_RESET);
                            }
                        } 
                    } else {
                        System.out.println(Main.ANSI_YELLOW + "Pas assez d’or." + Main.ANSI_RESET);
                    }
                } else {
                     System.out.println(Main.ANSI_RED + "Erreur : Objet non trouvé dans le stock à l'index choisi." + Main.ANSI_RESET);
                }
            } else {
                System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET);
            }
            if (continuerShopping && Main.continuerJeuGlobal) {
                System.out.println(Main.ANSI_YELLOW + "..." + Main.ANSI_RESET);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
        System.out.println("Vous quittez le magasin.");
    }
}