package personnage;

import monstre.Monstre;

public abstract class Dps extends Personnage {
    private int penetration;
    
    public int getPenetration() {
        return penetration;
    }
    public void setPenetration(int penetration) {
        if (penetration >= 0) {
            this.penetration = penetration;
        }
    }
    
    public Dps(String nom, String dps) {
    	super(nom, dps);
        this.setSante(getSante());
        this.penetration = 10;
    }

    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        setForceBase(getForce() + 2);
        this.penetration += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Pénétration !");
    }
   
    @Override
    public void subirDegats(int degats) {
    	setPv(getPv() - degats);
        if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
    public void ultiAtta(Monstre cible){ // force + pénétration + force cible + si ennemi meurt le monstre derrière a 50,100,150% des dégats infligés
        int degats;
        if (getNiveau() < 5) {
            degats = getForce() + getPenetration() + (cible.getPv() / 4);
        } else if (getNiveau() < 15) {
            degats = getForce() + getPenetration() + (cible.getDefence() / 2);
        } else {
            degats = getForce() + getPenetration() + cible.getDefence();
        }
        System.out.println(getNom() + " attaque " + cible.getNom() + " avec son arme ! 🔥");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats);

    }
}

