import java.util.Random;

public class Necromancien  extends Mage{
	
	private Necromancien() {
        super(nomAleatoire());
    }
	
	
	
	private static final String[] NOMS_POSSIBLES = {
	        " 🧙‍♂️ Thalor le Moissonneur ☠️ ", "🧙‍♂️ Ezrakar l'Éveilleur de Tombes ☠️ ","🧙‍♂️ Nyssara la Voix des Morts ☠️ ","🧙‍♂️ Voldren l’Échine Brisée ☠️ "," 🧙‍♂️ Malrith le Tisseur d’Âmes ☠️ "
	    };
	
	private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
	
	
	
	public void attaque1(Monstre cible) {
    	int degats;
    	if (getNiveau() < 5) {
            degats = getForce() + getMalediction() * (cible.getPv()/4);
        } else if (getNiveau() < 15) {
            degats = getForce() + getMalediction() * (cible.getDefence()/2);
        } else {
            degats = getForce() + getMalediction() * cible.getDefence();
        }
        System.out.println(getNom() + " 🪄 attaque " + cible.getNom() + " avec son baton !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats); 
    }
}
