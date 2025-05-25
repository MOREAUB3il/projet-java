package personnage;

import monstre.Monstre;
import personnage.EffetTemporaire;

import java.util.List;
import java.util.Random;

public class Pyromancien  extends Mage{
	
	public Pyromancien() {
        super(nomAleatoire(),"mage");
    }
	
	
	
	private static final String[] NOMS_POSSIBLES = {
	        "🧙‍♂️ Vaelrik Cendreflamme 🔥 ", "🧙‍♂️ Ashara la Fournaise Écarlate 🔥 ","🧙‍♂️ Drazul le Brasier Noir 🔥 ","🧙‍♂️ Kaelthar le Fils du Brasier 🔥 ","🧙‍♂️ Morgath Sombreflamme 🔥 "
	    };
	
	private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
	
	
	public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + getMalediction() * (cible.getPv()/4);
        } else if (getNiveau() < 15) {
            degats = getForce() + getMalediction() * (cible.getDefence()/2);
        } else {
            degats = getForce() + getMalediction() * cible.getDefence();
        }
        System.out.println(getNom() + " attaque " + cible.getNom() + " avec son baton 🪄 !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }

    public void ultiAtt(Monstre cible){ //force + 5,10,20% hp max monstre + malediction brule les ennemie pour 50,100,150% de degats sur  le prochain tour
        int degats = (int) (getForce() * 1.2 + 0.05 * cible.getPvMax());
        System.out.println(getNom() + " lance une attaque ultime sur " + cible.getNom() + " !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats);
        cible.appliquerEffet(EffetTemporaire.brulureLegere());

    }

    public void ultiSoigner(List<Personnage> equipe){

    }
}
