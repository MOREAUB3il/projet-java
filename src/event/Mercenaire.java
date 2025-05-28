package event;

import jeu.Equipe;
import jeu.Main; 
import monstre.Monstre;
import monstre.MercenairePNJ;
import personnage.Personnage;
import personnage.EffetTemporaire;
import personnage.Paladin; 

import java.util.ArrayList; 
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Mercenaire extends Evenement {

    public Mercenaire() {
        super("Un Mercenaire Arrogant",
              "Un mercenaire à l'allure revêche vous barre la route, un sourire narquois aux lèvres.\n\"Alors, les p'tits nouveaux ? Montrez-moi ce que vous avez dans le ventre pour 3 petits tours,\n ou payez simplement le péage de 20G pour passer tranquillement !\"");
    }

    @Override
    public void declencher(Equipe equipe, Scanner scanner, int etage, String nomJoueur, int idSauvegardeActive) {
        afficherDebutEvenement();

        String choixInitial = Main.lireStringAvecMenuPause(scanner,
                "Que faites-vous ?\n  1. Le combattre (max 3 tours de joueur).\n  2. Payer 20G pour passer.\n  3. Tenter de l'ignorer et partir (risqué).\nChoix ('M' menu) : ",
                equipe, etage, nomJoueur, idSauvegardeActive);

        if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(choixInitial)) return;

        if ("2".equals(choixInitial)) {
            int peage = 20;
            if (equipe.getInventaireCommun().getOr() >= peage) {
                equipe.getInventaireCommun().retirerOr(peage); 
                System.out.println(Main.ANSI_GREEN + "Vous payez " + peage + "G. Le mercenaire vous laisse passer en ricanant." + Main.ANSI_RESET);
            } else {
                System.out.println(Main.ANSI_RED + "Vous n'avez pas assez d'or ! Le mercenaire se moque et insiste pour un combat." + Main.ANSI_RESET);
                combattreMercenaire(equipe, scanner, etage, nomJoueur, idSauvegardeActive);
            }
        } else if ("1".equals(choixInitial)) {
            combattreMercenaire(equipe, scanner, etage, nomJoueur, idSauvegardeActive);
        } else if ("3".equals(choixInitial)) {
            System.out.println("Vous essayez de passer outre le mercenaire...");
             try { Thread.sleep(1000); } catch (InterruptedException e) {}
            if (new Random().nextInt(100) < 30) { 
                 System.out.println(Main.ANSI_GREEN + "Étonnamment, il vous laisse partir, peut-être vous sous-estime-t-il." + Main.ANSI_RESET);
            } else {
                 System.out.println(Main.ANSI_RED + "Le mercenaire vous attrape par le col ! \"Pas si vite !\" Il engage le combat." + Main.ANSI_RESET);
                 combattreMercenaire(equipe, scanner, etage, nomJoueur, idSauvegardeActive);
            }
        } else {
            System.out.println(Main.ANSI_YELLOW + "Vous restez immobile. Le mercenaire s'impatiente et attaque !" + Main.ANSI_RESET);
            combattreMercenaire(equipe, scanner, etage, nomJoueur, idSauvegardeActive);
        }

        if(Main.continuerJeuGlobal) {
             String pause = Main.lireStringAvecMenuPause(scanner, Main.ANSI_YELLOW + "\nFin de la rencontre. Appuyez sur Entrée ('M' menu)..." + Main.ANSI_RESET, equipe, etage, nomJoueur,idSauvegardeActive);
             if("QUIT_GAME_INTERNAL".equals(pause) || !Main.continuerJeuGlobal) Main.continuerJeuGlobal = false;
        }
    }

    private void combattreMercenaire(Equipe equipe, Scanner scanner, int etage, String nomJoueur,int idSauvegardeActive) {
        System.out.println(Main.ANSI_BOLD + Main.ANSI_RED + "\n--- COMBAT CONTRE LE MERCENAIRE ---" + Main.ANSI_RESET);
        MercenairePNJ mercenaire = new MercenairePNJ(etage);
        List<Monstre> adversaires = new ArrayList<>();
        adversaires.add(mercenaire);

        int pvMaxMercenaireInitial = mercenaire.getPvMax(); 
        int toursDeJoueurEffectues = 0;
        final int MAX_TOURS_JOUEUR = 3;
        boolean combatSpecialTermine = false;

        while (toursDeJoueurEffectues < MAX_TOURS_JOUEUR && mercenaire.estVivant() && !equipe.getMembres().isEmpty() && Main.continuerJeuGlobal && !combatSpecialTermine) {
            
            Main.afficherEtatCombat(equipe, adversaires); 
            System.out.println(Main.ANSI_YELLOW + "Tour du joueur " + (toursDeJoueurEffectues + 1) + "/" + MAX_TOURS_JOUEUR + " contre le Mercenaire." + Main.ANSI_RESET);


            if (!equipe.getMembres().isEmpty() && Main.continuerJeuGlobal) {
                System.out.println(Main.ANSI_BOLD + Main.ANSI_GREEN + "\n=== TOUR DE L'ÉQUIPE ===" + Main.ANSI_RESET);
                int pointsActionEquipe = 4;
                List<Personnage> personnagesAyantAgiCombatCeTourPA = new ArrayList<>();
                int paConsommesCeTour = 0;

                while (paConsommesCeTour < pointsActionEquipe && !equipe.getMembres().isEmpty() && mercenaire.estVivant() && Main.continuerJeuGlobal) {
                    System.out.println(Main.ANSI_YELLOW + "\nPA restants : " + (pointsActionEquipe - paConsommesCeTour) + Main.ANSI_RESET);
                    System.out.println(Main.ANSI_CYAN + "Perso à faire agir (0 pour passer les PA, 'M' menu) :" + Main.ANSI_RESET);
                    boolean tousOntAgi = true;
                    for (int i = 0; i < equipe.getMembres().size(); i++) {
                        Personnage p = equipe.getMembres().get(i);
                        System.out.print("  " + (i + 1) + ". " + p.getNom());
                        if (personnagesAyantAgiCombatCeTourPA.contains(p)) System.out.print(Main.ANSI_RED+" [Déjà agi combat]"+Main.ANSI_RESET);
                        else tousOntAgi = false;
                        System.out.println(" (PV: " + p.getPv() + "/" + p.getPvMax() + ")");
                    }

                    if (tousOntAgi && !equipe.getMembres().isEmpty()) {
                        System.out.println(Main.ANSI_YELLOW + "Tous les persos ont fait une action de combat. PA restants passés." + Main.ANSI_RESET);
                        paConsommesCeTour = pointsActionEquipe; continue;
                    }
                    
                    int choixPersoInput = Main.lireIntAvecMenuPause(scanner, "Votre choix (numéro) : ", equipe, etage, nomJoueur, idSauvegardeActive);
                    if (!Main.continuerJeuGlobal || choixPersoInput == -999) { combatSpecialTermine = true; break; }

                    if (choixPersoInput == 0) { /* ... gestion du "passer 0" ... */ continue; }
                    int choixPersoIndex = choixPersoInput - 1;
                    if (choixPersoIndex < 0 || choixPersoIndex >= equipe.getMembres().size()) { /* ... choix invalide ... */ continue; }
                    
                    Personnage joueurActif = equipe.getMembres().get(choixPersoIndex);
                    joueurActif.mettreAJourEffets();
                    if (!joueurActif.estVivant()) { /* ... gestion mort par DoT ... */ equipe.getMembres().remove(joueurActif); if(equipe.getMembres().isEmpty()) combatSpecialTermine = true; continue; }

                    System.out.println("\nÀ " + Main.ANSI_YELLOW + joueurActif.getNom() + Main.ANSI_RESET + " d'agir. ('M' menu)");
                    System.out.println("  1. Attaquer (Comp1) " + (personnagesAyantAgiCombatCeTourPA.contains(joueurActif) ? Main.ANSI_RED+"[Déjà agi]"+Main.ANSI_RESET : ""));
                    if (joueurActif.getUltLvl() > 0) System.out.println("  2. Ultime "  + (personnagesAyantAgiCombatCeTourPA.contains(joueurActif) ? Main.ANSI_RED+"[Déjà agi]"+Main.ANSI_RESET : ""));
                    System.out.println("  3. Utiliser Objet"); 
                    System.out.println("  0. Passer ce PA");
                    
                    int choixActionJoueur = Main.lireIntAvecMenuPause(scanner, "Action : ", equipe, etage, nomJoueur,idSauvegardeActive);
                    if (!Main.continuerJeuGlobal || choixActionJoueur == -999) { combatSpecialTermine = true; break; }

                    boolean actionCombatFaite = false;
                    boolean objetUtilise = false;

                    switch (choixActionJoueur) {
                        case 1: // Attaquer
                            if (personnagesAyantAgiCombatCeTourPA.contains(joueurActif)) { System.out.println(Main.ANSI_RED + "Action de combat déjà faite." + Main.ANSI_RESET); continue; }
                            System.out.println(Main.ANSI_YELLOW + joueurActif.getNom() + Main.ANSI_RESET + " attaque le Mercenaire !");
                            joueurActif.attaque1(mercenaire);
                            actionCombatFaite = true; personnagesAyantAgiCombatCeTourPA.add(joueurActif);
                            break;
                        case 2: // Ultime
                             if (joueurActif.getUltLvl() == 0) { System.out.println(Main.ANSI_RED + "Ultime non débloqué !" + Main.ANSI_RESET); continue; }
                             if (personnagesAyantAgiCombatCeTourPA.contains(joueurActif)) { System.out.println(Main.ANSI_RED + "Action de combat déjà faite." + Main.ANSI_RESET); continue; }
                             System.out.println(Main.ANSI_YELLOW + joueurActif.getNom() + Main.ANSI_RESET + " utilise son ULTIME sur le Mercenaire !");
                             joueurActif.ulti(mercenaire);
                             if (joueurActif instanceof Paladin) equipe.deplacerMembreAuDebut(joueurActif);
                             actionCombatFaite = true; personnagesAyantAgiCombatCeTourPA.add(joueurActif);
                             break;
                        case 3: 
                            if(equipe.getInventaireCommun().utiliserObjetInteractive(scanner, joueurActif, equipe.getMembres(), adversaires, equipe, etage, nomJoueur, idSauvegardeActive)){
                                objetUtilise = true;
                            }
                             if (!Main.continuerJeuGlobal) { combatSpecialTermine = true; }
                            break;
                        case 0: System.out.println(joueurActif.getNom() + " passe ce PA."); break;
                        default: System.out.println(Main.ANSI_RED+"Choix invalide."+Main.ANSI_RESET); continue;
                    } // Fin Switch Action
                    if (!Main.continuerJeuGlobal || combatSpecialTermine) break;

                    if (!mercenaire.estVivant()) {
                        combatSpecialTermine = true; 
                        break; 
                    }
                    
                    if (actionCombatFaite) {
                        paConsommesCeTour++;
                        if (!(joueurActif instanceof Paladin && choixActionJoueur == 2)) equipe.rotationApresAction(joueurActif);
                    } else if (objetUtilise) {
                        paConsommesCeTour++;
                    } else if (choixActionJoueur == 0) {
                        paConsommesCeTour++;
                    }
                }
            } 
            if (!Main.continuerJeuGlobal || combatSpecialTermine || !mercenaire.estVivant() || equipe.getMembres().isEmpty()) break;


            if (mercenaire.estVivant() && !equipe.getMembres().isEmpty() && Main.continuerJeuGlobal) {
                System.out.println(Main.ANSI_BOLD + Main.ANSI_RED + "\n=== TOUR DU MERCENAIRE ===" + Main.ANSI_RESET);
                mercenaire.mettreAJourEffets();
                if (mercenaire.estVivant()) {
                    boolean mercenaireEtourdi = false;
                    if(mercenaire.getEffetsActifs() != null) {
                        for(EffetTemporaire ef : mercenaire.getEffetsActifs()) if(ef.getNom().equals("Étourdi")) mercenaireEtourdi = true;
                    }
                    if(mercenaireEtourdi) System.out.println(mercenaire.getNom() + " est Étourdi 💫 !");
                    else {
                        Personnage cibleJoueur = equipe.getLeader();
                        if (cibleJoueur != null) {
                            mercenaire.attaquer(cibleJoueur);
                            if (!cibleJoueur.estVivant()) {
                                System.out.println(Main.ANSI_RED + cibleJoueur.getNom() + " a été vaincu !" + Main.ANSI_RESET);
                                equipe.getMembres().remove(cibleJoueur);
                                if(equipe.getMembres().isEmpty()) combatSpecialTermine = true;
                            }
                        }
                    }
                } else {
                     System.out.println(Main.ANSI_GREEN + "Le Mercenaire succombe à ses effets avant d'agir !" + Main.ANSI_RESET);
                     combatSpecialTermine = true; 
                }
            }
            toursDeJoueurEffectues++;
            if (!Main.continuerJeuGlobal || combatSpecialTermine || !mercenaire.estVivant() || equipe.getMembres().isEmpty()) break;
            
            String pauseRoundMerc = Main.lireStringAvecMenuPause(scanner, Main.ANSI_YELLOW + "\nFin du round contre le mercenaire. Entrée ('M' menu)..." + Main.ANSI_RESET, equipe, etage, nomJoueur, idSauvegardeActive);
            if ("QUIT_GAME_INTERNAL".equals(pauseRoundMerc) || !Main.continuerJeuGlobal) { combatSpecialTermine = true; break;}

        }


        if (!Main.continuerJeuGlobal) return;

        if (!mercenaire.estVivant()) {
            System.out.println(Main.ANSI_GREEN + "\nVous avez vaincu le Mercenaire Arrogant !" + Main.ANSI_RESET);
            int orRecompense = 50 + new Random().nextInt(26);
            System.out.println("Il laisse tomber une bourse bien garnie : +" + orRecompense + "G !");
            equipe.getInventaireCommun().ajouterOr(orRecompense);
            for(Personnage p : equipe.getMembres()){
                if(p.estVivant()) p.gagnerXp(mercenaire.getXpDonnee() / Math.max(1, equipe.getMembresVivantsCount())); 
            }
        } else if (equipe.getMembres().isEmpty()) {
            System.out.println(Main.ANSI_RED + "\nLe Mercenaire vous a terrassé... Il pille vos affaires." + Main.ANSI_RESET);
            int orPerdu = equipe.getInventaireCommun().getOr() / 2;
            equipe.getInventaireCommun().retirerOr(orPerdu);
            System.out.println("Vous perdez " + orPerdu + "G.");
        } else { 
            float pourcentagePvMercenaire = (float) mercenaire.getPv() / pvMaxMercenaireInitial;
            System.out.println("\nLe mercenaire essuie la sueur de son front.");
            if (pourcentagePvMercenaire < 0.30) {
                System.out.println(Main.ANSI_GREEN + "\"Vous m'avez bien amoché ! Tenez, 30G et fichez-moi la paix !\"" + Main.ANSI_RESET);
                equipe.getInventaireCommun().ajouterOr(30);
            } else if (pourcentagePvMercenaire <= 0.60) {
                System.out.println(Main.ANSI_YELLOW + "\"Hmpf. Pas mal. Allez, circulez.\"" + Main.ANSI_RESET);
            } else {
                System.out.println(Main.ANSI_RED + "\"Pathétique ! Pour mon temps perdu, donnez-moi 20G !\"" + Main.ANSI_RESET);
                if (equipe.getInventaireCommun().getOr() >= 20) {
                    equipe.getInventaireCommun().retirerOr(20);
                } else {
                    System.out.println("... mais vous n'avez même pas ça. Il crache par terre et s'en va.");
                }
            }
        }
    }
}