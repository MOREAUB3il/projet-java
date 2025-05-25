package personnage;

import monstre.Monstre;

public abstract class Mage extends Personnage {
    private int malediction;
    
    public int getMalediction() {
        return malediction;
    }
    public void setPenetration(int malediction) {
        if (malediction >= 0) {
            this.malediction = malediction;
        }
    }

   
    public Mage(String nom, String mage) {
        super(nom, mage);
        this.malediction = 10;
        this.setSante(getSante());
    }

    
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        setForceBase(getForce() + 2);
        this.malediction += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Malédiction !");
    }
    
    @Override
    public void subirDegats(int degats) {
    	setPv(getPv() - degats);
        if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
    public void ulti(Monstre cible) {
        System.out.println(getNom() + " utilise son ultime !");
        setPv(getPv() + 10);
        setForceBase(getForce() + 5);
        this.malediction += 5;
        System.out.println(getNom() + " gagne +10 PV, +5 Force, +5 Malédiction !");
    }
}
