package personnage;

import monstre.Monstre;

import java.util.List;
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
            degats = getForce() + getMalediction() * (cible.getPv()/4);
        } else if (getNiveau() < 15) {
            degats = getForce() + getMalediction() * (cible.getDefence()/2);
        } else {
            degats = getForce() + getMalediction() * cible.getDefence();
        }
        System.out.println(getNom() + "  attaque " + cible.getNom() + " avec son baton 🪄 !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }



    
    public void ultiAtt(Monstre cible) { //force + malediction + si monstre meurt il se retourne est attaque son alliés pour 70,80,100% des  degats qu’il a subit
        int degats = getForce() + getMalediction() ;
        System.out.println(getNom() + " lance son attaque ultime sur " + cible.getNom() + " !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        cible.subirDegats(degats);

        
            
        if (cible.getPv() <= 0) {
            System.out.println(cible.getNom() + " est mort !");
            
                int degatsRetour = (int) (degats * 0.4); // 40% des dégâts subis
                System.out.println(cible.getNom() + " attaque " + cible.getNom() + " en mourant pour " + degatsRetour + " dégâts !");
                cible.subirDegats(degatsRetour);
        
        }
    }


    public void ultiSoigner(List <Personnage> equipe){
    }
}
