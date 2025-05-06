package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.Random;

public class Pretre extends Support {

    ArrayList<Personnage> equipe;
    
    private static final String[] NOMS_POSSIBLES = {
        "Père Aldric du Sang Bénit", "Sœur Ylva de la Litanie Noire",
        "Frère Caelen l’Expiateur", "Archinquisiteur Thamos", "Mère Eriss la Voix du Néant"
    };

    public Pretre() {
        super(nomAleatoire());
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }



    
    public void soigner(Personnage cible) {
        int montant = (getSoin() * cible.getPvMax()) / 100;
        cible.setPv(Math.min(cible.getPv() + montant, cible.getPvMax()));

        System.out.println(getNom() + " 💖 soigne " + cible.getNom() + " de " + montant + " PV !");
        System.out.println(cible.getNom() + " a maintenant " + cible.getPv() + "/" + cible.getPvMax() + " PV.");
    }

    public void soinCibleFaible(ArrayList<Personnage> equipe) {
        Personnage plusBlesse = null;
        int pvMin = Integer.MAX_VALUE;

        for (Personnage p : equipe) {
            if (p == this) continue;
            if (p.getPv() < pvMin) {
                pvMin = p.getPv();
                plusBlesse = p;
            }
        }

        if (plusBlesse != null) {
            int montantSoin = (getSoin() * plusBlesse.getPvMax()) / 100;
            plusBlesse.setPv(Math.min(plusBlesse.getPv() + montantSoin, plusBlesse.getPvMax()));

            System.out.println(getNom() + " 💖 soigne " + plusBlesse.getNom() + " de " + montantSoin + " PV !");
            System.out.println(plusBlesse.getNom() + " a maintenant " + plusBlesse.getPv() + "/" + plusBlesse.getPvMax() + " PV.");
        } else {
            System.out.println(getNom() + " n'a trouvé personne à soigner.");
        }
    }

    public void attaque1(Monstre cible) {
        int degats = getForce();

        if (getNiveau() < 5) {
            soinCibleFaible(equipe);
        }

        System.out.println(getNom() + " 🪬 attaque " + cible.getNom() + " avec son sceau !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        cible.subirDegats(degats);
    }

 
}
