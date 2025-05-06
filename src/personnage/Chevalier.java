package personnage;

import monstre.Monstre;
import java.util.Random;

public class Chevalier extends Tank {
    

    private static final String[] NOMS_POSSIBLES = {
    		"🛡️ Sir Malrik le Cendré ⚔️", "🛡️ Dame Virelda de l’Éclipse ⚔️", "🛡️ Tharion l’Écorcheur ⚔️", "🛡️ Sir Vandar du Glas Noir ⚔️", "🛡️ Edran le Jugement Silencieux ⚔️"
    };

    public Chevalier() {
    	super(nomAleatoire());
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
    
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + (5 * getPv()) / 100;
        } else if (getNiveau() < 15) {
            degats = getForce() + (10 * getPv()) / 100;
        } else {
            degats = getForce() + (15 * getPv()) / 100;
        }
        System.out.println(getNom() + " ⚔️ attaque " + cible.getNom() + " avec son épée !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
    
}
