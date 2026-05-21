package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enchanteur extends Support {

    ArrayList<Personnage> equipe;
    
    private static final String[] NOMS_POSSIBLES = {
        " ✨ Morvayn l’Obscur 📜 ", " ✨ Ryze le Runique 📜 "," ✨ Selyne la Tisseuse de Malheur 📜 ", " ✨ Malzar le Prophete du Neant 📜 ", " ✨ Lee Sin Le Moine Aveugle 📜 "
    };

    public Enchanteur() {
        super(nomAleatoire(),"support");
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

    public void ulti(Monstre cible){ //scelle les ennemis( 20,25,30% de force en moins sur 1,2,3 tours) + 10 pv  de soin sur toute la team
        int force = getForce();
        int degats = 0;

        if (cible.estVivant()) {
            if (getNiveau() < 5) {
                degats = force / 5;
            } else if (getNiveau() < 15) {
                degats = force / 4;
            } else {
                degats = force / 3;
            }
            int soin = 10;
            this.setPv(this.getPv() + soin);
            System.out.println(getNom() + " 📜 utilise son ultime sur " + cible.getNom() + " !");
            System.out.println("Cela inflige " + degats + " dégâts !");
            cible.subirDegats(degats);
        } else {
            System.out.println(cible.getNom() + " est déjà mort !");
        }
        
    }
    public void ultiSoigner(List<Personnage> equipe){ 
    }
}
