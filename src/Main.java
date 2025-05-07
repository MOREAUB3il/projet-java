import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import inventaire.Inventaire;
import inventaire.Objet;
import monstre.Monstre;
import personnage.Personnage;
import personnage.Joueur;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Personnage> joueurs = new ArrayList<>();
        List<Monstre> monstres = new ArrayList<>();
        Inventaire inventaire = new Inventaire();

       /*
        joueurs.add(new Joueur("Héros Alpha", 120, 20));
        joueurs.add(new Joueur("Mage Beta", 80, 15));
        joueurs.add(new Joueur("Guerrier Gamma", 150, 25));
        joueurs.add(new Joueur("Soigneur Delta", 100, 10)); 

        monstres.add(new Monstre("Gobelin Chef", 100, 18, 10, 0.1, 20));
        monstres.add(new Monstre("Orc Berserker", 150, 22, 15, 0.2, 30));
        monstres.add(new Monstre("Chaman Gobelin", 70, 15, 8, 0.15, 25));
        monstres.add(new Monstre("Loup Féroce", 90, 16, 12, 0.1, 15));

        System.out.println("--- Le combat commence ! ---");
        int numeroTourGlobal = 1;
*/
        // Boucle principale du jeu
        while (!joueurs.isEmpty() && !monstres.isEmpty()) {
            System.out.println("\n-------------------------------------------");
            System.out.println("--- TOUR GLOBAL N°" + numeroTourGlobal + " ---");
            System.out.println("-------------------------------------------");

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
                System.out.println("  4. Passer son tour (ce personnage)");

                int choixAction = -1;
                while (choixAction < 1 || choixAction > 4) {
                    System.out.print("Votre choix (1-4) : ");
                    if (scanner.hasNextInt()) {
                        choixAction = scanner.nextInt();
                        if (choixAction < 1 || choixAction > 4) {
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

                    case 4: // Passer son tour
                        System.out.println(joueurActif.getNom() + " passe son tour.");
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
            numeroTourGlobal++;
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
