package personnage;
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

}

