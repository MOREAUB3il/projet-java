import java.util.Random;

public class Mage extends Personnage {
    private int force;
    private int malediction;
    
    public int getForce() {
        return force;
    }
    public void setForce(int force) {
        if (force >= 0) {
            this.force = force;
        }
    }
    public int getMalediction() {
        return malediction;
    }
    public void setPenetration(int malediction) {
        if (malediction >= 0) {
            this.malediction = malediction;
        }
    }

    private static final String[] NOMS_POSSIBLES = {
        "Maelis l’Arcanombre", "Vorthen le Briseur de Vœux","Zhallik le Runemort","Liraë la Flamme Déchue","Sevrak l’Esprit Fendu"
    };

    public Mage() {
        super(nomAleatoire(),30);
        this.force = 20;
        this.malediction = 10;
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
        this.malediction += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Malédiction !");
    }
    public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = force + malediction * (cible.getPv()/4);
        } else if (getNiveau() < 15) {
            degats = force + malediction * (cible.getDefence()/2);
        } else {
            degats = force + malediction * cible.getDefence();
        }
        System.out.println(getNom() + " 🪄 attaque " + cible.getNom() + " avec son baton !");
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
