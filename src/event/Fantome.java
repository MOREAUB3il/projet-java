package event;

import jeu.Equipe;
import personnage.Personnage;
import personnage.Enchanteur;
import java.util.Scanner;
import java.util.Random;
import jeu.Main;

public class Fantome extends Evenement {

    public Fantome() {
        super("Apparition Spectrale",
              "Une présence glaciale envahit la zone. Une forme éthérée et gémissante se matérialise devant vous !");
    }

    @Override
    public void declencher(Equipe equipe, Scanner scanner, int etage, String nomJoueur, int idSauvegardeActive) {
        afficherDebutEvenement();
        Random random = new Random();
        boolean enchanteurDansEquipe = false;
        Personnage enchanteur = null;

        for (Personnage p : equipe.getMembres()) {
            if (p instanceof Enchanteur) {
                enchanteurDansEquipe = true;
                enchanteur = p;
                break;
            }
        }

        if (enchanteurDansEquipe) {
            System.out.println(Main.ANSI_CYAN + enchanteur.getNom() + " sent la présence et se prépare à intervenir." + Main.ANSI_RESET);
            int orGagne = 10 + random.nextInt(6);
            System.out.println(enchanteur.getNom() + " effectue un rituel rapide. Le fantôme se dissipe avec un dernier soupir !");
            System.out.println(Main.ANSI_GREEN + "Vous trouvez " + orGagne + "G sur les lieux après sa disparition." + Main.ANSI_RESET);
            equipe.getInventaireCommun().ajouterOr(orGagne);
        } else {
            System.out.println("Le fantôme vous fixe de ses orbites vides...");
            if (random.nextBoolean()) {
                if (!equipe.getMembres().isEmpty()) {
                    Personnage victime = equipe.getMembres().get(random.nextInt(equipe.getMembres().size()));
                    System.out.println(Main.ANSI_RED + "Le contact glacial du fantôme affaiblit " + victime.getNom() + " !" + Main.ANSI_RESET);
                    String santeActuelle = victime.getSante();
                    String nouvelleSante = santeActuelle;

                    if (santeActuelle.equals(Personnage.SANTE_PLEINE_FORME)) nouvelleSante = Personnage.SANTE_FATIGUE;
                    else if (santeActuelle.equals(Personnage.SANTE_FATIGUE)) nouvelleSante = Personnage.SANTE_MALADE;
                    else if (santeActuelle.equals(Personnage.SANTE_MALADE)) nouvelleSante = Personnage.SANTE_EPUISE;
                    else if (santeActuelle.equals(Personnage.SANTE_EPUISE)) nouvelleSante = Personnage.SANTE_SUR_LA_FIN;

                    if (!nouvelleSante.equals(santeActuelle)) {
                        victime.setSante(nouvelleSante);
                    } else {
                        System.out.println(victime.getNom() + " résiste à l'influence néfaste ou est déjà très affaibli.");
                    }
                }
            } else {
                System.out.println("Après un moment de tension, le fantôme s'évanouit sans incident.");
            }
        }
        if(Main.continuerJeuGlobal) {
             String pause = Main.lireStringAvecMenuPause(scanner, Main.ANSI_YELLOW + "\nAppuyez sur Entrée pour continuer ('M' menu)..." + Main.ANSI_RESET, equipe, etage, nomJoueur, idSauvegardeActive);
             if("QUIT_GAME_INTERNAL".equals(pause)) Main.continuerJeuGlobal = false;
        }
    }
}