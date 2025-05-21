package jeu; 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


import event.Shop;
import monstre.Monstre;
import personnage.Personnage;
import personnage.Paladin;



public class Main {

    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BOLD = "\u001B[1m";

    
    public static boolean continuerJeuGlobal = true;

    // private static Database database; // Instance de votre classe de base de données

    public static String lireStringAvecMenuPause(Scanner scanner, String prompt, Equipe equipe, int etage, String nomJoueur) {
        String input;
        while (continuerJeuGlobal) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("M")) {
                afficherMenuPause(scanner, equipe, etage, nomJoueur); 
                if (!continuerJeuGlobal) return "QUIT_GAME_INTERNAL"; 
                
            } else {
                return input; 
            }
        }
        return "QUIT_GAME_INTERNAL"; 
    }

    public static int lireIntAvecMenuPause(Scanner scanner, String prompt, Equipe equipe, int etage, String nomJoueur) {
        String input;
        while (continuerJeuGlobal) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("M")) {
                afficherMenuPause(scanner, equipe, etage, nomJoueur);
                if (!continuerJeuGlobal) return -999; 
            } else {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_RED + "Veuillez entrer un nombre valide." + ANSI_RESET);
                    
                }
            }
        }
        return -999; 
    }


    public static void afficherMenuPause(Scanner scanner, Equipe equipe, int etage, String nomJoueur) {
        System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- PAUSE ---" + ANSI_RESET);
        String choixMenuPause;
        boolean resterEnPause = true;

        while (resterEnPause && continuerJeuGlobal) { 
            System.out.println(ANSI_CYAN + "Choisissez une option :" + ANSI_RESET);
            System.out.println("  1. Reprendre la partie");
            System.out.println("  2. Sauvegarder et Quitter");
            System.out.println("  3. Quitter sans sauvegarder");
            System.out.print("Votre choix : ");
            choixMenuPause = scanner.nextLine(); 

            switch (choixMenuPause) {
                case "1":
                    System.out.println(ANSI_GREEN + "Reprise de la partie..." + ANSI_RESET);
                    resterEnPause = false;
                    break;
                case "2":
                    System.out.println(ANSI_YELLOW + "Sauvegarde de la progression..." + ANSI_RESET);
                    /*
                    if (database != null) {
                        try {
                            int sauvegardeId = database.createSauvegarde(nomJoueur, etage);
                            if (sauvegardeId != -1) {
                               database.sauvegarderEquipe(equipe.getMembres(), sauvegardeId);
                               // TODO: Sauvegarder l'inventaire de l'équipe aussi
                               System.out.println("Partie sauvegardée avec succès !");
                            } else {
                               System.out.println(ANSI_RED + "Erreur lors de la création de la sauvegarde." + ANSI_RESET);
                            }
                        } catch (Exception e) {
                            System.err.println(ANSI_RED + "Erreur lors de la sauvegarde : " + e.getMessage() + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED + "Système de base de données non initialisé." + ANSI_RESET);
                    }
                    */
                    System.out.println("Partie sauvegardée (simulation)."); // Placeholder
                    System.out.println("Merci d'avoir joué !");
                    continuerJeuGlobal = false;
                    resterEnPause = false;
                    break;
                case "3":
                    System.out.print(ANSI_RED + "Êtes-vous sûr de vouloir quitter sans sauvegarder ? (Y/N) : " + ANSI_RESET);
                    String confirmation = scanner.nextLine();
                    if (confirmation.equalsIgnoreCase("Y")) {
                        System.out.println("Merci d'avoir joué !");
                        continuerJeuGlobal = false;
                        resterEnPause = false;
                    } else {
                        System.out.println(ANSI_GREEN + "Retour au menu pause." + ANSI_RESET);
                    }
                    break;
                default:
                    System.out.println(ANSI_RED + "Choix invalide." + ANSI_RESET);
                    break;
            }
        }
        System.out.println();
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // database = new Database();
        // database.connect();
        // database.createTables();

        String continuerSession = "Y";
        String nomJoueur = "";
        
        System.out.println(ANSI_BOLD + ANSI_CYAN + "=== Bienvenue dans le Donjon Infini ! ===" + ANSI_RESET);
        
        while(continuerSession.equalsIgnoreCase("Y") && continuerJeuGlobal) {
            int etage = 1;
            Equipe equipeDuJoueur = new Equipe();
            boolean victoireJoueurCombat = true; 

            if (nomJoueur.isEmpty() || !continuerSession.equalsIgnoreCase("Y_RELOAD")) { 
                nomJoueur = lireStringAvecMenuPause(scanner, "Entrez votre pseudo de héros ('M' pour menu): " + ANSI_YELLOW, equipeDuJoueur, etage, ""); 
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(nomJoueur)) { continuerSession = "N"; break; }
                System.out.print(ANSI_RESET);
            }
            // TODO: Ajouter une option de chargement de partie ici si 'database' est actif

            equipeDuJoueur.choisirEtAjouterPersonnageInitial(scanner, equipeDuJoueur, etage, nomJoueur); 
            if (!continuerJeuGlobal) { continuerSession = "N"; break; }

            if (equipeDuJoueur.getMembres().isEmpty()) {
                System.out.println(ANSI_RED + "Aucun personnage n'a été créé. Fin du jeu." + ANSI_RESET);
                continuerSession = "N";
                break;
            }
            equipeDuJoueur.afficherStatEquipe();
            String pauseApresStats = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nAppuyez sur Entrée pour continuer ('M' pour menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
            if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(pauseApresStats)) { continuerSession = "N"; break;}

            while (victoireJoueurCombat && continuerJeuGlobal) {
                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- Étage " + etage + " ---" + ANSI_RESET);
                System.out.println("Joueur : " + ANSI_YELLOW + nomJoueur + ANSI_RESET);

                String actionPreparation;
                do {
                    actionPreparation = lireStringAvecMenuPause(scanner, 
                        ANSI_CYAN + "\nQue faire avant le combat ? ('M' menu)\n" + ANSI_RESET +
                        "  1. Magasin\n  2. Inventaire\n  3. Stats Équipe\n" +
                        ANSI_GREEN + "  0. Partir au combat !\n" + ANSI_RESET +
                        "Votre choix : ", equipeDuJoueur, etage, nomJoueur);

                    if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;

                    switch (actionPreparation) {
                        case "1": new Shop().ouvrir(scanner, equipeDuJoueur.getInventaireCommun(), equipeDuJoueur, etage, nomJoueur); break; 
                        case "2": equipeDuJoueur.getInventaireCommun().afficherInventaire(); break;
                        case "3": equipeDuJoueur.afficherStatEquipe(); break; 
                        case "0": System.out.println(ANSI_GREEN + "En route pour le combat !" + ANSI_RESET); break;
                        default: System.out.println(ANSI_RED + "Choix invalide." + ANSI_RESET); break;
                    }
                     if (continuerJeuGlobal && !actionPreparation.equals("0") && !"QUIT_GAME_INTERNAL".equals(actionPreparation) ) { 
                        String pauseApresActionPrep = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nAppuyez sur Entrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                         if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(pauseApresActionPrep)) { actionPreparation = "QUIT_GAME_INTERNAL"; break;}
                     }

                } while (!actionPreparation.equals("0") && continuerJeuGlobal);
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;

                System.out.println(ANSI_BOLD + ANSI_RED + "\n=== DÉBUT DU COMBAT - ÉTAGE " + etage + " ===" + ANSI_RESET);
                List<Monstre> monstres = genererMonstresPourEtage(etage);
                victoireJoueurCombat = false;

                if (monstres.isEmpty() && continuerJeuGlobal) {
                    System.out.println(ANSI_GREEN + "Aucun monstre. Victoire facile !" + ANSI_RESET);
                    victoireJoueurCombat = true;
                } else {
                    boolean combatTermine = false;
                    while (!combatTermine && continuerJeuGlobal) {
                        afficherEtatCombat(equipeDuJoueur, monstres);
                        
                        // --- TOUR DES JOUEURS ---
                        if (!equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty() && continuerJeuGlobal) {
                            System.out.println(ANSI_BOLD + ANSI_GREEN + "\n=== TOUR DE L'ÉQUIPE (" + nomJoueur + ") ===" + ANSI_RESET);
                            int pointsActionEquipe = 4;
                            List<Personnage> personnagesAyantAgiCombat = new ArrayList<>();
                            int paConsommesCeTour = 0;

                            while (paConsommesCeTour < pointsActionEquipe && !equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty() && continuerJeuGlobal) {
                                System.out.println(ANSI_YELLOW + "\nPoints d'action restants pour l'équipe : " + (pointsActionEquipe - paConsommesCeTour) + ANSI_RESET);
                                System.out.println(ANSI_CYAN + "Perso à faire agir (0 pour passer les PA si tous ont agi, 'M' menu) :" + ANSI_RESET);
                                boolean tousOntAgi = true;
                                for (int i = 0; i < equipeDuJoueur.getMembres().size(); i++) {
                                    Personnage p = equipeDuJoueur.getMembres().get(i);
                                    System.out.print("  " + (i + 1) + ". " + p.getNom());
                                    if (personnagesAyantAgiCombat.contains(p)) System.out.print(ANSI_RED+" [Déjà agi combat]"+ANSI_RESET);
                                    else tousOntAgi = false;
                                    System.out.println(" (PV: " + p.getPv() + "/" + p.getPvMax() + ")");
                                }

                                if (tousOntAgi && !equipeDuJoueur.getMembres().isEmpty()) {
                                    System.out.println(ANSI_YELLOW + "Tous les persos ont fait une action de combat. PA restants passés." + ANSI_RESET);
                                    paConsommesCeTour = pointsActionEquipe; continue;
                                }
                                
                                int choixPersoInput = lireIntAvecMenuPause(scanner, "Votre choix (numéro) : ", equipeDuJoueur, etage, nomJoueur);
                                if (!continuerJeuGlobal || choixPersoInput == -999) { combatTermine = true; break; }

                                if (choixPersoInput == 0) {
                                    if (tousOntAgi) { paConsommesCeTour = pointsActionEquipe; continue; }
                                    else { System.out.println(ANSI_RED + "Utilisez l'option 'Passer PA' dans le menu d'action." + ANSI_RESET); continue; }
                                }
                                int choixPersoIndex = choixPersoInput - 1;
                                if (choixPersoIndex < 0 || choixPersoIndex >= equipeDuJoueur.getMembres().size()) {
                                    System.out.println(ANSI_RED + "Choix de personnage invalide." + ANSI_RESET); continue;
                                }
                                Personnage joueurActif = equipeDuJoueur.getMembres().get(choixPersoIndex);
                                joueurActif.mettreAJourEffets();
                                if (!joueurActif.estVivant()) {
                                    System.out.println(ANSI_RED + joueurActif.getNom() + " a succombé à ses effets !" + ANSI_RESET);
                                    equipeDuJoueur.getMembres().remove(joueurActif);
                                    if (equipeDuJoueur.getMembres().isEmpty()) { combatTermine = true; break; }
                                    continue;
                                }

                                System.out.println("\nÀ " + ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " d'agir. ('M' menu)");
                                System.out.println("  1. Attaquer (Comp1) " + (personnagesAyantAgiCombat.contains(joueurActif) ? ANSI_RED+"[Déjà agi combat]"+ANSI_RESET : ""));
                                if (joueurActif.getUltLvl() > 0) System.out.println("  2. Ultime "  + (personnagesAyantAgiCombat.contains(joueurActif) ? ANSI_RED+"[Déjà agi combat]"+ANSI_RESET : ""));
                                System.out.println("  3. Utiliser Objet");
                                System.out.println("  4. Changer Place " + (personnagesAyantAgiCombat.contains(joueurActif) ? ANSI_RED+"[Déjà agi combat]"+ANSI_RESET : ""));
                                System.out.println("  0. Passer ce PA");
                                
                                int choixActionJoueur = lireIntAvecMenuPause(scanner, "Action : ", equipeDuJoueur, etage, nomJoueur);
                                if (!continuerJeuGlobal || choixActionJoueur == -999) { combatTermine = true; break; }

                                boolean actionCombatFaiteCePa = false;
                                boolean objetUtiliseCePa = false;

                                switch (choixActionJoueur) {

                                    case 1:
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) { System.out.println(ANSI_RED + "Action de combat déjà faite." + ANSI_RESET); continue; }
                                        if (monstres.isEmpty()) { System.out.println(ANSI_YELLOW+"Plus de cibles!"+ANSI_RESET); continue; }
                                        
                                        Monstre cibleAtt = selectionnerCibleMonstreInteractive(scanner, monstres, equipeDuJoueur, etage, nomJoueur);
                                        if (!continuerJeuGlobal || cibleAtt == null && monstres.size() > 1) { if(!continuerJeuGlobal) combatTermine = true; break; } 
                                        if (cibleAtt == null && monstres.size() ==1) cibleAtt = monstres.get(0); 
                                        if (cibleAtt == null) continue; 
                                        joueurActif.attaque1(cibleAtt);
                                        
                                        if (!cibleAtt.estVivant()) { 
                                        	System.out.println(ANSI_GREEN + cibleAtt.getNom() + " a été vaincu !" + ANSI_RESET);
                                        
	                                        joueurActif.gagnerXp(cibleAtt.getXpDonnee());
	                                        equipeDuJoueur.getInventaireCommun().ajouterOr(cibleAtt.getOrM());
	                                        System.out.println("L'équipe gagne " + ANSI_YELLOW + cibleAtt.getOrM() + " or." + ANSI_RESET);
	                                        
	                                        monstres.remove(cibleAtt); 
	
	                                        if (!monstres.isEmpty() && continuerJeuGlobal) {
	                                            String pauseApresKill = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nAppuyez sur Entrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
	                                            if ("QUIT_GAME_INTERNAL".equals(pauseApresKill) || !continuerJeuGlobal) { combatTermine = true;}
	                                        } else if (monstres.isEmpty()) {
	                                            System.out.println(ANSI_GREEN + "Tous les monstres ont été vaincus !" + ANSI_RESET);
	                                            victoireJoueurCombat = true; 
	                                            combatTermine = true;     

	                                        }
                                        }
                                        actionCombatFaiteCePa = true;
                                        personnagesAyantAgiCombat.add(joueurActif);
                                        break;
                                    case 2: // Utiliser Ultime
                                        if (joueurActif.getUltLvl() == 0) {
                                            System.out.println(ANSI_RED + joueurActif.getNom() + " n'a pas encore débloqué son ultime !" + ANSI_RESET);
                                            continue;
                                        }
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                            System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat." + ANSI_RESET);
                                            continue;
                                        }

                                        Monstre cibleUlti = null;
                                        if (!(joueurActif instanceof Paladin)) {
                                            if (monstres.isEmpty()) {
                                                System.out.println(ANSI_YELLOW + "Plus de cibles pour l'ultime !" + ANSI_RESET);
                                                continue;
                                            }
                                            System.out.println(ANSI_CYAN + "Choisissez une cible pour l'ultime ('M' menu) :" + ANSI_RESET);
                                            for (int i = 0; i < monstres.size(); i++) {
                                                System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s\n",
                                                        (i + 1), ANSI_YELLOW, monstres.get(i).getNom(), ANSI_RESET,
                                                        ANSI_RED, monstres.get(i).getPv(), ANSI_RESET);
                                            }
                                            cibleUlti = selectionnerCibleMonstreInteractive(scanner, monstres, equipeDuJoueur, etage, nomJoueur);
                                            if (!continuerJeuGlobal) { combatTermine = true; break; }
                                            if (cibleUlti == null) {
                                                System.out.println(ANSI_YELLOW + "Aucune cible sélectionnée pour l'ultime." + ANSI_RESET);
                                                continue;
                                            }
                                        }

                                        System.out.println(ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " déchaîne son ULTIME " + (cibleUlti != null ? "sur " + ANSI_RED + cibleUlti.getNom() : "") + ANSI_RESET + " !");
                                        joueurActif.ulti(cibleUlti); 

                                        if (joueurActif instanceof Paladin) {
                                            equipeDuJoueur.deplacerMembreAuDebut(joueurActif);
                                        }

                                        if (cibleUlti != null && !cibleUlti.estVivant()) {
                                            System.out.println(ANSI_GREEN + cibleUlti.getNom() + " a été pulvérisé par l'ultime !" + ANSI_RESET);
                                            joueurActif.gagnerXp(cibleUlti.getXpDonnee());
                                            equipeDuJoueur.getInventaireCommun().ajouterOr(cibleUlti.getOrM());
                                            System.out.println("L'équipe gagne " + ANSI_YELLOW + cibleUlti.getOrM() + " or." + ANSI_RESET);
                                            monstres.remove(cibleUlti);
                                            if (monstres.isEmpty()) {
                                                System.out.println(ANSI_GREEN + "Tous les monstres ont été vaincus !" + ANSI_RESET);
                                                victoireJoueurCombat = true;
                                                combatTermine = true;
                                            } else if (continuerJeuGlobal) {
                                                String pauseApresKillUlti = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nAppuyez sur Entrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                                                if ("QUIT_GAME_INTERNAL".equals(pauseApresKillUlti) || !continuerJeuGlobal) { combatTermine = true; }
                                            }
                                        }
                                        actionCombatFaiteCePa = true;
                                        personnagesAyantAgiCombat.add(joueurActif);
                                        break;

                                    case 3: // Utiliser Objet
                                        if (equipeDuJoueur.getInventaireCommun().estVide()) {
                                            System.out.println(ANSI_YELLOW + "L'inventaire de l'équipe est vide !" + ANSI_RESET);
                                            continue;
                                        }
                                        System.out.println(joueurActif.getNom() + " fouille dans son sac...");
                                        if (equipeDuJoueur.getInventaireCommun().utiliserObjetInteractive(
                                                scanner,
                                                joueurActif,
                                                equipeDuJoueur.getMembres(), 
                                                monstres, 
                                                equipeDuJoueur, 
                                                choixActionJoueur, 
                                                actionPreparation
                                            )) {
                                            objetUtiliseCePa = true;
                                        }
                                        if (!continuerJeuGlobal) { combatTermine = true; }
                                        break;

                                    case 4: // Changer Place
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                            System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat." + ANSI_RESET);
                                            continue;
                                        }
                                        if (equipeDuJoueur.changerDePlace(
                                                scanner,
                                                joueurActif
                                            )) {
                                            actionCombatFaiteCePa = true;
                                            personnagesAyantAgiCombat.add(joueurActif);
                                        }
                                        if (!continuerJeuGlobal) { combatTermine = true; }
                                        break;
                                     case 0:
                                        System.out.println(joueurActif.getNom() + " passe ce PA.");
                                        
                                        break;
                                    default: System.out.println(ANSI_RED+"Choix invalide."+ANSI_RESET); continue;
                                }

                                if (!continuerJeuGlobal) { combatTermine = true; break; }

                                if (actionCombatFaiteCePa) {
                                    paConsommesCeTour++;
                                    if (!(joueurActif instanceof Paladin && choixActionJoueur == 2)) { 
                                        equipeDuJoueur.rotationApresAction(joueurActif);
                                    }
                                } else if (objetUtiliseCePa) {
                                    paConsommesCeTour++;
                                } else if (choixActionJoueur == 0) {
                                    paConsommesCeTour++;
                                }
                                if (monstres.isEmpty() || equipeDuJoueur.getMembres().isEmpty()) { combatTermine = true; break;}
                            } 
                        } 
                        if (!continuerJeuGlobal) break; 

                        // --- TOUR DES MONSTRES ---
                        if (!combatTermine && !monstres.isEmpty() && !equipeDuJoueur.getMembres().isEmpty() && continuerJeuGlobal) {
                            System.out.println(ANSI_BOLD + ANSI_RED + "\n=== TOUR DES MONSTRES ===" + ANSI_RESET);
                            for (Monstre monstreActif : new ArrayList<>(monstres)) { 
                                if (!monstreActif.estVivant()) continue; 
                                if (!continuerJeuGlobal) { combatTermine = true; break; } 
                                if (equipeDuJoueur.getMembres().isEmpty()) { combatTermine = true; victoireJoueurCombat = false; break; }
                                System.out.println(ANSI_PURPLE + "\n-- Action de " + monstreActif.getNom() + " --" + ANSI_RESET);
                                monstreActif.mettreAJourEffets();
                                if (!monstreActif.estVivant()) { 
                                	System.out.println(ANSI_GREEN + monstreActif.getNom() + " a succombé à ses effets avant d'attaquer !" + ANSI_RESET);
                                    equipeDuJoueur.getInventaireCommun().ajouterOr(monstreActif.getOrM() / 2);
                                    monstres.remove(monstreActif); 
                                    if (monstres.isEmpty()) {
                                        victoireJoueurCombat = true; 
                                        combatTermine = true;
                                    }
                                    continue;
                               }
                            Personnage joueurCible = equipeDuJoueur.getLeader();
                            if (joueurCible != null && joueurCible.estVivant()) {
                                monstreActif.attaquer(joueurCible); 

                                if (!joueurCible.estVivant()) {
                                    System.out.println(ANSI_RED + ANSI_BOLD + joueurCible.getNom() + " a été mis hors de combat par " + monstreActif.getNom() + " !" + ANSI_RESET);
                                    equipeDuJoueur.getMembres().remove(joueurCible); 

                                    if (equipeDuJoueur.getMembres().isEmpty()) {
                                        System.out.println(ANSI_RED + ANSI_BOLD + "\nTOUTE L'ÉQUIPE A ÉTÉ ANÉANTIE !" + ANSI_RESET);
                                        victoireJoueurCombat = false;
                                        combatTermine = true;
                                        break; 
                                    } else {
                                        System.out.println(ANSI_YELLOW + equipeDuJoueur.getLeader().getNom() + ANSI_RESET + " est maintenant en première ligne.");
                                    }
                                }
                            } else {
                                if (equipeDuJoueur.getMembres().isEmpty()){
                                     System.out.println(ANSI_RED + "L'équipe a été anéantie avant l'action de " + monstreActif.getNom() + ANSI_RESET);
                                     victoireJoueurCombat = false;
                                     combatTermine = true;
                                     break;
                                } else {
                                     System.out.println(monstreActif.getNom() + " ne trouve pas de cible valide (joueur principal K.O. ?).");
                                     Personnage nouvelleCible = equipeDuJoueur.getLeader();
                                     if (nouvelleCible != null && nouvelleCible.estVivant()) {
                                         monstreActif.attaquer(nouvelleCible);
                                         if(!nouvelleCible.estVivant()){
                                             if(equipeDuJoueur.getMembres().isEmpty()){ victoireJoueurCombat = false; combatTermine = true; break;}
                                         }
                                     }
                                }
                            }
                            if (combatTermine && !victoireJoueurCombat) break;
                        } 
                    }
                        }
                        if (!continuerJeuGlobal) break;
                        
                        if (equipeDuJoueur.getMembres().isEmpty()) { victoireJoueurCombat = false; combatTermine = true; }
                        else if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; }
                        
                        if (combatTermine && continuerJeuGlobal && (victoireJoueurCombat || !equipeDuJoueur.getMembres().isEmpty()) ) {
                            String pauseFinCombat = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nFin du round. Entrée pour continuer ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                            if ("QUIT_GAME_INTERNAL".equals(pauseFinCombat) || !continuerJeuGlobal) break;
                        }
                    }
                } 
                if (!continuerJeuGlobal) break;

                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- COMBAT DE L'ÉTAGE " + etage + " TERMINÉ ! ---" + ANSI_RESET);
                if (victoireJoueurCombat) {
                    System.out.println(ANSI_GREEN + ANSI_BOLD + "Victoire !" + ANSI_RESET);
                    etage++;
                    equipeDuJoueur.proposerChoixNouveauPersonnage(scanner, equipeDuJoueur, etage, nomJoueur); 
                    if (!continuerJeuGlobal) break;
                } else {
                    System.out.println(ANSI_RED + ANSI_BOLD + "Défaite..." + ANSI_RESET);
                }
                if (!continuerJeuGlobal) break;

                if (victoireJoueurCombat) {
                    String prochainEtageChoix = lireStringAvecMenuPause(scanner, ANSI_CYAN + "\nPrêt pour l'étage " + etage + " ? (Y/N, 'M' menu) : " + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                    if ("QUIT_GAME_INTERNAL".equals(prochainEtageChoix) || !prochainEtageChoix.equalsIgnoreCase("Y") || !continuerJeuGlobal) {
                        continuerSession = "N";
                    }
                } else { 
                     String recommencerChoix = lireStringAvecMenuPause(scanner, ANSI_RED + "Tenter une nouvelle aventure ? (Y/N, 'M' menu) : " + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                     if ("QUIT_GAME_INTERNAL".equals(recommencerChoix) || !recommencerChoix.equalsIgnoreCase("Y") || !continuerJeuGlobal) {
                        continuerSession = "N";
                     } else {
                        nomJoueur = ""; 
                        continuerSession = "Y_RELOAD"; 
                     }
                }
            } 
            if (!continuerSession.equalsIgnoreCase("Y_RELOAD")) {
                continuerSession = "N";
            } else {
                continuerSession = "Y";
            }
    }
    
        

     public static Monstre selectionnerCibleMonstreInteractive(Scanner scanner, List<Monstre> monstres, Equipe equipe, int etage, String nomJoueur) {
        if (monstres.isEmpty()) return null;
        if (monstres.size() == 1) {
            return monstres.get(0);
        }


        int choixCibleIndex = lireIntAvecMenuPause(scanner, "Votre choix (numéro de cible, 'M' menu) : ", equipe, etage, nomJoueur) -1;

        if (!continuerJeuGlobal || choixCibleIndex == -1000) return null; 

        if (choixCibleIndex >= 0 && choixCibleIndex < monstres.size()) {
            return monstres.get(choixCibleIndex);
        } else {
            System.out.println(ANSI_RED + "Choix de cible invalide. Attaque sur le premier par défaut." + ANSI_RESET);
            return monstres.get(0);
        }
    }
    
     public static List<Monstre> genererMonstresPourEtage(int etage) {
    	    List<Monstre> groupeMonstres = new ArrayList<>();
    	    Random random = new Random();
    	    int nbMonstres;


    	    if (etage > 0 && etage % 10 == 0) { // Étage de Boss
    	        nbMonstres = 1; 
    	    } else {
    	        if (etage < 5) {
    	            nbMonstres = 1; 
    	        } else if (etage < 15) {
    	            nbMonstres = random.nextInt(2) + 2; 
    	        } else if (etage < 30) {
    	            nbMonstres = random.nextInt(3) + 3; 
    	        } else if (etage < 50) {
    	        	nbMonstres = random.nextInt(2) + 4; 
    	        } else {
    	            nbMonstres = 5;
    	        }
    	    }

    	    if (etage > 0 && etage % 10 == 0) { // Étage de Boss
    	        System.out.println(ANSI_RED + ANSI_BOLD + "!!! UN BOSS APPARAÎT !!!" + ANSI_RESET);
    	        groupeMonstres.add(Monstre.boss(etage));
    	        if (etage >= 60) {
    	            int nbSbires = random.nextInt(2) + 1;
    	            for (int i = 0; i < nbSbires; i++) {
    	                groupeMonstres.add(Monstre.monstreFaible(etage)); 
    	            }
    	        }
    	    } else {
    	        for (int i = 0; i < nbMonstres; i++) {
    	            Monstre nouveauMonstre = null;
    	            int typeRoll = random.nextInt(100); 

    	            // Structure des vagues 
    	            if (etage <= 10) {        
    	                nouveauMonstre = Monstre.monstreFaible(etage);
    	            } else if (etage <= 20) { 
    	                if (typeRoll < 80) nouveauMonstre = Monstre.monstreFaible(etage);
    	                else nouveauMonstre = Monstre.monstreMoyen(etage);
    	            } else if (etage <= 30) { 
    	                if (typeRoll < 50) nouveauMonstre = Monstre.monstreFaible(etage);
    	                else nouveauMonstre = Monstre.monstreMoyen(etage);
    	            } else if (etage <= 40) {
    	                if (typeRoll < 20) nouveauMonstre = Monstre.monstreFort(etage); 
    	                else nouveauMonstre = Monstre.monstreMoyen(etage);            
    	            } else if (etage <= 50) { 
    	                if (typeRoll < 50) nouveauMonstre = Monstre.monstreMoyen(etage);
    	                else nouveauMonstre = Monstre.monstreFort(etage);
    	            } else {                  
    	                nouveauMonstre = Monstre.monstreFort(etage);
    	            }

    	            if (nouveauMonstre != null) {
    	                groupeMonstres.add(nouveauMonstre);
    	            }
    	        }
    	    }

    	    if (!groupeMonstres.isEmpty()) {
    	         System.out.println(ANSI_YELLOW + groupeMonstres.size() + " monstres apparaissent !" + ANSI_RESET);
    	    } else if (etage % 10 != 0) {
    	        System.out.println(ANSI_YELLOW + "Aucun monstre n'est apparu cet étage (configuration à vérifier)." + ANSI_RESET);
    	    }
    	    return groupeMonstres;
    	}
     
     public static void afficherEtatCombat(Equipe equipe, List<Monstre> monstres) {
    	    System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- ÉTAT DU CHAMP DE BATAILLE ---" + ANSI_RESET);

    	    System.out.println(ANSI_GREEN + "Votre Équipe (" + equipe.getMembres().size() + "):" + ANSI_RESET);
    	    if (equipe.getMembres().isEmpty()) {
    	        System.out.println("  (Aucun héros debout...)");
    	    } else {
    	        for (int i = 0; i < equipe.getMembres().size(); i++) {
    	            Personnage p = equipe.getMembres().get(i);
    	            String effetsEmojis = p.afficherEmojisEffets(); 
    	            System.out.printf("  %d. %s%-25s%s %s- PV: %s%3d/%-3d%s\n",
    	                    (i + 1),
    	                    ANSI_YELLOW, p.getNom(), ANSI_RESET, 
    	                    effetsEmojis.isEmpty() ? "" : ANSI_CYAN + effetsEmojis + ANSI_RESET + " ", 
    	                    ANSI_GREEN, p.getPv(), p.getPvMax(), ANSI_RESET); // PV
    	        }
    	    }


    	    System.out.println(ANSI_RED + "\nMonstres Restants (" + monstres.size() + "):" + ANSI_RESET);
    	    if (monstres.isEmpty()) {
    	        System.out.println("  (Plus aucun monstre !)");
    	    } else {
    	        for (int i = 0; i < monstres.size(); i++) {
    	            Monstre m = monstres.get(i);
    	            String effetsEmojisMonstre = m.afficherEmojisEffets(); 
    	            System.out.printf("  %d. %s%-23s%s %s- PV: %s%3d/%-3d%s\n", 
    	                    (i + 1),
    	                    ANSI_YELLOW, m.getNom(), ANSI_RESET, 
    	                    effetsEmojisMonstre.isEmpty() ? "" : ANSI_PURPLE + effetsEmojisMonstre + ANSI_RESET + " ", 
    	                    ANSI_RED, m.getPv(),m.getPvMax(), ANSI_RESET); // PV
    	        }
    	    }
    	    System.out.println(ANSI_BLUE + "---------------------------------" + ANSI_RESET);
    	}
}