package personnage;
public abstract class Support extends Personnage {
	
private int soin;
    
    public int getSoin() {
        return soin;
    }
    public void setSoin(int soin) {
        if (soin >= 0) {
            this.soin = soin;
        }
    }
    public Support(String nom) {
    	super(nom);
        this.setSante(getSante());
        this.soin = 10;
    }
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        setForce(getForce() + 1);
        soin += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Soin !");
    }
    @Override
    public void subirDegats(int degats) {
    	int degatsReduits = degats * (100 - soin) / 100;
    	setPv(getPv() - degatsReduits);
    	if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
}
