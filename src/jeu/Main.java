package jeu; 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import event.Ecclesiastique; 
import event.Evenement;
import event.Fantome;       
import event.Mercenaire;    
import event.Shop;
import event.TerrainRuine;  
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
    // private static Database database;

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
                case "1": resterEnPause = false; System.out.println(ANSI_GREEN + "Reprise..." + ANSI_RESET); break;
                case "2":
                    System.out.println(ANSI_YELLOW + "Sauvegarde..." + ANSI_RESET);
                    System.out.println("Partie sauvegardée (simulation).");
                    System.out.println("Merci d'avoir joué !");
                    continuerJeuGlobal = false; resterEnPause = false; break;
                case "3":
                    System.out.print(ANSI_RED + "Quitter sans sauvegarder ? (Y/N) : " + ANSI_RESET);
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
        String continuerSession = "Y";
        String nomJoueur = "";
        System.out.println(ANSI_BOLD + ANSI_CYAN + "=== Bienvenue dans le Donjon Infini ! ===" + ANSI_RESET);

        while (continuerSession.equalsIgnoreCase("Y") && continuerJeuGlobal) {
            int etage = 1;
            Equipe equipeDuJoueur = new Equipe();
            boolean victoireJoueurCombat = true;

            if (nomJoueur.isEmpty() || !continuerSession.equalsIgnoreCase("Y_RELOAD")) {
                nomJoueur = lireStringAvecMenuPause(scanner, "Entrez pseudo ('M' menu): " + ANSI_YELLOW, equipeDuJoueur, etage, "");
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(nomJoueur)) { continuerSession = "N"; break; }
                System.out.print(ANSI_RESET);
            }

            equipeDuJoueur.choisirEtAjouterPersonnageInitial(scanner, equipeDuJoueur, etage, nomJoueur);
            if (!continuerJeuGlobal) { continuerSession = "N"; break; }
            if (equipeDuJoueur.getMembres().isEmpty()) { System.out.println(ANSI_RED + "Aucun perso. Fin." + ANSI_RESET); continuerSession = "N"; break; }
            
            equipeDuJoueur.afficherStatEquipe();
            String pauseApresStats = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nEntrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
            if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(pauseApresStats)) { continuerSession = "N"; break; }

            while (victoireJoueurCombat && continuerJeuGlobal) {
                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- Étage " + etage + " ---" + ANSI_RESET);
                System.out.println("Joueur : " + ANSI_YELLOW + nomJoueur + ANSI_RESET);

                // Déclenchement des événements
                if (doitDeclencherEvenement(etage) && continuerJeuGlobal) {
                    Evenement evenementActuel = choisirEvenementAleatoire(etage);
                    if (evenementActuel != null) {
                        evenementActuel.declencher(equipeDuJoueur, scanner, etage, nomJoueur);
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
                                    "Choix : ", equipeDuJoueur, etage, nomJoueur);
                    if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;
                    switch (actionPreparation) {
                        case "1": new Shop().ouvrir(scanner, equipeDuJoueur.getInventaireCommun(), equipeDuJoueur, etage, nomJoueur); break;
                        case "2": equipeDuJoueur.getInventaireCommun().afficherInventaire(); break;
                        case "3": equipeDuJoueur.afficherStatEquipe(); break;
                        case "0": System.out.println(ANSI_GREEN + "En route !" + ANSI_RESET); break;
                        default: System.out.println(ANSI_RED + "Invalide." + ANSI_RESET); break;
                    }
                    if (continuerJeuGlobal && !actionPreparation.equals("0") && !"QUIT_GAME_INTERNAL".equals(actionPreparation)) {
                        String pauseApresActionPrep = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nEntrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                        if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(pauseApresActionPrep)) { actionPreparation = "QUIT_GAME_INTERNAL"; break; }
                    }
                } while (!actionPreparation.equals("0") && continuerJeuGlobal);
                if (!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(actionPreparation)) break;

                System.out.println(ANSI_BOLD + ANSI_RED + "\n=== DÉBUT COMBAT - ÉTAGE " + etage + " ===" + ANSI_RESET);
                List<Monstre> monstres = genererMonstresPourEtage(etage);
                victoireJoueurCombat = false;

                if (monstres.isEmpty() && continuerJeuGlobal) {
                    System.out.println(ANSI_GREEN + "Aucun monstre. Victoire !" + ANSI_RESET);
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

                                int choixPersoInput = lireIntAvecMenuPause(scanner, "Choix (numéro): ", equipeDuJoueur, etage, nomJoueur);
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

                                int choixActionJoueur = lireIntAvecMenuPause(scanner, "Action : ", equipeDuJoueur, etage, nomJoueur);
                                if (!continuerJeuGlobal || choixActionJoueur == -999) { combatTermine = true; break; }

                                boolean actionCombatFaiteCePa = false;
                                boolean objetUtiliseCePa = false;

                                switch (choixActionJoueur) {
                                    case 1: // Attaquer
                                    case 2: // Ultime
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) { System.out.println(ANSI_RED+"Action combat déjà faite."+ANSI_RESET); continue; }
                                        if (choixActionJoueur == 2 && joueurActif.getUltLvl() == 0) { System.out.println(ANSI_RED+"Ultime non débloqué."+ANSI_RESET); continue; }
                                        if (monstres.isEmpty()) { System.out.println(ANSI_YELLOW+"Plus de cibles."+ANSI_RESET); continue; }

                                        Monstre cibleAction = null;
                                        if (!(joueurActif instanceof Paladin && choixActionJoueur == 2)) { // Paladin ulti 
                                            System.out.println(ANSI_CYAN + "Cible pour " + (choixActionJoueur == 1 ? "Attaque" : "Ultime") + " ('M' menu) :" + ANSI_RESET);
                                            for (int i = 0; i < monstres.size(); i++) {
                                                System.out.printf("  %d. %s%-20s%s - PV: %s%3d%s\n", (i + 1), ANSI_YELLOW, monstres.get(i).getNom(), ANSI_RESET, ANSI_RED, monstres.get(i).getPv(), ANSI_RESET);
                                            }
                                            cibleAction = selectionnerCibleMonstreInteractive(scanner, monstres, equipeDuJoueur, etage, nomJoueur);
                                            if (!continuerJeuGlobal) { combatTermine = true; break; }
                                            if (cibleAction == null) { System.out.println(ANSI_YELLOW+"Aucune cible sélectionnée."+ANSI_RESET); continue; }
                                         }
                                        
                                        if (choixActionJoueur == 1) joueurActif.attaque1(cibleAction);
                                        else {
                                            joueurActif.ultiSoigner(equipeDuJoueur.getMembres());
                                            joueurActif.ultiAtt(cibleAction);
                                            if (joueurActif instanceof Paladin) equipeDuJoueur.deplacerMembreAuDebut(joueurActif);
                                        }

                                        if (cibleAction != null && !cibleAction.estVivant()) {
                                            System.out.println(ANSI_GREEN + cibleAction.getNom() + " vaincu !" + ANSI_RESET);
                                            joueurActif.gagnerXp(cibleAction.getXpDonnee());
                                            equipeDuJoueur.getInventaireCommun().ajouterOr(cibleAction.getOrM());
                                            System.out.println("Équipe gagne " + ANSI_YELLOW + cibleAction.getOrM() + " or." + ANSI_RESET);
                                            monstres.remove(cibleAction);
                                            if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; System.out.println(ANSI_GREEN + "Tous monstres vaincus!" + ANSI_RESET); }
                                            else if (continuerJeuGlobal) {
                                                String pKill = lireStringAvecMenuPause(scanner, ANSI_YELLOW+"\nEntrée ('M' menu)..."+ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                                                if(!continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(pKill)) combatTermine = true;
                                            }
                                        }
                                        actionCombatFaiteCePa = true; personnagesAyantAgiCombat.add(joueurActif);
                                        break;
                                    case 3: // Utiliser Objet
                                        if (equipeDuJoueur.getInventaireCommun().estVide()){ System.out.println(ANSI_YELLOW+"Inventaire vide."+ANSI_RESET); continue; }
                                        System.out.println(joueurActif.getNom() + " utilise un objet...");
                                        if (equipeDuJoueur.getInventaireCommun().utiliserObjetInteractive(scanner, joueurActif, equipeDuJoueur.getMembres(), monstres, equipeDuJoueur, etage, nomJoueur)) {
                                            objetUtiliseCePa = true;
                                        }
                                        if (!continuerJeuGlobal) combatTermine = true;
                                        break;
                                    case 4: // Changer Place
                                        if (personnagesAyantAgiCombat.contains(joueurActif)) { System.out.println(ANSI_RED+"Action combat déjà faite."+ANSI_RESET); continue; }
                                        if (equipeDuJoueur.changerDePlace(scanner, joueurActif)) {
                                            actionCombatFaiteCePa = true; personnagesAyantAgiCombat.add(joueurActif);
                                        }
                                        if (!continuerJeuGlobal) combatTermine = true;
                                        break;
                                    case 0: System.out.println(joueurActif.getNom() + " passe ce PA."); break;
                                    default: System.out.println(ANSI_RED+"Choix invalide."+ANSI_RESET); continue;
                                } 

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
                                if (!monstreActif.estVivant() || !continuerJeuGlobal) continue;
                                if (equipeDuJoueur.getMembres().isEmpty()) { combatTermine = true; victoireJoueurCombat = false; break; }
                                System.out.println(ANSI_PURPLE + "\n-- Action de " + monstreActif.getNom() + " --" + ANSI_RESET);
                              
                                // Attaque un personnage aléatoire de l'équipe
                                if (!equipeDuJoueur.getMembres().isEmpty()) {
                                    Random random = new Random();
                                    Personnage cible = equipeDuJoueur.getMembres().get(random.nextInt(equipeDuJoueur.getMembres().size()));
                                    monstreActif.attaquer(cible);
                                }
                                monstreActif.mettreAJourEffets();
                                if (!monstreActif.estVivant()) {
                                    System.out.println(ANSI_GREEN + monstreActif.getNom() + " succombe à ses effets !" + ANSI_RESET);
                                    monstres.remove(monstreActif);
                                    if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; }
                                    continue;
                                }
                        } // Fin tour des monstres
                        if (!continuerJeuGlobal) break;

                        if (equipeDuJoueur.getMembres().isEmpty()) { victoireJoueurCombat = false; combatTermine = true; }
                        else if (monstres.isEmpty()) { victoireJoueurCombat = true; combatTermine = true; }
                        
                        if (combatTermine && continuerJeuGlobal && (victoireJoueurCombat || !equipeDuJoueur.getMembres().isEmpty())) {
                            String pauseFinRound = lireStringAvecMenuPause(scanner, ANSI_YELLOW + "\nFin du round. Entrée ('M' menu)..." + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                            if ("QUIT_GAME_INTERNAL".equals(pauseFinRound) || !continuerJeuGlobal) break;
                        }
                    } 
                } 
                if (!continuerJeuGlobal) break;

                System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- COMBAT ÉTAGE " + etage + " TERMINÉ ! ---" + ANSI_RESET);
                if (victoireJoueurCombat) {
                    System.out.println(ANSI_GREEN + ANSI_BOLD + "Victoire !" + ANSI_RESET);
                    etage++;
                    equipeDuJoueur.proposerChoixNouveauPersonnage(scanner, equipeDuJoueur, etage, nomJoueur);
                    if (!continuerJeuGlobal) break;
                } else { System.out.println(ANSI_RED + ANSI_BOLD + "Défaite..." + ANSI_RESET); }
                if (!continuerJeuGlobal) break;

                if (victoireJoueurCombat) {
                    String choixProchain = lireStringAvecMenuPause(scanner, ANSI_CYAN + "\nPrêt pour étage " + etage + " (Y/N, 'M' menu) : " + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                    if ("QUIT_GAME_INTERNAL".equals(choixProchain) || !choixProchain.equalsIgnoreCase("Y") || !continuerJeuGlobal) continuerSession = "N";
                } else {
                     String choixRecommencer = lireStringAvecMenuPause(scanner, ANSI_RED + "Nouvelle aventure (Y/N, 'M' menu) : " + ANSI_RESET, equipeDuJoueur, etage, nomJoueur);
                     if ("QUIT_GAME_INTERNAL".equals(choixRecommencer) || !choixRecommencer.equalsIgnoreCase("Y") || !continuerJeuGlobal) continuerSession = "N";
                     else { nomJoueur = ""; continuerSession = "Y_RELOAD"; }
                }
            } // Fin boucle principale d'étage
            if (!continuerSession.equalsIgnoreCase("Y_RELOAD")) continuerSession = "N";
            else continuerSession = "Y";
        } // Fin boucle de session
        System.out.println(ANSI_BOLD + ANSI_CYAN + "\nFin de partie. Merci d'avoir joué !" + ANSI_RESET);
        // if (database != null) database.disconnect();
        scanner.close();
        }
    }

    // --- MÉTHODES UTILITAIRES STATIQUES ---
    public static List<Monstre> genererMonstresPourEtage(int etage) {
        List<Monstre> groupeMonstres = new ArrayList<>();
        Random random = new Random();
        int nbMonstres;
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
                Monstre nouveauMonstre = null; int typeRoll = random.nextInt(100);
                if (etage <= 10) nouveauMonstre = Monstre.monstreFaible(etage);
                else if (etage <= 20) nouveauMonstre = (typeRoll < 80) ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 30) nouveauMonstre = (typeRoll < 50) ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 40) nouveauMonstre = (typeRoll < 20) ? Monstre.monstreFort(etage) : Monstre.monstreMoyen(etage);
                else if (etage <= 50) nouveauMonstre = (typeRoll < 50) ? Monstre.monstreMoyen(etage) : Monstre.monstreFort(etage);
                else nouveauMonstre = Monstre.monstreFort(etage);
                if (nouveauMonstre != null) groupeMonstres.add(nouveauMonstre);
            }
        }
        if (!groupeMonstres.isEmpty()) System.out.println(ANSI_YELLOW+groupeMonstres.size()+" monstres apparaissent!"+ANSI_RESET);
        else if (etage % 10 != 0) System.out.println(ANSI_YELLOW+"Aucun monstre (config?)."+ANSI_RESET);
        return groupeMonstres;
    }

    public static void afficherEtatCombat(Equipe equipe, List<Monstre> monstres) {
        System.out.println(ANSI_BOLD + ANSI_BLUE + "\n--- ÉTAT CHAMP DE BATAILLE ---" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "Votre Équipe (" + equipe.getMembres().size() + "):" + ANSI_RESET);
        if (equipe.getMembres().isEmpty()) System.out.println("  (Aucun héros debout...)");
        else {
            for (int i = 0; i < equipe.getMembres().size(); i++) {
                Personnage p = equipe.getMembres().get(i); String e = p.afficherEmojisEffets();
                System.out.printf("  %d. %s%-25s%s %s- PV: %s%3d/%-3d%s\n", (i+1), ANSI_YELLOW, p.getNom(), ANSI_RESET, e.isEmpty()?"":ANSI_CYAN+e+ANSI_RESET+" ", ANSI_GREEN,p.getPv(),p.getPvMax(),ANSI_RESET);
            }
        }
        System.out.println(ANSI_RED + "\nMonstres Restants (" + monstres.size() + "):" + ANSI_RESET);
        if (monstres.isEmpty()) System.out.println("  (Plus aucun monstre !)");
        else {
            for (int i = 0; i < monstres.size(); i++) {
                Monstre m = monstres.get(i); String e = m.afficherEmojisEffets();
                System.out.printf("  %d. %s%-23s%s %s- PV: %s%3d/%-3d%s\n", (i+1), ANSI_YELLOW, m.getNom(), ANSI_RESET, e.isEmpty()?"":ANSI_PURPLE+e+ANSI_RESET+" ", ANSI_RED,m.getPv(),m.getPvMax(),ANSI_RESET);
            }
        }
        System.out.println(ANSI_BLUE + "---------------------------------" + ANSI_RESET);
    }

    public static Monstre selectionnerCibleMonstreInteractive(Scanner scanner, List<Monstre> monstres, Equipe equipe, int etage, String nomJoueur) {
        if (monstres.isEmpty()) return null;
        if (monstres.size() == 1) return monstres.get(0); 
        int choixCibleInput = lireIntAvecMenuPause(scanner, "Votre choix (numéro de cible) : ", equipe, etage, nomJoueur);
        if (!continuerJeuGlobal || choixCibleInput == -999) return null;
        int choixCibleIndex = choixCibleInput - 1;
        if (choixCibleIndex >= 0 && choixCibleIndex < monstres.size()) return monstres.get(choixCibleIndex);
        else { System.out.println(ANSI_RED + "Choix cible invalide. Cible par défaut: premier." + ANSI_RESET); return monstres.get(0); }
    }

    private static boolean doitDeclencherEvenement(int etage) {
        return (etage > 1 && etage % 10 == 5 && etage % 2 != 0); 
    }

    private static Evenement choisirEvenementAleatoire(int etage) {
        Random random = new Random();
        List<Evenement> pool = new ArrayList<>();
        pool.add(new TerrainRuine());
        pool.add(new Ecclesiastique());
        pool.add(new Mercenaire());
        pool.add(new Fantome());
        if (pool.isEmpty()) return null;
        return pool.get(random.nextInt(pool.size()));
    }
}