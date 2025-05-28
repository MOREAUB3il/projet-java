package event;

import jeu.Equipe;
import java.util.Scanner;
import java.util.Random;
import jeu.Main;
import personnage.Personnage;

public class TerrainRuine extends Evenement {

    public TerrainRuine() {
        super("Ruines Mystérieuses", 
              "Vous tombez sur les vestiges d'une ancienne structure en ruine. L'air est lourd et chargé d'histoire... ou de danger.");
    }

    @Override
    public void declencher(Equipe equipe, Scanner scanner, int etage, String nomJoueur,int idSauvegardeActive) {
        afficherDebutEvenement();
        Random random = new Random();

        String choixAction = Main.lireStringAvecMenuPause(scanner,
                "Que voulez-vous faire ?\n  1. Fouiller les ruines (Piller)\n  2. Continuer votre chemin (Ignorer)\nChoix ('M' menu) : ",
                equipe, etage, nomJoueur, idSauvegardeActive);

        if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(choixAction)) return;

        if ("1".equals(choixAction)) {
            System.out.println("Vous décidez de fouiller prudemment les décombres...");
            try { Thread.sleep(1500); } catch (InterruptedException e) {}

            int resultatRoll = random.nextInt(100);

            if (resultatRoll < 40) {
                int orGagne = 15 + random.nextInt(11);
                System.out.println(Main.ANSI_GREEN + "Chanceux ! Vous trouvez une petite bourse contenant " + orGagne + " pièces d'or !" + Main.ANSI_RESET);
                equipe.getInventaireCommun().ajouterOr(orGagne);
            } else if (resultatRoll < 80) {
                System.out.println(Main.ANSI_YELLOW + "Après une recherche minutieuse, vous ne trouvez rien d'intéressant." + Main.ANSI_RESET);
            } else { // 80-99 (20%)
                System.out.println(Main.ANSI_RED + "Vous tombez sur un cadavre en décomposition... Une odeur nauséabonde se dégage." + Main.ANSI_RESET);
                if (!equipe.getMembres().isEmpty()) {
                    Personnage victime = equipe.getMembres().get(random.nextInt(equipe.getMembres().size()));
                    System.out.println(victime.getNom() + " semble avoir été affecté par quelque chose...");
                    victime.appliquerMaladie();
                }
            }
        } else if ("2".equals(choixAction)) {
            System.out.println("Vous décidez que le risque n'en vaut pas la peine et continuez votre route.");
        } else {
            System.out.println(Main.ANSI_YELLOW + "Vous hésitez et finalement, vous ne faites rien de spécial dans ces ruines." + Main.ANSI_RESET);
        }

        if(Main.continuerJeuGlobal) {
             String pause = Main.lireStringAvecMenuPause(scanner, Main.ANSI_YELLOW + "\nAppuyez sur Entrée pour continuer ('M' menu)..." + Main.ANSI_RESET, equipe, etage, nomJoueur, idSauvegardeActive);
             if("QUIT_GAME_INTERNAL".equals(pause)) Main.continuerJeuGlobal = false;
        }
    }
}