import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import event.Shop;
import inventaire.Inventaire;
import inventaire.Objet;
import monstre.Monstre;
import personnage.Assassin;
import personnage.Barbare;
import personnage.Chevalier;
import personnage.Enchanteur;
import personnage.Necromancien;
import personnage.Paladin;
import personnage.Personnage;
import personnage.Pretre;
import personnage.Pyromancien;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String reponse;
        do {
        	System.out.println("Voulez vous creer un nouveau personnage ? Y/N");
        	reponse = scanner.nextLine();
        }while(!reponse.equals("Y")&&!reponse.equals("N"));
        
        Personnage p1 = null;

        if (reponse.equals("Y")) {
            System.out.println("Veuillez choisir la classe :");
            System.out.println("1 pour Assassin");
            System.out.println("2 pour Barbare");
            System.out.println("3 pour Chevalier");
            System.out.println("4 pour Enchanteur");
            System.out.println("5 pour Necromancien");
            System.out.println("6 pour Paladin");
            System.out.println("7 pour Pretre");
            System.out.println("8 pour Pyromancien");
            
            reponse = scanner.nextLine();

            switch (reponse) {
                case "1":
                    p1 = new Assassin();
                    break;
                case "2":
                    p1 = new Barbare();
                    break;
                case "3":
                    p1 = new Chevalier();
                    break;
                case "4":
                    p1 = new Enchanteur();
                    break;
                case "5":
                    p1 = new Necromancien();
                    break;
                case "6":
                    p1 = new Paladin();
                    break;
                case "7":
                    p1 = new Pretre();
                    break;
                case "8":
                    p1 = new Pyromancien();
                    break;
                default:
                    System.out.println("Choix invalide. Aucun personnage créé.");
                    break;
            }

            if (p1 != null) {
                System.out.println("Voici votre personnage");
                p1.afficherStat();
            }
        }
        
        do {
        	System.out.println("Voulez vous acceder à l'inventaire Y/N");
        	reponse = scanner.nextLine();
        }while(!reponse.equals("Y")&&!reponse.equals("N"));
        if(reponse.equals("Y")) {
        	p1.getInventaire().afficherInventaire();
        }


  
        
        List<Personnage> joueurs = new ArrayList<>();
        List<Monstre> monstres = new ArrayList<>();
        Inventaire inventaire = new Inventaire();

        
        
        while (!joueurs.isEmpty() && !monstres.isEmpty()) {
            afficherEtatEquipes(joueurs, monstres);

            // --- TOUR DES JOUEURS (4 Points d'Action pour l'équipe) ---
            System.out.println("\n=== TOUR DES JOUEURS ===");
            int pointsActionEquipe = 4;

            for (int pa = 0; pa < pointsActionEquipe; pa++) {
                if (joueurs.isEmpty() || monstres.isEmpty()) break;

                Personnage joueurActif = joueurs.get(0);
                System.out.println("\nAction " + (pa + 1) + "/" + pointsActionEquipe + " pour l'équipe. C'est à " + joueurActif.getNom() + " d'agir.");

                System.out.println("Choisissez une action pour " + joueurActif.getNom() + ":");
                System.out.println("  1. Attaquer (" + joueurActif.getForce() + " atk stat)");
                System.out.println("  2. Utiliser un objet");
                System.out.println("  3. Changer de place avec un coéquipier");

                int choixAction = -1;
                while (choixAction < 1 || choixAction > 3) {
                    System.out.print("Votre choix (1-3) : ");
                    if (scanner.hasNextInt()) {
                        choixAction = scanner.nextInt();
                        if (choixAction < 1 || choixAction > 3) {
                            System.out.println("Choix invalide.");
                        }
                    } else {
                        System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                        scanner.next();
                    }
                }
                scanner.nextLine();

                boolean actionEffectuee = true;
                switch (choixAction) {
                    case 1: // Attaquer
                        if (!monstres.isEmpty()) {
                            Monstre monstreCible = monstres.get(0);
                            joueurActif.attaque1(monstreCible);
                            if (!monstreCible.estVivant()) {
                                monstreCible.estMort(inventaire);
                                System.out.println(monstreCible.getNom() + " a été vaincu !");
                                monstres.remove(0);
                            }
                        } else {
                            System.out.println("Il n'y a plus de monstres à attaquer !");
                            actionEffectuee = false;
                        }
                        break;

                    case 2: // Utiliser un objet
                        if (inventaire.getOr() == 0) {
                            System.out.println(joueurActif.getNom() + " n'a pas d'objets !");
                            actionEffectuee = false;
                            pa--;
                            System.out.println("Veuillez choisir une autre action.");
                            continue;
                        }
                        inventaire.afficherInventaire();
                        System.out.println("Choisissez un objet à utiliser (1-6) ou 0 pour annuler :");
                        int choixObjet = scanner.nextInt();
                        scanner.nextLine();
                        if (choixObjet > 0 && choixObjet <= 6) {
                            System.out.println("Choisissez un personnage (1-4) ou 0 pour annuler :");
                            int c = scanner.nextInt();
                            Personnage cible = joueurs.get(c - 1);
                            if (choixObjet != 0) {
                                inventaire.utiliserObjet(choixObjet, joueurActif, cible);
                            } else {
                                System.out.println("Action annulée.");
                                actionEffectuee = false;
                                pa--;
                            }
                        } else {
                            System.out.println("Utilisation d'objet annulée.");
                            actionEffectuee = false;
                            pa--;
                        }
                        break;

                    case 3: // Changer de place
                        if (joueurs.size() < 2) {
                            System.out.println("Pas assez de coéquipiers pour changer de place.");
                            actionEffectuee = false;
                            pa--;
                            System.out.println("Veuillez choisir une autre action.");
                            continue;
                        }
                        System.out.println("Avec quel coéquipier " + joueurActif.getNom() + " doit-il échanger sa place ?");
                        for (int i = 1; i < joueurs.size(); i++) {
                            System.out.println("  " + i + ". " + joueurs.get(i).getNom());
                        }
                        int choixCoequipier = scanner.nextInt();
                        scanner.nextLine();
                        if (choixCoequipier > 0 && choixCoequipier < joueurs.size()) {
                            Collections.swap(joueurs, 0, choixCoequipier);
                            System.out.println(joueurActif.getNom() + " a échangé sa place avec " + joueurs.get(choixCoequipier).getNom() + ".");
                        } else {
                            System.out.println("Changement de place annulé.");
                            actionEffectuee = false;
                            pa--;
                        }
                        break;
                }

                if (actionEffectuee && !joueurs.isEmpty() && joueurs.contains(joueurActif)) {
                    joueurs.remove(0);
                    joueurs.add(joueurActif);
                }
                if (monstres.isEmpty() || joueurs.isEmpty()) break;
            }

            if (joueurs.isEmpty() || monstres.isEmpty()) break;

            // --- TOUR DES MONSTRES ---
            System.out.println("\n=== TOUR DES MONSTRES ===");
            for (Monstre monstreActif : new ArrayList<>(monstres)) {
                if (joueurs.isEmpty()) break;
                Personnage joueurCible = joueurs.get(0);
                monstreActif.attaquer(joueurCible);
                if (!joueurCible.estVivant()) {
                    System.out.println(joueurCible.getNom() + " a été vaincu !");
                    joueurs.remove(0);
                }
            }
        }

        System.out.println("\n-------------------------------------------");
        System.out.println("--- COMBAT TERMINÉ ! ---");
        if (joueurs.isEmpty() && !monstres.isEmpty()) {
            System.out.println("Les monstres ont gagné !");
        } else if (monstres.isEmpty() && !joueurs.isEmpty()) {
            System.out.println("Les joueurs ont gagné ! Félicitations !");
        } else {
            System.out.println("Incroyable ! Égalité parfaite, tout le monde est vaincu !");
        }
        scanner.close();
    }

    public static void afficherEtatEquipes(List<Personnage> joueurs, List<Monstre> monstres) {
        System.out.println("\n--- ÉQUIPES ---");
        System.out.println("Joueurs (" + joueurs.size() + "):");
        if (joueurs.isEmpty()) {
            System.out.println("  (Aucun joueur restant)");
        } else {
            for (int i = 0; i < joueurs.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + joueurs.get(i).getNom());
            }
        }

        System.out.println("Monstres (" + monstres.size() + "):");
        if (monstres.isEmpty()) {
            System.out.println("  (Aucun monstre restant)");
        } else {
            for (int i = 0; i < monstres.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + monstres.get(i).getNom());
            }
        }
        System.out.println("---------------");
    }
}
