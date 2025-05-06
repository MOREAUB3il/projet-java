package personnage;

import monstre.Monstre;
import java.util.Random;

public class Paladin extends Tank {
    

    private static final String[] NOMS_POSSIBLES = {
    		"🛡️ Ser Vaelgrim le Porte-Nuit 🔨", "🛡️ Dame Ysmera de l'Aube Cendrée 🔨", "🛡️ Tharion Morvain, le Justicier Obscur 🔨", " 🛡️ Elandros le Fléau des Profanés 🔨", "🛡️ Sir Caldrak du Sanctuaire Brisé 🔨"
    };
    
    public Paladin() {
    	super(nomAleatoire());
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
    
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + (70 * getDefense()) / 100;
        } else if (getNiveau() < 15) {
            degats = getForce() + (80 * getDefense()) / 100;
        } else {
            degats = getForce() + (100  * getDefense()) / 100;
        }
        System.out.println(getNom() + " ⚔️ attaque " + cible.getNom() + " avec son marteau de guerre !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
    
}
