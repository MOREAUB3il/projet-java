package personnage;

import monstre.Monstre;
import java.util.Random;
import jeu.Main; 

public class Pyromancien extends Mage {

    private static final String[] NOMS_POSSIBLES = {
            "🧙‍♂️ Vaelrik Cendreflamme 🔥", "🧙‍♂️ Ashara la Fournaise Écarlate 🔥",
            "🧙‍♂️ Drazul le Brasier Noir 🔥", "🧙‍♂️ Kaelthar le Fils du Brasier 🔥",
            "🧙‍♂️ Morgath Sombreflamme 🔥"
    };

    public Pyromancien() {
        super(nomAleatoire(), "mage");
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }

    @Override
    public void attaque1(Monstre cible) {
        if (cible == null || !cible.estVivant()) {
            System.out.println(getNom() + " n'a pas de cible valide.");
            return;
        }

        int forceEffective = getForce(); 
        int maledictionEffective = getMalediction();
        int degatsBase = forceEffective + (maledictionEffective / 2); 


        int bonusDegatsPvCible = (int) (cible.getPv() * (maledictionEffective * 0.005)); 
        degatsBase += bonusDegatsPvCible;


        if (getCompLvl() == 2) {
            degatsBase = (int) (degatsBase * 1.2); 
        } else if (getCompLvl() >= 3) {
            degatsBase = (int) (degatsBase * 1.4); 
        }
        degatsBase = Math.max(1, degatsBase);



        System.out.println(Main.ANSI_CYAN + getNom() + Main.ANSI_RESET + " lance une boule de feu 🔥 sur " + Main.ANSI_RED + cible.getNom() + Main.ANSI_RESET + " !");
        System.out.println("Cela inflige " +Main.ANSI_YELLOW + degatsBase + Main.ANSI_RESET + " dégâts !");
        cible.subirDegats(degatsBase);


        if (cible.estVivant() && new Random().nextInt(100) < (30 + getCompLvl() * 10)) {
            System.out.println(Main.ANSI_RED + cible.getNom() + Main.ANSI_RESET + " prend feu légèrement !");
            cible.appliquerEffet(EffetTemporaire.brulureLegere());
        }
    }

    @Override
    public void ulti(Monstre cible) {
        if (cible == null || !cible.estVivant()) {
            System.out.println(getNom() + " a besoin d'une cible vivante pour son cataclysme de feu !");
            return;
        }
        if (getUltLvl() == 0) {
            System.out.println(getNom() + " n'a pas encore maîtrisé cet art destructeur.");
            return;
        }



        System.out.println(Main.ANSI_BOLD + Main.ANSI_RED + getNom() + " invoque un CATACLYSME DE FEU sur " + cible.getNom() + " ! 🔥🔥🔥" + Main.ANSI_RESET);

        int forceEffective = getForce();
        int maledictionEffective = getMalediction();
        int degatsInitiaux = 0;

        if (getUltLvl() == 1) {
            degatsInitiaux = forceEffective * 2 + maledictionEffective * 3; 
        } else if (getUltLvl() == 2) {
            degatsInitiaux = forceEffective * 3 + maledictionEffective * 4;
        } else { // ultLvl 3+
            degatsInitiaux = forceEffective * 4 + maledictionEffective * 5; 
        }
        degatsInitiaux = Math.max(5, degatsInitiaux); 

        System.out.println("L'explosion initiale inflige " + Main.ANSI_YELLOW + degatsInitiaux + Main.ANSI_RESET + " dégâts de feu !");
        cible.subirDegats(degatsInitiaux);

        if (cible.estVivant()) {
            System.out.println(cible.getNom() + " est enveloppé de flammes maudites et subira plus de dégâts !");
            cible.appliquerEffet(EffetTemporaire.brulureIntense());
        }
    }
}