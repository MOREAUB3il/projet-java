import java.util.Random;

public class Chevalier extends Personnage {

    private int force;
    private int defense;
    
    
    public int getForce() {
        return force;
    }
    public void setForce(int force) {
        if (force >= 0) {
            this.force = force;
        }
    }
    public int getDefence() {
        return defense;
    }
    public void setDefence(int defense) {
        if (defense >= 0) {
            this.defense = defense;
        }
    }

    private static final String[] NOMS_POSSIBLES = {
    		"Sir Malrik le Cendré", "Dame Virelda de l’Éclipse", "Tharion l’Écorcheur", "Sir Vandar du Glas Noir", "Edran le Jugement Silencieux"
    };

    public Chevalier() {
        super(nomAleatoire(),50);
        this.force = 10;
        this.defense = 10;
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        force += 1;
        defense += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Défense !");
    }
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = force + (5 * getPv()) / 100;
        } else if (getNiveau() < 15) {
            degats = force + (10 * getPv()) / 100;
        } else {
            degats = force + (15 * getPv()) / 100;
        }
        System.out.println(getNom() + " ⚔️ attaque " + cible.getNom() + " avec son épée !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
    @Override
    public void subirDegats(int degats) {
    	int degatsReduits = degats * (100 - defense) / 100;
    	setPv(getPv() - degatsReduits);
    	if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
}
