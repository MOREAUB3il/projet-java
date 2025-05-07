
public class Main {

	public static void main(String[] args) {
		System.out.println("teste");

		        Pyromancien pyro = new Pyromancien();
		        System.out.println("Nouveau personnage : " + pyro.getNom());
		        System.out.println("Nom : " + pyro.getNom());
		        System.out.println("Santé : " + pyro.getSante());
		        System.out.println("Force : " + pyro.getForce());
		        System.out.println("Niveau : " + pyro.getNiveau());
		        System.out.println("XP : " + pyro.xp + "/" + pyro.xpPourNiveauSuivant);
		        System.out.println("Or : " + pyro.getOr());
		        System.out.println("Inventaire : " + pyro.inventaire.size() + " objet(s)");
		        Objet potion = Objet.potionCommune();
		        

		    }
		}

	


