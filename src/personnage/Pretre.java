package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.Random;

public class Pretre extends Support {

    ArrayList<Personnage> equipe;
    
    private static final String[] NOMS_POSSIBLES = {
        " ✨ Père Aldric du Sang Bénit 🪬 ", " ✨ Sœur Ylva de la Litanie Noire 🪬 "," ✨ Frère Caelen l’Expiateur 🪬 ", " ✨ Archinquisiteur Thamos 🪬 ", " ✨ Mère Eriss la Voix du Néant 🪬 "
    };

    public Pretre() {
        super(nomAleatoire());
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }

    public void attaque1(Monstre cible) {
        int degats = getForce();

        soinCibleFaible(equipe);

        System.out.println(getNom() + " 🪬 attaque " + cible.getNom() + " avec son sceau !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        cible.subirDegats(degats);
    }

 
}
