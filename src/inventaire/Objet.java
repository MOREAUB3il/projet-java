package inventaire;
import personnage.Personnage;

public class Objet {
    private String nom;
    private String type;
    private String rarete;
    private String effet;
    private int valeur;

    
    private Objet(String nom, String type, String rarete, String effet, int valeur) {
        this.nom = nom;
        this.type = type;
        this.rarete = rarete;
        this.effet = effet;
        this.valeur = valeur;
    }

    
    public static Objet potionCommune() {
        return new Objet("Potion commune", "potion", "commune", "+10 PV", 10);
    }

    public static Objet potionRare() {
        return new Objet("Potion rare", "potion", "rare", "+25 PV", 25);
    }

    public static Objet potionLegendaire() {
        return new Objet("Potion légendaire", "potion", "légendaire", "+100 PV", 100);
    }

    
    public static Objet collierCommun() {
        return new Objet("Collier commun", "buff", "commune", "+1 Force", 1);
    }

    public static Objet collierRare() {
        return new Objet("Collier rare", "buff", "rare", "+2 Force", 2);
    }

    public static Objet collierLegendaire() {
        return new Objet("Collier légendaire", "buff", "légendaire", "+5 Force", 5);
    }

    
    public void utiliser(Personnage cible) {
        if (cible == null) {
            System.out.println("Aucune cible pour l'objet " + nom);
            return;
        }
        System.out.println(cible.getNom() + " utilise " + nom + ".");
        if (type.equals("potion")) {
            int pvActuels = cible.getPv();
            cible.setPv(pvActuels + valeur); 
            System.out.println("  " + cible.getNom() + " récupère " + valeur + " PV ! (Nouveaux PV: " + cible.getPv() + ")");
        } else if (type.equals("buff")) {
            int forceActuelle = cible.getForce(); 
            cible.setForce(forceActuelle + valeur); 
            System.out.println("  " + cible.getNom() + " gagne " + valeur + " en force ! (Nouvelle Force: " + cible.getForce() + ")");
        } else {
            System.out.println("  Effet de l'objet " + nom + " non reconnu ou non applicable.");
        }
    }
    public void afficher() {
        System.out.println(nom + " (" + rarete + ") - " + effet);
    }

    public int getPrix() {
        if (rarete.equals("commune")) return 10;
        else if (rarete.equals("rare")) return 20;
        else if (rarete.equals("légendaire")) return 50;
        else return 10;
    }

    public String getNom() {
        return nom;
    }
}
