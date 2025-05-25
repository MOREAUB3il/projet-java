package personnage;


import monstre.Monstre;

import java.util.List;
import java.util.Random;

import jeu.Main;

public  class Barbare extends Dps {
    
    private static final String[] NOMS_POSSIBLES = {
    		" 🔪 Gorvok Crâne-Sanglant 🪓 "," 🔪 Olaf le Berserker 🪓 "," 🔪 Varkh le Hurleur d’Abîme 🪓 ","🔪 Korgash le Maudit 🪓 "," 🔪 Dragan Croc-de-Fer 🪓 "
    };

    public Barbare() {
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
        System.out.println(Main.ANSI_CYAN + getNom() + " 🗡️ attaque " + cible.getNom() + " avec sa dague !" + Main.ANSI_RESET);
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
    public void ultiAtt(Monstre cible){
        int force = getForce();
        int penetration = getPenetration();
        int degats = 0;

        if (cible.estVivant()) {
            if (getNiveau() < 5) {
                degats = force + 2 * penetration + (15 * force) / 100;
            } else if (getNiveau() < 15) {
                degats = force + 2 * penetration + (30 * force) / 100;
            } else {
                degats = force + 2 * penetration + (60 * force) / 100;
            }
            System.out.println(Main.ANSI_CYAN+ getNom() + " ⚔️ utilise son ultime sur " + cible.getNom() + " !" + Main.ANSI_RESET);
            System.out.println("Cela inflige " + degats + " dégâts !");
            cible.subirDegats(degats);
        } else {
            System.out.println(cible.getNom() + " est déjà mort !");
        }

    }
     public void ultiSoigner(List <Personnage> equipe){ 
    }
}