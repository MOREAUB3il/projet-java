package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.Random;

public class Enchanteur extends Support {

    ArrayList<Personnage> equipe;
    
    private static final String[] NOMS_POSSIBLES = {
        " ✨ Morvayn l’Obscur 📜 ", " ✨ Ryze le Runique 📜 "," ✨ Selyne la Tisseuse de Malheur 📜 ", " ✨ Malzar le Prophete du Neant 📜 ", " ✨ Lee Sin Le Moine Aveugle 📜 "
    };

    public Enchanteur() {
        super(nomAleatoire());
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }



    public void attaque1(Monstre cible) {
        int degats = getForce();

        

        System.out.println(getNom() + " 🪬 attaque " + cible.getNom() + " avec son sceau !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        cible.subirDegats(degats);
    }

 
}
