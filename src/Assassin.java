import java.util.Random;

public class Assassin extends Personnage {
    private int force;
    private int penetration;
    
    
    public int getForce() {
        return force;
    }
    public void setForce(int force) {
        if (force >= 0) {
            this.force = force;
        }
    }
    public int getPenetration() {
        return penetration;
    }
    public void setPenetration(int penetration) {
        if (penetration >= 0) {
            this.penetration = penetration;
        }
    }
    
    private static final String[] NOMS_POSSIBLES = {
    		"Nyss la Lame Voilée","Kael Sombrelien","Vex du Souffle Mortel","Shyrra la Marche-Nuit","Orren des Mains Grises"
    };

    public Assassin() {
        super(nomAleatoire(),30);
        this.force = 20;
        this.penetration = 10;
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        this.force += 1;
        this.penetration += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Pénétration !");
    }
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = force + penetration * (cible.getDefence()/4);
        } else if (getNiveau() < 15) {
            degats = force + penetration * (cible.getDefence()/2);
        } else {
            degats = force + penetration * cible.getDefence();
        }
        System.out.println(getNom() + " 🗡️ attaque " + cible.getNom() + " avec sa dague !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
    @Override
    public void subirDegats(int degats) {
    	setPv(getPv() - degats);
        if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }

}
