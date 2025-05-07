package personnage;

import monstre.Monstre;
import java.util.Random;

public  class Assassin extends Dps {
    
    private static final String[] NOMS_POSSIBLES = {
    		"🔪 Nyss la Lame Voilée 🗡️ ","🔪 Kael Sombrelien 🗡️","🔪 Vex du Souffle Mortel 🗡️ ","🔪 Shyrra la Marche-Nuit 🗡️ ","🔪 Orren des Mains Grises 🗡️ "
    };

    public Assassin() {
        super(nomAleatoire(),"dps");
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
   
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + getPenetration() * (cible.getDefence()/4);
        } else if (getNiveau() < 15) {
            degats = getForce() + getPenetration() * (cible.getDefence()/2);
        } else {
            degats = getForce() + getPenetration() * cible.getDefence();
        }
        System.out.println(getNom() + " 🗡️ attaque " + cible.getNom() + " avec sa dague !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }


}
