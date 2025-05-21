package personnage;
public abstract class Tank extends Personnage {
	
private int defense;
    
    public int getDefense() {
        return defense;
    }
    public void setDefense(int defense) {
        if (defense >= 0) {
            this.defense = defense;
        }
    }
    public Tank(String nom,String tank) {
    	super(nom, tank);
        this.setSante(getSante());
        this.defense = 10;
    }
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        setForceBase(getForce() + 1);
        defense += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Défense !");
    }
    @Override
    public void subirDegats(int degats) {
    	int degatsReduits = degats * (100 - defense) / 100;
    	setPv(getPv() - degatsReduits);
    	if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
}
