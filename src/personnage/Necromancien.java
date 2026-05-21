package personnage;

import monstre.Monstre;
import java.util.Random;

public class Necromancien  extends Mage{
	
	public Necromancien() {
        super(nomAleatoire(),"mage");
    }
	
	
	
	private static final String[] NOMS_POSSIBLES = {
	        " 🧙‍♂️ Thalor le Moissonneur ☠️ ", "🧙‍♂️ Ezrakar l'Éveilleur de Tombes ☠️ ","🧙‍♂️ Nyssara la Voix des Morts ☠️ ","🧙‍♂️ Voldren l’Échine Brisée ☠️ "," 🧙‍♂️ Ainz le Roi Sorcier ☠️ "
	    };
	
	private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
	
	
	
	public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + getMalediction() + (cible.getPv()/8);
        } else if (getNiveau() < 15) {
            degats = getForce() + getMalediction() + (cible.getPv()/4);
        } else {
            degats = getForce() + getMalediction() + (cible.getPv()/2);
        }
        System.out.println(getNom() + "  attaque " + cible.getNom() + " avec son baton 🪄 !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
	public void ulti(Monstre cible) { 
        int degats = getForce() + getMalediction() ;
        System.out.println(getNom() + " lance son attaque ultime sur " + cible.getNom() + " !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        cible.subirDegats(degats);


            
        if (cible.getPv() <= 0) {
            System.out.println(cible.getNom() + " est mort !");
            int degatsRetour = (int) (degats * 0.4);
            System.out.println(getNom() + " absorbe l'âme de " + cible.getNom() + " et récupère " + degatsRetour + " PV !");
            this.setPv(Math.min(this.getPv() + degatsRetour, this.getPvMax()));
        }
    }
}
