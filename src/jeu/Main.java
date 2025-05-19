package jeu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


import event.Shop;
import inventaire.Inventaire; 
import inventaire.Objet;     
import monstre.Monstre;
import personnage.Personnage;
import jeu.Equipe;


public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public static void attendreEntree(Scanner scanner) {
        System.out.print(ANSI_YELLOW + "\nAppuyez sur Entrée pour continuer..." + ANSI_RESET);
        scanner.nextLine();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String continuerLoopGeneral = "Y";
        String nomJoueur;
        int etage = 1;

        Shop shop = new Shop();
        Equipe equipeDuJoueur = new Equipe();

        System.out.println(ANSI_BOLD + ANSI_CYAN + "=== Bienvenue dans le Donjon Infini ! ===" + ANSI_RESET);
        System.out.print("Entrez votre pseudo de héros : " + ANSI_YELLOW);
        nomJoueur = scanner.nextLine();
        System.out.print(ANSI_RESET);

        equipeDuJoueur.choisirEtAjouterPersonnageInitial(scanner);
        if (equipeDuJoueur.getMembres().isEmpty()) {
            System.out.println(ANSI_RED + "Aucun personnage n'a été créé. Fin du jeu." + ANSI_RESET);
            scanner.close();
            return;
        }
        equipeDuJoueur.afficherStatEquipe();
        attendreEntree(scanner);

        boolean victoireJoueurSession = true;

        do {
            System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- Étage " + etage + " ---" + ANSI_RESET);
            System.out.println("Joueur : " + ANSI_YELLOW + nomJoueur + ANSI_RESET);

            String actionPreparation;
            do {
                System.out.println(ANSI_CYAN + "\nQue voulez-vous faire avant de combattre ?" + ANSI_RESET);
                System.out.println("  1. Accéder au magasin");
                System.out.println("  2. Gérer l'inventaire de l'équipe");
                System.out.println("  3. Voir les stats de l'équipe");
                System.out.println(ANSI_GREEN + "  0. Partir au combat !" + ANSI_RESET);
                System.out.print("Votre choix : ");
                actionPreparation = scanner.nextLine();

                switch (actionPreparation) {
                    case "1":
                        shop.ouvrir(scanner, equipeDuJoueur.getInventaireCommun());
                        attendreEntree(scanner);
                        break;
                    case "2":
                        equipeDuJoueur.getInventaireCommun().afficherInventaire();
                        attendreEntree(scanner);
                        break;
                    case "3":
                        equipeDuJoueur.afficherStatEquipe();
                        attendreEntree(scanner);
                        break;
                    case "0":
                        System.out.println(ANSI_GREEN + "En route pour le combat !" + ANSI_RESET);
                        break;
                    default:
                        System.out.println(ANSI_RED + "Choix invalide." + ANSI_RESET);
                        break;
                }
            } while (!actionPreparation.equals("0"));

            System.out.println(ANSI_BOLD + ANSI_RED + "\n=== DÉBUT DU COMBAT - ÉTAGE " + etage + " ===" + ANSI_RESET);
            List<Monstre> monstres = genererMonstresPourEtage(etage);

            if (monstres.isEmpty()) {
                System.out.println(ANSI_GREEN + "Aucun monstre à cet étage ? Étrange... Victoire facile !" + ANSI_RESET);
                victoireJoueurSession = true;
            } else {
                boolean combatTermine = false;
                victoireJoueurSession = false;

                while (!combatTermine) {
                    afficherEtatCombat(equipeDuJoueur, monstres);
                    

                    if (!equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty()) { 
                        System.out.println(ANSI_BOLD + ANSI_GREEN + "\n=== TOUR DE L'ÉQUIPE (" + nomJoueur + ") ===" + ANSI_RESET);
                        int pointsActionEquipe = 4;
                        List<Personnage> personnagesAyantAgiCombat = new ArrayList<>();
                        int paConsommesCeTour = 0;

                        while (paConsommesCeTour < pointsActionEquipe && !equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty()) {
                            System.out.println(ANSI_YELLOW + "\nPoints d'action restants pour l'équipe : " + (pointsActionEquipe - paConsommesCeTour) + ANSI_RESET);
                            System.out.println(ANSI_CYAN + "Choisissez un personnage pour agir :" + ANSI_RESET);
                            for (int i = 0; i < equipeDuJoueur.getMembres().size(); i++) {
                                Personnage p = equipeDuJoueur.getMembres().get(i);
                                System.out.print("  " + (i + 1) + ". " + p.getNom() + " (PV: " + p.getPv() + "/" + p.getPvMax() + ")");
                                if (personnagesAyantAgiCombat.contains(p)) {
                                    System.out.print(ANSI_RED + " [A déjà fait une action de combat]" + ANSI_RESET);
                                }
                                System.out.println();
                            }
                            System.out.print("Votre choix (numéro de personnage) : ");
                            int choixPersoIndex = -1;
                            if (scanner.hasNextInt()) {
                                choixPersoIndex = scanner.nextInt() - 1;
                            } else {
                                scanner.nextLine();
                                System.out.println(ANSI_RED + "Entrée invalide." + ANSI_RESET);
                                continue;
                            }
                            scanner.nextLine();

                            if (choixPersoIndex < 0 || choixPersoIndex >= equipeDuJoueur.getMembres().size()) {
                                System.out.println(ANSI_RED + "Choix de personnage invalide." + ANSI_RESET);
                                continue;
                            }

                            Personnage joueurActif = equipeDuJoueur.getMembres().get(choixPersoIndex);
                            System.out.println("\nC'est à " + ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " d'agir.");

                            System.out.println(ANSI_CYAN + "Choisissez une action :" + ANSI_RESET);
                            System.out.println("  1. Attaquer (Compétence 1)");
                            if (joueurActif.getUltLvl() > 0) {
                                System.out.println("  2. Utiliser Ultime");
                            }
                            System.out.println("  3. Utiliser un objet");
                            System.out.println("  4. Changer de place");
                            System.out.println("  0. Passer ce Point d'Action");
                            System.out.print("Votre choix : ");

                            int choixActionJoueur = -1;
                            if (scanner.hasNextInt()) {
                                choixActionJoueur = scanner.nextInt();
                            } else {
                                scanner.nextLine();
                                System.out.println(ANSI_RED + "Entrée invalide." + ANSI_RESET);
                                continue;
                            }
                            scanner.nextLine();

                            boolean actionDeCombatPourCePa = false;
                            boolean paUtilisePourObjet = false;

                            switch (choixActionJoueur) {
                            
                            case 1: 
                                if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                    System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat ce tour." + ANSI_RESET);
                                    continue; 
                                }

                                if (!monstres.isEmpty()) {
                                    Monstre monstreCible = selectionnerCibleMonstre(monstres, scanner);
                                    if (monstreCible != null) {
                                        System.out.println(ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " utilise sa compétence 1 sur " + ANSI_RED + monstreCible.getNom() + ANSI_RESET + " !");
                                        joueurActif.attaque1(monstreCible); 

                                        
                                        if (!monstreCible.estVivant()) {
                                            System.out.println(ANSI_GREEN + monstreCible.getNom() + " a été vaincu !" + ANSI_RESET);
                                            
                                            
                                            joueurActif.gagnerXp(monstreCible.getXpDonnee()); 
                                            
                                            
                                            equipeDuJoueur.getInventaireCommun().ajouterOr(monstreCible.getOrM()); 
                                            System.out.println("L'équipe gagne " + ANSI_YELLOW + monstreCible.getOrM() + " or." + ANSI_RESET);
                                            
                                            monstres.remove(monstreCible); 
                                        }
                                        actionDeCombatPourCePa = true; 
                                        personnagesAyantAgiCombat.add(joueurActif); 
                                    } else {
                                        
                                        System.out.println(ANSI_YELLOW + "Aucune cible sélectionnée. Action annulée." + ANSI_RESET);
                                        continue; 
                                    }
                                } else {
                                    System.out.println(ANSI_YELLOW + "Il n'y a plus de monstres à attaquer !" + ANSI_RESET);
                                    continue;
                                }
                                break; 
                                case 2: 
                                    if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                        System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat ce tour." + ANSI_RESET);
                                        continue;
                                    }
                                    if (choixActionJoueur == 2 && joueurActif.getUltLvl() == 0) {
                                        System.out.println(ANSI_RED + joueurActif.getNom() + " n'a pas encore débloqué son ultime !" + ANSI_RESET);
                                        continue;
                                    }

                                    if (!monstres.isEmpty()) {
                                        Monstre monstreCible = selectionnerCibleMonstre(monstres, scanner);
                                        if (monstreCible != null) {
                                            if (choixActionJoueur == 1) joueurActif.attaque1(monstreCible);
                                            else joueurActif.ulti(monstreCible);

                                            if (!monstreCible.estVivant()) {
                                                System.out.println(ANSI_GREEN + monstreCible.getNom() + " a été vaincu !" + ANSI_RESET);
                                                joueurActif.gagnerXp(monstreCible.getXpDonnee());
                                                equipeDuJoueur.getInventaireCommun().ajouterOr(monstreCible.getOrM());
                                                System.out.println("L'équipe gagne " + ANSI_YELLOW + monstreCible.getOrM() + " or." + ANSI_RESET);
                                                monstres.remove(monstreCible);
                                            }
                                            actionDeCombatPourCePa = true;
                                            personnagesAyantAgiCombat.add(joueurActif);
                                        } else { System.out.println(ANSI_YELLOW+"Action annulée (pas de cible)."+ANSI_RESET); }
                                    } else { System.out.println(ANSI_YELLOW + "Il n'y a plus de monstres !" + ANSI_RESET); }
                                    break;

                                case 3: 
                                    if (equipeDuJoueur.getInventaireCommun().estVide()) {
                                        System.out.println(ANSI_YELLOW + "L'inventaire de l'équipe est vide !" + ANSI_RESET);
                                        continue;
                                    }
                                    if (equipeDuJoueur.getInventaireCommun().utiliserObjetInteractive(scanner, joueurActif, equipeDuJoueur.getMembres())) {
                                        paUtilisePourObjet = true;
                                    }
                                    break;

                                case 4: // Changer de place
                                     if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                        System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat ce tour." + ANSI_RESET);
                                        continue;
                                    }
                                    if (equipeDuJoueur.changerDePlace(scanner, joueurActif)) {
                                        actionDeCombatPourCePa = true;
                                        personnagesAyantAgiCombat.add(joueurActif);
                                    }
                                    break;
                                case 0:
                                    System.out.println(joueurActif.getNom() + " passe ce Point d'Action.");
                                    // Ne fait rien d'autre, le PA sera consommé à la fin de cette itération de la boucle while
                                    break; // Assure que actionDeCombatPourCePa et paUtilisePourObjet restent false
                                default:
                                    System.out.println(ANSI_RED + "Choix d'action invalide." + ANSI_RESET);
                                    continue;
                            }

                            // Logique de consommation de PA
                            if (actionDeCombatPourCePa) {
                                paConsommesCeTour++;
                                equipeDuJoueur.rotationApresAction(joueurActif);
                            } else if (paUtilisePourObjet) {
                                paConsommesCeTour++;
                                // Pas de rotation ici, le joueur peut encore faire une action de combat
                            } else if (choixActionJoueur == 0) { // Si l'action était explicitement "Passer"
                                paConsommesCeTour++;
                            }
                            // Si aucune action valide n'a été faite qui consomme un PA (ex: choix invalide, tentative d'action déjà faite),
                            // la boucle des PA se répète sans incrémenter paConsommesCeTour,
                            // redemandant une action pour le même personnage ou permettant de changer de personnage.

                            if (monstres.isEmpty() || equipeDuJoueur.getMembres().isEmpty()) break;
                        }
                    }


                    // --- TOUR DES MONSTRES ---
                    // Vérifier si le combat n'est pas déjà terminé avant le tour des monstres
                    if (equipeDuJoueur.getMembres().isEmpty() || monstres.isEmpty()) {
                        combatTermine = true;
                    } else {
                        System.out.println(ANSI_BOLD + ANSI_RED + "\n=== TOUR DES MONSTRES ===" + ANSI_RESET);
                        for (Monstre monstreActif : new ArrayList<>(monstres)) {
                            if (!monstreActif.estVivant()) continue;
                            if (equipeDuJoueur.getMembres().isEmpty()) break;

                            Personnage joueurCible = equipeDuJoueur.getLeader();
                            if (joueurCible != null) {
                                System.out.println(monstreActif.getNom() + " (PV: " + monstreActif.getPv() + ") attaque " + ANSI_YELLOW + joueurCible.getNom() + ANSI_RESET + " !");
                                monstreActif.attaquer(joueurCible); // La méthode attaquer de Monstre doit exister
                                if (!joueurCible.estVivant()) {
                                    System.out.println(ANSI_RED + joueurCible.getNom() + " a été vaincu !" + ANSI_RESET);
                                    equipeDuJoueur.getMembres().remove(joueurCible);
                                    if (equipeDuJoueur.getMembres().isEmpty()) {
                                        System.out.println(ANSI_RED + ANSI_BOLD + "Toute l'équipe a été vaincue..." + ANSI_RESET);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    attendreEntree(scanner); // Pause après le tour des monstres

                    // Vérification de fin de combat
                    if (equipeDuJoueur.getMembres().isEmpty()) {
                        victoireJoueurSession = false;
                        combatTermine = true;
                    } else if (monstres.isEmpty()) {
                        victoireJoueurSession = true;
                        combatTermine = true;
                    }
                }
            }

            System.out.println(ANSI_BOLD + ANSI_BLUE + "\n-------------------------------------------" + ANSI_RESET);
            System.out.println(ANSI_BOLD + ANSI_BLUE + "--- COMBAT DE L'ÉTAGE " + etage + " TERMINÉ ! ---" + ANSI_RESET);
            if (victoireJoueurSession) {
                System.out.println(ANSI_GREEN + ANSI_BOLD + "Victoire ! Vous avez vaincu tous les monstres de l'étage." + ANSI_RESET);
                etage++;
                equipeDuJoueur.proposerChoixNouveauPersonnage(scanner, etage); // Logique à implémenter dans Equipe
            } else {
                System.out.println(ANSI_RED + ANSI_BOLD + "Défaite... Votre équipe a été anéantie." + ANSI_RESET);
                System.out.print("Voulez-vous tenter une nouvelle aventure ? (Y/N) : ");
                String retry = scanner.nextLine();
                if (retry.equalsIgnoreCase("Y")) {
                    etage = 1;
                    equipeDuJoueur = new Equipe();
                    equipeDuJoueur.choisirEtAjouterPersonnageInitial(scanner);
                    if (equipeDuJoueur.getMembres().isEmpty()) {
                        System.out.println(ANSI_RED + "Création de personnage annulée. Fin du jeu." + ANSI_RESET);
                        continuerLoopGeneral = "N";
                    }
                    victoireJoueurSession = true; // Pour permettre à la boucle de continuer si on recommence
                } else {
                    continuerLoopGeneral = "N";
                }
            }

            if (continuerLoopGeneral.equalsIgnoreCase("Y") && victoireJoueurSession) {
                 // Si on a gagné le combat ou si on a choisi de recommencer après une défaite et que la création a réussi.
                System.out.print(ANSI_CYAN + "\nPrêt pour l'étage " + etage + " ? (Y pour continuer, N pour quitter cette session) : " + ANSI_RESET);
                String continuerProchainEtage = scanner.nextLine();
                if (!continuerProchainEtage.equalsIgnoreCase("Y")) {
                    continuerLoopGeneral = "N";
                }
            } else if (!victoireJoueurSession && !continuerLoopGeneral.equalsIgnoreCase("Y")){
                // Si défaite et on ne recommence pas, la boucle s'arrêtera.
            }


        } while (continuerLoopGeneral.equalsIgnoreCase("Y"));

        System.out.println(ANSI_BOLD + ANSI_CYAN + "\nFin de la partie. Merci d'avoir joué !" + ANSI_RESET);
        scanner.close();
    }


    public static void afficherEtatCombat(Equipe equipe, List<Monstre> monstres) {
        System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- ÉTAT DU CHAMP DE BATAILLE ---" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "Votre Équipe (" + equipe.getMembres().size() + "):" + ANSI_RESET);
        if (equipe.getMembres().isEmpty()) {
            System.out.println("  (Aucun héros debout...)");
        } else {
            for (int i = 0; i < equipe.getMembres().size(); i++) {
                Personnage p = equipe.getMembres().get(i);
                System.out.printf("  %d. %s%-25s%s - PV: %s%3d/%-3d%s %s\n",
                        (i + 1),
                        ANSI_YELLOW, p.getNom(), ANSI_RESET,
                        ANSI_GREEN, p.getPv(), p.getPvMax(), ANSI_RESET,
                        "" /* p.afficherEmojisEffets() // TODO */);
            }
        }

        System.out.println(ANSI_RED + "\nMonstres Restants (" + monstres.size() + "):" + ANSI_RESET);
        if (monstres.isEmpty()) {
            System.out.println("  (Plus aucun monstre !)");
        } else {
            for (int i = 0; i < monstres.size(); i++) {
                Monstre m = monstres.get(i);
                 System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s %s\n",
                        (i + 1),
                        ANSI_YELLOW, m.getNom(), ANSI_RESET,
                        ANSI_RED, m.getPv(), ANSI_RESET,
                        "" /* m.afficherEmojisEffets() // TODO */);
            }
        }
        System.out.println(ANSI_BLUE + "-----------------------------" + ANSI_RESET);
    }

    public static List<Monstre> genererMonstresPourEtage(int etage) {
        List<Monstre> groupeMonstres = new ArrayList<>();
        Random random = new Random();
        int nbMonstresBase = random.nextInt(3) + 3; // 3 à 5 monstres

        if (etage > 0 && etage % 10 == 0) {
            System.out.println(ANSI_RED + ANSI_BOLD + "!!! UN BOSS APPARAÎT !!!" + ANSI_RESET);
            groupeMonstres.add(Monstre.boss(etage)); // La méthode Monstre.boss() doit exister
        } else {
            for (int i = 0; i < nbMonstresBase; i++) {
                // Logique de génération simplifiée pour le test
                if (etage < 5) groupeMonstres.add(Monstre.monstreFaible());
                else if (etage < 15) groupeMonstres.add(Monstre.monstreMoyen());
                else groupeMonstres.add(Monstre.monstreFort());
            }
        }

        if (!groupeMonstres.isEmpty()) {
             System.out.println(ANSI_YELLOW + groupeMonstres.size() + " monstres apparaissent !" + ANSI_RESET);
        }
        return groupeMonstres;
    }

    public static Monstre selectionnerCibleMonstre(List<Monstre> monstres, Scanner scanner) {
        if (monstres.isEmpty()) return null;
        if (monstres.size() == 1) {
            System.out.println("Cible automatique : " + monstres.get(0).getNom());
            return monstres.get(0);
        }

        System.out.println(ANSI_CYAN + "Choisissez une cible parmi les monstres :" + ANSI_RESET);
        for (int i = 0; i < monstres.size(); i++) {
             System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s\n",
                        (i + 1),
                        ANSI_YELLOW, monstres.get(i).getNom(), ANSI_RESET,
                        ANSI_RED, monstres.get(i).getPv(), ANSI_RESET);
        }
        System.out.print("Votre choix (numéro) : ");
        int choixCibleIndex = -1;
        if (scanner.hasNextInt()) {
            choixCibleIndex = scanner.nextInt() - 1;
        } else {
            scanner.nextLine();
            System.out.println(ANSI_RED + "Entrée invalide. Cible par défaut : premier monstre." + ANSI_RESET);
            return monstres.get(0);
        }
        scanner.nextLine();

        if (choixCibleIndex >= 0 && choixCibleIndex < monstres.size()) {
            return monstres.get(choixCibleIndex);
        } else {
            System.out.println(ANSI_RED + "Choix de cible invalide. Cible par défaut : premier monstre." + ANSI_RESET);
            return monstres.get(0);
        }
    }
}