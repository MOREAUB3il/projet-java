package jeu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import db.Database;
import db.Database.SauvegardeInfo;
import event.*;
import monstre.Monstre;
import personnage.*;

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
    private static Database database;

    public static String lireStringAvecMenuPause(Scanner scanner, String prompt, Equipe equipe, int etage, String nomJoueur, int idSauvegardeActive) {
        String input;
        while (continuerJeuGlobal) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("M")) {
                afficherMenuPause(scanner, equipe, etage, nomJoueur, idSauvegardeActive);
                if (!continuerJeuGlobal) return "QUIT_GAME_INTERNAL";
            } else {
                return input;
            }
        }
        return "QUIT_GAME_INTERNAL";
    }

    public static int lireIntAvecMenuPause(Scanner scanner, String prompt, Equipe equipe, int etage, String nomJoueur, int idSauvegardeActive) {
        String input;
        while (continuerJeuGlobal) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("M")) {
                afficherMenuPause(scanner, equipe, etage, nomJoueur, idSauvegardeActive);
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

    public static void afficherMenuPause(Scanner scanner, Equipe equipe, int etage, String nomJoueur, int idSauvegardeActive) {
        System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- PAUSE ---" + ANSI_RESET);
        String choixMenuPause;
        boolean resterEnPause = true;
        while (resterEnPause && continuerJeuGlobal) {
            System.out.println(ANSI_CYAN + "Choisissez une option :" + ANSI_RESET);
            System.out.println("  1. Reprendre la partie");
            System.out.println("  2. Sauvegarder et Quitter");
            System.out.println("  3. Quitter sans Sauvegarder");
            System.out.print("Votre choix : ");
            choixMenuPause = scanner.nextLine();
            switch (choixMenuPause) {
                case "1": resterEnPause = false; System.out.println(ANSI_GREEN + "Reprise..." + ANSI_RESET); break;
                case "2":
                    System.out.println(ANSI_YELLOW + "Sauvegarde..." + ANSI_RESET);
                    if (database != null && nomJoueur != null && !nomJoueur.isEmpty() && equipe != null && !equipe.getMembres().isEmpty()) {
                        try {
                            int nouvelleSauvegardeId = database.createSauvegarde(nomJoueur, etage);
                            if (nouvelleSauvegardeId != -1) {
                                database.sauvegarderEquipeDb(equipe.getMembres(), nouvelleSauvegardeId);
                                database.sauvegarderInventaireDb(equipe.getInventaireCommun(), nouvelleSauvegardeId);
                                System.out.println(ANSI_GREEN + "Partie sauvegardée avec succès (ID: " + nouvelleSauvegardeId + ")!" + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_RED + "Erreur lors de la création de la sauvegarde." + ANSI_RESET);
                            }
                        } catch (SQLException e) {
                            System.err.println(ANSI_RED + "Erreur DB lors de la sauvegarde: " + e.getMessage() + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED + "Impossible de sauvegarder: informations de jeu manquantes ou DB non initialisée." + ANSI_RESET);
                    }
                    System.out.println("Merci d'avoir joué !");
                    continuerJeuGlobal = false; resterEnPause = false;
                    break;
                case "3":
                    System.out.print(ANSI_RED + "Êtes-vous sûr de vouloir quitter sans sauvegarder ? (Y/N) : " + ANSI_RESET);
                    if (scanner.nextLine().equalsIgnoreCase("Y")) {
                        System.out.println("Merci d'avoir joué !");
                        continuerJeuGlobal = false; resterEnPause = false;
                    } else System.out.println(ANSI_GREEN + "Retour au menu pause." + ANSI_RESET);
                    break;
                default: System.out.println(ANSI_RED + "Choix invalide." + ANSI_RESET); break;
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        database = new Database();
        database.connect();
        database.createTables();

        String continuerSession = "Y";
        String nomJoueur = "";
        int idSauvegardeActive = -1;

        System.out.println(ANSI_BOLD + ANSI_CYAN + "=== Bienvenue dans le Donjon Infini ! ===" + ANSI_RESET);

        while (continuerSession.equalsIgnoreCase("Y") && continuerJeuGlobal) {
            int etage = 1;
            Equipe equipeDuJoueur = new Equipe();
            boolean victoireJoueurCombat = true;
            idSauvegardeActive = -1;

            if (nomJoueur.isEmpty() || !continuerSession.equalsIgnoreCase("Y_RELOAD_SAVE")) {
                nomJoueur = lireStringAvecMenuPause(scanner, "Entrez votre pseudo ('M' menu): " + ANSI_YELLOW, equipeDuJoueur, etage, "", idSauvegardeActive);
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(nomJoueur) || nomJoueur.trim().isEmpty()) { continuerSession = "N"; break; }
                System.out.print(ANSI_RESET);
            }

            List<SauvegardeInfo> sauvegardesExistantes = database.getListeSauvegardes(nomJoueur);
            boolean chargerPartie = false;
            if (!sauvegardesExistantes.isEmpty()) {
                System.out.println(ANSI_CYAN + "\nSauvegardes pour " + nomJoueur + ":" + ANSI_RESET);
                for (int i = 0; i < sauvegardesExistantes.size(); i++) {
                    SauvegardeInfo info = sauvegardesExistantes.get(i);
                    System.out.printf("  %d. Étage %d - Date: %s (ID: %d)\n", (i + 1), info.numEtage, info.dateCreation, info.id);
                }
                System.out.println("  0. Nouvelle Partie");
                int choixLoad = lireIntAvecMenuPause(scanner, "Charger (numéro) ou Nouvelle Partie (0) ('M' menu) ? ", equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);

                if (!continuerJeuGlobal || choixLoad == -999) { continuerSession = "N"; break; }

                if (choixLoad > 0 && choixLoad <= sauvegardesExistantes.size()) {
                    SauvegardeInfo sauvegardeACcharger = sauvegardesExistantes.get(choixLoad - 1);
                    idSauvegardeActive = sauvegardeACcharger.id;
                    System.out.println(ANSI_GREEN + "Chargement sauvegarde ID " + idSauvegardeActive + "..." + ANSI_RESET);
                    equipeDuJoueur = database.chargerEquipe(idSauvegardeActive, scanner, nomJoueur);
                    etage = database.getEtageSauvegarde(idSauvegardeActive);
                    chargerPartie = true;
                    if (choixLoad > 1) {
                        try {
                            System.out.println(ANSI_YELLOW + "Suppression des sauvegardes plus récentes..." + ANSI_RESET);
                            database.supNouSav(nomJoueur, idSauvegardeActive);
                        } catch (SQLException e) { System.err.println(ANSI_RED + "Erreur suppr récentes: " + e.getMessage() + ANSI_RESET); }
                    }
                } else { System.out.println(ANSI_GREEN + "Lancement Nouvelle Partie." + ANSI_RESET); }
            } else { System.out.println(ANSI_GREEN + "Aucune sauvegarde pour " + nomJoueur + ". Nouvelle Partie." + ANSI_RESET); }

            if (!chargerPartie) {
                idSauvegardeActive = -1; etage = 1; equipeDuJoueur = new Equipe();
                equipeDuJoueur.choisirEtAjouterPersonnageInitial(scanner, equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);
                if (!continuerJeuGlobal) { continuerSession = "N"; break; }
                if (equipeDuJoueur.getMembres().isEmpty()) { System.out.println(ANSI_RED + "Aucun perso. Fin." + ANSI_RESET); continuerSession = "N"; break; }
            }
            
            equipeDuJoueur.afficherStatEquipe();

            while (victoireJoueurCombat && continuerJeuGlobal) {
                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- Étage " + etage + " ---" + ANSI_RESET);
                System.out.println("Joueur : " + ANSI_YELLOW + nomJoueur + ANSI_RESET + " (Sauvegarde Active ID: " + (idSauvegardeActive == -1 ? "Aucune" : idSauvegardeActive) +")");

                if (doitDeclencherEvenement(etage) && continuerJeuGlobal) {
                    Evenement evenementActuel = choisirEvenementAleatoire(etage);
                    if (evenementActuel != null) {
                        evenementActuel.declencher(equipeDuJoueur, scanner, etage, nomJoueur, idSauvegardeActive);
                        if (!continuerJeuGlobal) break;
                    }
                }
                if (!continuerJeuGlobal) break;

                String actionPreparation;
                do {
                    actionPreparation = lireStringAvecMenuPause(scanner,
                            ANSI_CYAN + "\nAvant combat ('M' menu):\n" + ANSI_RESET +
                                    "  1. Magasin\n  2. Inventaire\n  3. Stats Équipe\n" +
                                    ANSI_GREEN + "  0. Au combat !\n" + ANSI_RESET +
                                    "Choix : ", equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);
                    if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;
                    switch (actionPreparation) {
                        case "1": new Shop().ouvrir(scanner, equipeDuJoueur.getInventaireCommun(), equipeDuJoueur, etage, nomJoueur, idSauvegardeActive); break;
                        case "2": equipeDuJoueur.getInventaireCommun().afficherInventaire(); break;
                        case "3": equipeDuJoueur.afficherStatEquipe(); break;
                        case "0": System.out.println(ANSI_GREEN + "En route !" + ANSI_RESET); break;
                        default: System.out.println(ANSI_RED + "Invalide." + ANSI_RESET); break;
                    }
                    if (continuerJeuGlobal && !actionPreparation.equals("0") && !"QUIT_GAME_INTERNAL".equals(actionPreparation)) {
                         System.out.println(ANSI_YELLOW + "Préparatifs terminés pour ce choix..." + ANSI_RESET);
                         try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    }
                } while (!actionPreparation.equals("0") && continuerJeuGlobal);
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;
                
                System.out.println(ANSI_YELLOW + "\nVotre équipe s'aventure plus profondément..." + ANSI_RESET);
                try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                System.out.println(ANSI_BOLD + ANSI_RED + "\n=== DÉBUT COMBAT - ÉTAGE " + etage + " ===" + ANSI_RESET);
                List<Monstre> monstres = genererMonstresPourEtage(etage);
                victoireJoueurCombat = false;

                if (monstres.isEmpty() && continuerJeuGlobal) {
                    System.out.println(ANSI_GREEN + "Aucun monstre. Victoire !" + ANSI_RESET);
                    victoireJoueurCombat = true;
                } else {
                    boolean combatTermine = false;
                    while (!combatTermine && continuerJeuGlobal) {
                        if (!combatTermine && continuerJeuGlobal) afficherEtatCombat(equipeDuJoueur, monstres);
                        
                        if (!equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty() && continuerJeuGlobal && !combatTermine) {
                            System.out.println(ANSI_BOLD + ANSI_GREEN + "\n=== TOUR DE L'ÉQUIPE (" + nomJoueur + ") ===" + ANSI_RESET);
                            int pointsActionEquipe = 4;
                            List<Personnage> personnagesAyantAgiCombat = new ArrayList<>();
                            int paConsommesCeTour = 0;

                            while (paConsommesCeTour < pointsActionEquipe && !equipeDuJoueur.getMembres().isEmpty() && !monstres.isEmpty() && continuerJeuGlobal && !combatTermine) {
                                System.out.println(ANSI_YELLOW + "\nPA restants: " + (pointsActionEquipe - paConsommesCeTour) + ANSI_RESET);
                                System.out.println(ANSI_CYAN + "Perso (0 passer PA si tous ont agi, 'M' menu):" + ANSI_RESET);
                                boolean tousOntAgi = true;
                                for (int i = 0; i < equipeDuJoueur.getMembres().size(); i++) {
                                    Personnage p = equipeDuJoueur.getMembres().get(i);
                                    System.out.print("  " + (i + 1) + ". " + p.getNom());
                                    if (personnagesAyantAgiCombat.contains(p)) System.out.print(ANSI_RED + " [Déjà agi combat]" + ANSI_RESET);
                                    else tousOntAgi = false;
                                    System.out.println(" (PV: " + p.getPv() + "/" + p.getPvMax() + ")");
                                }

                                if (tousOntAgi && !equipeDuJoueur.getMembres().isEmpty()) {
                                    System.out.println(ANSI_YELLOW + "Tous ont agi. PA restants passés." + ANSI_RESET);
                                    paConsommesCeTour = pointsActionEquipe; continue;
                                }

                                int choixPersoInput = lireIntAvecMenuPause(scanner, "Choix (numéro): ", equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);
                                if (!continuerJeuGlobal || choixPersoInput == -999) { combatTermine = true; break; }
                                if (choixPersoInput == 0) {
                                    if (tousOntAgi) { paConsommesCeTour = pointsActionEquipe; continue; }
                                    else { System.out.println(ANSI_RED + "Utilisez 'Passer PA' dans menu d'action." + ANSI_RESET); continue; }
                                }
                                int choixPersoIndex = choixPersoInput - 1;
                                if (choixPersoIndex < 0 || choixPersoIndex >= equipeDuJoueur.getMembres().size()) { System.out.println(ANSI_RED + "Choix perso invalide." + ANSI_RESET); continue; }
                                
                                Personnage joueurActif = equipeDuJoueur.getMembres().get(choixPersoIndex);
                                joueurActif.mettreAJourEffets();
                                if (!joueurActif.estVivant()) {
                                    System.out.println(ANSI_RED + joueurActif.getNom() + " a succombé à ses effets !" + ANSI_RESET);
                                    equipeDuJoueur.getMembres().remove(joueurActif);
                                    if (equipeDuJoueur.getMembres().isEmpty()) { victoireJoueurCombat = false; combatTermine = true; break; }
                                    continue;
                                }

                                System.out.println("\nÀ " + ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " d'agir. ('M' menu)");
                                String actionPrompt = "  1. Attaquer (Comp1)";
                                if (personnagesAyantAgiCombat.contains(joueurActif)) actionPrompt += ANSI_RED + " [Déjà agi combat]" + ANSI_RESET;
                                System.out.println(actionPrompt);
                                if (joueurActif.getUltLvl() > 0) {
                                    actionPrompt = "  2. Ultime";
                                    if (personnagesAyantAgiCombat.contains(joueurActif)) actionPrompt += ANSI_RED + " [Déjà agi combat]" + ANSI_RESET;
                                    System.out.println(actionPrompt);
                                }
                                System.out.println("  3. Utiliser Objet");
                                actionPrompt = "  4. Changer Place";
                                if (personnagesAyantAgiCombat.contains(joueurActif)) actionPrompt += ANSI_RED + " [Déjà agi combat]" + ANSI_RESET;
                                System.out.println(actionPrompt);
                                System.out.println("  0. Passer ce PA");

                                int choixActionJoueur = lireIntAvecMenuPause(scanner, "Action : ", equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);
                                if (!continuerJeuGlobal || choixActionJoueur == -999) { combatTermine = true; break; }

                                boolean actionCombatFaiteCePa = false;
                                boolean objetUtiliseCePa = false;

                                // --- DEBUT SWITCH ACTION JOUEUR ---
                                switch (choixActionJoueur) {
                                    case 1: // Attaquer (Compétence 1)
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) {
                                            System.out.println(ANSI_RED + joueurActif.getNom() + " a déjà effectué son action de combat." + ANSI_RESET);
                                            continue; 
                                        }
                                        if (monstres.isEmpty()) {
                                            System.out.println(ANSI_YELLOW + "Plus de cibles !" + ANSI_RESET);
                                            continue;
                                        }
                                        
                                        Monstre cibleAtt = null;
                                        if (monstres.size() == 1) {
                                            cibleAtt = monstres.get(0);
                                            System.out.println("Cible automatique : " + cibleAtt.getNom());
                                        } else {
                                            System.out.println(ANSI_CYAN + "Choisissez une cible pour l'Attaque ('M' menu) :" + ANSI_RESET);
                                            for (int i = 0; i < monstres.size(); i++) {
                                                System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s\n", 
                                                                  (i + 1), 
                                                                  ANSI_YELLOW, monstres.get(i).getNom(), ANSI_RESET, 
                                                                  ANSI_RED, monstres.get(i).getPv(), ANSI_RESET);
                                            }
                                            cibleAtt = selectionnerCibleMonstreInteractive(scanner, monstres, equipeDuJoueur, etage, nomJoueur, idSauvegardeActive); 
                                        }
                                        
                                        if (!continuerJeuGlobal) { 
                                            combatTermine = true; 
                                            break; 
                                        }
                                        if (cibleAtt == null) { 
                                            System.out.println(ANSI_YELLOW + "Aucune cible valide sélectionnée pour l'attaque." + ANSI_RESET);
                                            continue; 
                                        }

                                        System.out.println(ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + 
                                                           " utilise sa compétence 1 sur " + 
                                                           ANSI_RED + cibleAtt.getNom() + ANSI_RESET + " !");
                                        joueurActif.attaque1(cibleAtt); 

                                        if (!cibleAtt.estVivant()) {
                                            System.out.println(ANSI_GREEN + cibleAtt.getNom() + " a été vaincu !" + ANSI_RESET);
                                            int xpDuMonstre = cibleAtt.getXpDonnee();
                                            List<Personnage> membresVivants = equipeDuJoueur.getMembresVivants(); 
                                            if (!membresVivants.isEmpty()) {
                                                int xpParMembre = Math.max(1, xpDuMonstre / membresVivants.size());
                                                System.out.println(ANSI_GREEN + "L'équipe gagne de l'expérience !" + ANSI_RESET);
                                                for (Personnage membre : membresVivants) membre.gagnerXp(xpParMembre); 
                                            } else System.out.println(ANSI_YELLOW + "Aucun membre pour recevoir l'XP." + ANSI_RESET);
                                            
                                            equipeDuJoueur.getInventaireCommun().ajouterOr(cibleAtt.getOrM());
                                            System.out.println("L'équipe gagne " + ANSI_YELLOW + cibleAtt.getOrM() + " or." + ANSI_RESET);
                                            monstres.remove(cibleAtt); 
                                            
                                            if (monstres.isEmpty()) { 
                                                System.out.println(ANSI_GREEN + "Tous les monstres ont été vaincus !" + ANSI_RESET);
                                                victoireJoueurCombat = true; combatTermine = true;     
                                            } else if (continuerJeuGlobal) {
                                                 System.out.println(ANSI_YELLOW + "Le combat continue..." + ANSI_RESET);
                                                 try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                                            }
                                        }
                                        actionCombatFaiteCePa = true; 
                                        personnagesAyantAgiCombat.add(joueurActif); 
                                        break;

                                    case 2: // Utiliser Ultime
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) { System.out.println(ANSI_RED+"Action combat déjà faite."+ANSI_RESET); continue; }
                                        if (joueurActif.getUltLvl() == 0) { System.out.println(ANSI_RED+"Ultime non débloqué."+ANSI_RESET); continue; }
                                        
                                        Monstre cibleUlti = null;
                                        if (joueurActif instanceof Pretre) {
                                            System.out.println(ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " canalise son Intervention Divine !");
                                            ((Pretre) joueurActif).ulti(equipeDuJoueur, null); 
                                        } else { 
                                            if (monstres.isEmpty() && !(joueurActif instanceof Paladin)) { 
                                                 System.out.println(ANSI_YELLOW+"Plus de cibles pour l'ultime!"+ANSI_RESET); continue;
                                            }
                                            if (!(joueurActif instanceof Paladin)) { 
                                                 System.out.println(ANSI_CYAN + "Cible pour l'Ultime ('M' menu) :" + ANSI_RESET);
                                                 for (int i = 0; i < monstres.size(); i++) {
                                                     System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s\n", (i + 1), ANSI_YELLOW, monstres.get(i).getNom(), ANSI_RESET, ANSI_RED, monstres.get(i).getPv(), ANSI_RESET);
                                                 }
                                                 cibleUlti = selectionnerCibleMonstreInteractive(scanner, monstres, equipeDuJoueur, etage, nomJoueur, idSauvegardeActive);
                                                 if (!continuerJeuGlobal) { combatTermine = true; break; }
                                                 if (cibleUlti == null) { System.out.println(ANSI_YELLOW+"Aucune cible valide sélectionnée."+ANSI_RESET); continue; }
                                            }
                                            System.out.println(ANSI_YELLOW + joueurActif.getNom() + ANSI_RESET + " déchaîne son ULTIME " + (cibleUlti != null ? "sur " + ANSI_RED + cibleUlti.getNom() : (joueurActif instanceof Paladin ? "" : " (effet de zone?)")) + ANSI_RESET + " !");
                                            joueurActif.ulti(cibleUlti); 
                                            if (joueurActif instanceof Paladin) equipeDuJoueur.deplacerMembreAuDebut(joueurActif);
                                        }

                                        if (cibleUlti != null && !cibleUlti.estVivant()) {
                                            System.out.println(ANSI_GREEN + cibleUlti.getNom() + " a été pulvérisé par l'ultime !" + ANSI_RESET);
                                            int xpDuMonstre = cibleUlti.getXpDonnee();
                                            List<Personnage> membresVivants = equipeDuJoueur.getMembresVivants();
                                            if (!membresVivants.isEmpty()) {
                                                int xpParMembre = Math.max(1, xpDuMonstre / membresVivants.size());
                                                System.out.println(ANSI_GREEN + "L'équipe gagne de l'expérience !" + ANSI_RESET);
                                                for (Personnage membre : membresVivants) membre.gagnerXp(xpParMembre);
                                            } else System.out.println(ANSI_YELLOW + "Aucun membre pour recevoir l'XP." + ANSI_RESET);
                                            
                                            equipeDuJoueur.getInventaireCommun().ajouterOr(cibleUlti.getOrM());
                                            System.out.println("Équipe gagne " + ANSI_YELLOW + cibleUlti.getOrM() + " or." + ANSI_RESET);
                                            monstres.remove(cibleUlti);

                                            if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; System.out.println(ANSI_GREEN + "Tous monstres vaincus!" + ANSI_RESET); }
                                            else if (continuerJeuGlobal) {
                                                System.out.println(ANSI_YELLOW + "Le combat continue..." + ANSI_RESET);
                                                try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                                            }
                                        }
                                        actionCombatFaiteCePa = true; personnagesAyantAgiCombat.add(joueurActif);
                                        break;
                                    case 3: // Utiliser Objet
                                        if (equipeDuJoueur.getInventaireCommun().estVide()){ System.out.println(ANSI_YELLOW+"Inventaire vide."+ANSI_RESET); continue; }
                                        System.out.println(joueurActif.getNom() + " utilise un objet...");
                                        if (equipeDuJoueur.getInventaireCommun().utiliserObjetInteractive(scanner, joueurActif, equipeDuJoueur.getMembres(), monstres, equipeDuJoueur, etage, nomJoueur, idSauvegardeActive)) {
                                            objetUtiliseCePa = true;
                                        }
                                        if (!continuerJeuGlobal) combatTermine = true;
                                        break;
                                    case 4: // Changer Place
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) { System.out.println(ANSI_RED+"Action combat déjà faite."+ANSI_RESET); continue; }
                                        if (equipeDuJoueur.changerDePlace(scanner, joueurActif, equipeDuJoueur, choixActionJoueur, actionPrompt, choixActionJoueur)) {
                                            actionCombatFaiteCePa = true; personnagesAyantAgiCombat.add(joueurActif);
                                        }
                                        if (!continuerJeuGlobal) combatTermine = true;
                                        break;
                                    case 0: System.out.println(joueurActif.getNom() + " passe ce PA."); break;
                                    default: System.out.println(ANSI_RED+"Choix invalide."+ANSI_RESET); continue;
                                }
                                // --- FIN SWITCH ACTION JOUEUR ---
                                if (!continuerJeuGlobal || combatTermine) break;

                                if (actionCombatFaiteCePa) {
                                    paConsommesCeTour++;
                                    if (!(joueurActif instanceof Paladin && choixActionJoueur == 2)) equipeDuJoueur.rotationApresAction(joueurActif);
                                } else if (objetUtiliseCePa) {
                                    paConsommesCeTour++;
                                } else if (choixActionJoueur == 0) {
                                    paConsommesCeTour++;
                                }
                            } 
                        } 
                        if (!continuerJeuGlobal || combatTermine) break;

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
                                    System.out.println(ANSI_GREEN + monstreActif.getNom() + " succombe à ses effets !" + ANSI_RESET);
                                    monstres.remove(monstreActif);
                                    if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; }
                                    continue;
                                }
                                boolean monstreEstEtourdi = false;
                                if(monstreActif.getEffetsActifs() != null) { 
                                    for(personnage.EffetTemporaire ef : monstreActif.getEffetsActifs()) { 
                                       if(ef != null && "Étourdi".equals(ef.getNom())) {
                                           monstreEstEtourdi = true; break;
                                       }
                                    }
                                }
                                if(monstreEstEtourdi) {System.out.println(monstreActif.getNom() + " est Étourdi 💫 !"); continue;}

                                Personnage joueurCible = equipeDuJoueur.getLeader();
                                if (joueurCible != null && joueurCible.estVivant()) {
                                    monstreActif.attaquer(joueurCible);
                                    if (!joueurCible.estVivant()) {
                                        System.out.println(ANSI_RED+ANSI_BOLD+joueurCible.getNom()+" vaincu par "+monstreActif.getNom()+"!"+ANSI_RESET);
                                        equipeDuJoueur.getMembres().remove(joueurCible);
                                        if (equipeDuJoueur.getMembres().isEmpty()) { System.out.println(ANSI_RED+ANSI_BOLD+"\nÉQUIPE ANÉANTIE !"+ANSI_RESET); victoireJoueurCombat = false; combatTermine = true; break; }
                                        else System.out.println(ANSI_YELLOW+equipeDuJoueur.getLeader().getNom()+ANSI_RESET+" en première ligne.");
                                    }
                                } else if (equipeDuJoueur.getMembres().isEmpty()) { victoireJoueurCombat = false; combatTermine = true; break; }
                                else { 
                                    Personnage nouvelleCible = equipeDuJoueur.getLeader();
                                    if(nouvelleCible != null && nouvelleCible.estVivant()) {
                                        monstreActif.attaquer(nouvelleCible);
                                        if(!nouvelleCible.estVivant()){
                                            equipeDuJoueur.getMembres().remove(nouvelleCible);
                                            if(equipeDuJoueur.getMembres().isEmpty()){ victoireJoueurCombat = false; combatTermine = true; break;}
                                        }
                                    } else if (equipeDuJoueur.getMembres().isEmpty()) {
                                        victoireJoueurCombat = false; combatTermine = true; break;
                                    }
                                     else System.out.println(monstreActif.getNom() + " ne trouve pas de cible valide.");
                                }
                                if (combatTermine && !victoireJoueurCombat) break;
                            }
                        }
                        if (!continuerJeuGlobal) break;

                        if (equipeDuJoueur.getMembres().isEmpty()) { victoireJoueurCombat = false; combatTermine = true; }
                        else if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; }
                        
                        if (!combatTermine && continuerJeuGlobal) {
                            System.out.println(ANSI_YELLOW + "\nFin du round..." + ANSI_RESET);
                            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        }
                    }
                }
                if (!continuerJeuGlobal) break;
                

                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- COMBAT ÉTAGE " + etage + " TERMINÉ ! ---" + ANSI_RESET);
                if (victoireJoueurCombat) { 
                    System.out.println(ANSI_GREEN + ANSI_BOLD + "Victoire !" + ANSI_RESET);
                    etage++;
                    equipeDuJoueur.proposerChoixNouveauPersonnage(scanner, equipeDuJoueur, etage, nomJoueur, idSauvegardeActive); 
                     if (!continuerJeuGlobal) break;

                    System.out.println(ANSI_YELLOW + "\nVotre équipe se repose et se prépare pour l'étage " + ANSI_BOLD + etage + ANSI_RESET + ANSI_YELLOW + "..." + ANSI_RESET);
                    try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                }
                else if (!equipeDuJoueur.getMembres().isEmpty()){ System.out.println(ANSI_RED + ANSI_BOLD + "Objectif non atteint..." + ANSI_RESET); break; }
                else { System.out.println(ANSI_RED + ANSI_BOLD + "Défaite..." + ANSI_RESET); break; }
                if (!continuerJeuGlobal) break;

            } 
            
            if (continuerJeuGlobal && !victoireJoueurCombat) {
                 String choixRecommencer = lireStringAvecMenuPause(scanner, ANSI_RED + "\nNouvelle aventure (Y/N, 'M' menu) : " + ANSI_RESET, equipeDuJoueur, etage > 1 ? etage -1 : 1 , nomJoueur, idSauvegardeActive);
                 if ("QUIT_GAME_INTERNAL".equals(choixRecommencer) || !choixRecommencer.equalsIgnoreCase("Y") || !continuerJeuGlobal) continuerSession = "N";
                 else { nomJoueur = ""; continuerSession = "Y_RELOAD_SAVE"; }
            } else if (!continuerJeuGlobal) {
                continuerSession = "N";
            } else if (victoireJoueurCombat && continuerJeuGlobal) { 
                System.out.println(ANSI_GREEN + ANSI_BOLD + "Vous avez terminé tous les étages prévus pour cette session !" + ANSI_RESET);
                continuerSession = "N";
            }

            if (continuerSession.equalsIgnoreCase("Y_RELOAD_SAVE")) continuerSession = "Y";
            else if (!continuerJeuGlobal) continuerSession = "N";
            
        } 
        System.out.println(ANSI_BOLD + ANSI_CYAN + "\nFin de partie. Merci !" + ANSI_RESET);
        if (database != null) database.disconnect();
        scanner.close();
    }

    public static List<Monstre> genererMonstresPourEtage(int etage) {
        List<Monstre> groupeMonstres = new ArrayList<>(); Random random = new Random(); int nbMonstres;
        if (etage > 0 && etage % 10 == 0) nbMonstres = 1;
        else if (etage < 5) nbMonstres = random.nextInt(1) + 1; 
        else if (etage < 15) nbMonstres = random.nextInt(2) + 2;
        else if (etage < 30) nbMonstres = random.nextInt(3) + 3;
        else nbMonstres = random.nextInt(2) + 4;

        if (etage > 0 && etage % 10 == 0) {
            System.out.println(ANSI_RED + ANSI_BOLD + "!!! BOSS APPARAÎT !!!" + ANSI_RESET);
            groupeMonstres.add(Monstre.boss(etage));
            if (etage >= 20) for (int i = 0; i < Math.min(2, etage / 15); i++) groupeMonstres.add(Monstre.monstreFaible(etage));
        } else {
            for (int i = 0; i < nbMonstres; i++) {
                Monstre nM = null; int r = random.nextInt(100);
                if (etage <= 10) nM = Monstre.monstreFaible(etage);
                else if (etage <= 20) nM = (r < 80) ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 30) nM = (r < 50) ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 40) nM = (r < 20) ? Monstre.monstreFort(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 50) nM = (r < 50) ? Monstre.monstreMoyen(etage) : Monstre.monstreFort(etage);
                else nM = Monstre.monstreFort(etage);
                if (nM != null) groupeMonstres.add(nM);
            }
        }
        if(!groupeMonstres.isEmpty())System.out.println(ANSI_YELLOW+groupeMonstres.size()+" monstres!"+ANSI_RESET);
        else if(etage%10!=0)System.out.println(ANSI_YELLOW+"Aucun monstre?"+ANSI_RESET);
        return groupeMonstres;
    }

    public static void afficherEtatCombat(Equipe equipe, List<Monstre> monstres) {
        System.out.println(ANSI_BOLD+ANSI_BLUE+"\n--- ÉTAT CHAMP DE BATAILLE ---"+ANSI_RESET);
        System.out.println(ANSI_GREEN+"Votre Équipe ("+equipe.getMembres().size()+"):"+ANSI_RESET);
        if(equipe.getMembres().isEmpty())System.out.println("  (Aucun héros...)");
        else for(int i=0;i<equipe.getMembres().size();i++){Personnage p=equipe.getMembres().get(i);String e=p.afficherEmojisEffets();System.out.printf("  %d. %s%-25s%s %s- PV: %s%3d/%-3d%s\n",(i+1),ANSI_YELLOW,p.getNom(),ANSI_RESET,e.isEmpty()?"":ANSI_CYAN+e+ANSI_RESET+" ",ANSI_GREEN,p.getPv(),p.getPvMax(),ANSI_RESET);}
        System.out.println(ANSI_RED+"\nMonstres Restants ("+monstres.size()+"):"+ANSI_RESET);
        if(monstres.isEmpty())System.out.println("  (Plus de monstres !)");
        else for(int i=0;i<monstres.size();i++){Monstre m=monstres.get(i);String e=m.afficherEmojisEffets();System.out.printf("  %d. %s%-23s%s %s- PV: %s%3d/%-3d%s\n",(i+1),ANSI_YELLOW,m.getNom(),ANSI_RESET,e.isEmpty()?"":ANSI_PURPLE+e+ANSI_RESET+" ",ANSI_RED,m.getPv(),m.getPvMax(),ANSI_RESET);}
        System.out.println(ANSI_BLUE+"---------------------------------"+ANSI_RESET);
    }

    public static Monstre selectionnerCibleMonstreInteractive(Scanner scanner, List<Monstre> monstres, Equipe equipe, int etage, String nomJoueur, int idSauvegardeActive) {
        if (monstres.isEmpty()) return null;
        if (monstres.size() == 1) return monstres.get(0); 
        int choix = lireIntAvecMenuPause(scanner, "Votre choix (numéro de cible) : ", equipe, etage, nomJoueur, idSauvegardeActive);
        if(!continuerJeuGlobal||choix==-999)return null;
        int index=choix-1;
        if(index>=0&&index<monstres.size())return monstres.get(index);
        else{System.out.println(ANSI_RED+"Invalide. Cible par défaut."+ANSI_RESET);return monstres.get(0);}
    }

    private static boolean doitDeclencherEvenement(int etage) { return (etage > 1 && etage % 10 == 5); }
    private static Evenement choisirEvenementAleatoire(int etage) {
        Random r = new Random(); List<Evenement> p = new ArrayList<>();
        p.add(new TerrainRuine()); p.add(new Ecclesiastique()); p.add(new Mercenaire()); p.add(new Fantome());
        return p.isEmpty()?null:p.get(r.nextInt(p.size()));
    }
}