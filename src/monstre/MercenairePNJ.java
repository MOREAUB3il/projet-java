package monstre;

import personnage.Personnage; 

public class MercenairePNJ extends Monstre {

    public MercenairePNJ(int etageJoueur) {

        super("Mercenaire Arrogant", 
              75 + (etageJoueur * 3),  
              15 + (etageJoueur * 1),  
              5 + (etageJoueur / 2),   
              0.01,                   
              0,                      
              50 + (etageJoueur * 5),
              etageJoueur);
    }


    public void attaquer(Personnage cible) {
        System.out.println(getNom() + " vous lance un regard mauvais et frappe avec son épée rouillée !");
        super.attaquer(cible);
    }
}