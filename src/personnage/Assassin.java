package personnage;

import monstre.Monstre;

import java.util.List;
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
            degats = getForce() + getPenetration() + (cible.getDefence());
        } else if (getNiveau() < 15) {
            degats = getForce() + getPenetration() + (cible.getDefence()*2);
        } else {
            degats = getForce() + getPenetration() + (cible.getDefence()*4);
        }
        System.out.println(getNom() + "  attaque " + cible.getNom() + " avec sa dague 🗡️ !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }


    public void ulti(Monstre cible){
        
                int degatsBase = getForce() + getPenetration() * (cible.getDefence() / 5); 
                int degats = degatsBase;
            
               Random rand = new Random();
               boolean coupCritique = rand.nextInt(100) < 30;
            
                if (coupCritique) {
                    degats *= 2;
                    System.out.println("Coup critique ! 💥");
                }
            
                System.out.println(getNom() + " réalise une attaque ultime sur " + cible.getNom() + " !");
                System.out.println("Cela inflige " + degats + " dégâts dévastateurs !");
                
                cible.subirDegats(degats);
            }
            
         public void ultiSoigner(List <Personnage> equipe){ 
    }

}
