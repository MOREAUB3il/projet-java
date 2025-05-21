package event;

import jeu.Equipe;
import personnage.Personnage;
import personnage.Pretre;

import java.util.Random;
import java.util.Scanner;
import jeu.Main;

public class Ecclesiastique extends Evenement {

    public Ecclesiastique() {
        super("Rencontre avec un Ecclésiastique",
              "Un homme en robes modestes, portant des symboles sacrés, croise votre chemin.\nIls vous offrent leurs services... moyennant une contribution.");
    }

    @Override
    public void declencher(Equipe equipe, Scanner scanner, int etage, String nomJoueur) {
        afficherDebutEvenement();
        int coutAmeliorationSante = 10;
        int coutRetablirSante = 30;
        boolean pretreDansEquipe = false;

        for (Personnage p : equipe.getMembres()) {
            if (p instanceof Pretre) {
                pretreDansEquipe = true;
                break;
            }
        }

        if (pretreDansEquipe) {
            System.out.println(Main.ANSI_CYAN + "L'ecclésiastique reconnaît un frère/une sœur de foi dans votre groupe et vous offre une réduction." + Main.ANSI_RESET);
            coutAmeliorationSante -= 5; 
            coutRetablirSante -= 5;    
        }
        coutAmeliorationSante = Math.max(1, coutAmeliorationSante); 
        coutRetablirSante = Math.max(5, coutRetablirSante);


        String choixAction;
        boolean interactionTerminee = false;
        while(!interactionTerminee && Main.continuerJeuGlobal){
            System.out.println("\nQue souhaitez-vous faire ?");
            System.out.println("  1. Payer " + Main.ANSI_YELLOW + coutAmeliorationSante + "G"+ Main.ANSI_RESET + "pour améliorer la santé max d'un personnage (+1 niveau de santé max).");
            System.out.println("  2. Payer " + Main.ANSI_YELLOW + coutRetablirSante + Main.ANSI_RESET + "G pour rétablir la santé d'un personnage (état 'en pleine forme').");
            System.out.println("  3. Décliner poliment et partir.");
            choixAction = Main.lireStringAvecMenuPause(scanner, "Votre choix ('M' menu) : ", equipe, etage, nomJoueur);

            if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(choixAction)) { interactionTerminee = true; break; }

            switch (choixAction) {
                case "1":
                    if (equipe.getInventaireCommun().getOr() >= coutAmeliorationSante) {
                        Personnage persoAChoisir = selectionnerPersonnagePourSoin(equipe, scanner, "améliorer la santé max", equipe, etage, nomJoueur);
                        if (persoAChoisir != null && Main.continuerJeuGlobal) {
                            equipe.getInventaireCommun().setOr(equipe.getInventaireCommun().getOr() - coutAmeliorationSante);
                            int bonusPvMax = 5 + new Random().nextInt(6);
                            persoAChoisir.setPvMax(persoAChoisir.getPvMax() + bonusPvMax);
                            persoAChoisir.setPv(persoAChoisir.getPv() + bonusPvMax);
                            System.out.println(Main.ANSI_GREEN + "La bénédiction a renforcé " + persoAChoisir.getNom() + " ! (+"+bonusPvMax+" PV Max)" + Main.ANSI_RESET);
                            interactionTerminee = true;
                        }
                    } else {
                        System.out.println(Main.ANSI_RED + "Pas assez d'or pour ce service." + Main.ANSI_RESET);
                    }
                    break;
                case "2":
                    if (equipe.getInventaireCommun().getOr() >= coutRetablirSante) {
                        Personnage persoAChoisir = selectionnerPersonnagePourSoin(equipe, scanner, "rétablir la santé", equipe, etage, nomJoueur);
                        if (persoAChoisir != null && Main.continuerJeuGlobal) {
                            equipe.getInventaireCommun().setOr(equipe.getInventaireCommun().getOr() - coutRetablirSante);
                            persoAChoisir.setSante(Personnage.SANTE_PLEINE_FORME); // Met le perso en pleine forme
                            System.out.println(Main.ANSI_GREEN + persoAChoisir.getNom() + " se sent revigoré et en pleine forme !" + Main.ANSI_RESET);
                            interactionTerminee = true;
                        }
                    } else {
                        System.out.println(Main.ANSI_RED + "Pas assez d'or pour ce service." + Main.ANSI_RESET);
                    }
                    break;
                case "3":
                    System.out.println("Vous remerciez l'ecclésiastique pour son temps et continuez votre chemin.");
                    interactionTerminee = true;
                    break;
                default:
                    System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET);
                    break;
            }
        }
        if(Main.continuerJeuGlobal) {
             String pause = Main.lireStringAvecMenuPause(scanner, Main.ANSI_YELLOW + "\nAppuyez sur Entrée pour continuer ('M' menu)..." + Main.ANSI_RESET, equipe, etage, nomJoueur);
             if("QUIT_GAME_INTERNAL".equals(pause)) Main.continuerJeuGlobal = false;
        }
    }

    private Personnage selectionnerPersonnagePourSoin(Equipe equipe, Scanner scanner, String action,
                                                      Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu) {
        if (equipe.getMembres().isEmpty()) return null;
        System.out.println("Quel personnage doit " + action + " ?");
        for (int i = 0; i < equipe.getMembres().size(); i++) {
            Personnage p = equipe.getMembres().get(i);
            System.out.println("  " + (i + 1) + ". " + p.getNom() + " (Santé: " + p.getSante() + ", PV: " + p.getPv() + "/" + p.getPvMax() + ")");
        }
        int choixPerso = Main.lireIntAvecMenuPause(scanner, "Choix (0 annuler, 'M' menu) : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu);
        if (!Main.continuerJeuGlobal || choixPerso == -999 || choixPerso == 0) return null;
        if (choixPerso > 0 && choixPerso <= equipe.getMembres().size()) {
            return equipe.getMembres().get(choixPerso - 1);
        }
        System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET);
        return null;
    }
}