package inventaire;
import personnage.EffetTemporaire;
import personnage.Personnage;
import jeu.Main;
import monstre.Monstre;

public class Objet {
    private String nom;
    private String type;
    private String rarete;
    private String effet;
    private int valeur;
    private String cibleAutorisee;

    
    private Objet(String nom, String type, String rarete, String effet, int valeur, String cibleAutorisee) {
        this.nom = nom;
        this.type = type;
        this.rarete = rarete;
        this.effet = effet;
        this.valeur = valeur;
        this.cibleAutorisee = cibleAutorisee;
    }
    

    
    public static Objet potionCommune() {
        return new Objet("Potion commune 🔼💖", "potion", "commune", "+10 PV", 10, "ALLIE");
    }

    public static Objet potionRare() {
        return new Objet("Potion rare 🔼💖", "potion", "rare", "+25 PV", 25, "ALLIE");
    }

    public static Objet potionLegendaire() {
        return new Objet("Potion légendaire ⏫💖", "potion", "légendaire", "+100 PV", 100, "ALLIE");
    }

    
    public static Objet collierCommun() {
        return new Objet("Collier commun 🔼💪", "buff", "commune", "+1 Force", 1, "ALLIE");
    }

    public static Objet collierRare() {
        return new Objet("Collier rare 🔼💪", "buff", "rare", "+2 Force", 2, "ALLIE");
    }

    public static Objet collierLegendaire() {
        return new Objet("Collier légendaire ⏫💪", "buff", "légendaire", "+5 Force", 5, "ALLIE");
    }
    public static Objet fioleDePoison() {
		return new Objet("Fiole de poison 💀", "poison", "légendaire", "Poison", 0, "ENNEMI");
	}
    
    public void utiliser(Personnage utilisateur, Object cible) {
        if (cible == null) {
            System.out.println("Aucune cible valide pour l'objet " + nom);
            return;
        }

        System.out.println(Main.ANSI_YELLOW + utilisateur.getNom() + Main.ANSI_RESET + " utilise " + Main.ANSI_GREEN + nom + Main.ANSI_RESET + " sur " + Main.ANSI_YELLOW  + ((cible instanceof Personnage) ? ((Personnage)cible).getNom() : ((Monstre)cible).getNom()) + Main.ANSI_RESET + ".");

        switch (type) {
            case "potion":
                if (cible instanceof Personnage) {
                    Personnage pCible = (Personnage) cible;
                    int pvActuels = pCible.getPv();
                    pCible.setPv(Math.min(pvActuels + valeur, pCible.getPvMax())); 
                    System.out.println("  " + Main.ANSI_GREEN + pCible.getNom() + " récupère " + valeur + " PV ! (Nouveaux PV: " + pCible.getPv() + "/" + pCible.getPvMax() + ")" + Main.ANSI_RESET);
                } else {
                    System.out.println("  " + Main.ANSI_RED + "Cet objet ne peut être utilisé que sur un personnage." + Main.ANSI_RESET);
                }
                break;

            case "buff":
                if (cible instanceof Personnage) {
                    Personnage pCible = (Personnage) cible;
                    EffetTemporaire buff = null;
                    if (this.nom.equals(collierCommun().getNom())) buff = EffetTemporaire.buffForceFaible();
                    else if (this.nom.equals(collierRare().getNom())) buff = EffetTemporaire.buffForceMoyen();
                    else if (this.nom.equals(collierLegendaire().getNom())) buff = EffetTemporaire.buffForceForte();

                    if (buff != null) {
                        pCible.appliquerEffet(buff);
                    } else {
                        System.out.println("  " + Main.ANSI_RED + "Type de collier non reconnu pour le buff." + Main.ANSI_RESET);
                    }
                } else {
                    System.out.println("  " + Main.ANSI_RED + "Cet objet ne peut être utilisé que sur un personnage." + Main.ANSI_RESET);
                }
                break;

            case "poison":
                EffetTemporaire poison = EffetTemporaire.poison();
                if (cible instanceof Personnage) {
                    ((Personnage) cible).appliquerEffet(poison);
                } else if (cible instanceof Monstre) {
                    ((Monstre) cible).appliquerEffet(poison);
                } else {
                    System.out.println("  " + Main.ANSI_RED + "Cible invalide pour le poison." + Main.ANSI_RESET);
                }
                break;

            default:
                System.out.println("  " + Main.ANSI_RED + "Effet de l'objet " + nom + " non reconnu ou non applicable à cette cible." + Main.ANSI_RESET);
                break;
        }
    }

    public static Objet creerObjetParNom(String nomObjet) {
        if (nomObjet == null || nomObjet.trim().isEmpty()) return null;
        if (nomObjet.equalsIgnoreCase(potionCommune().getNom())) return potionCommune();
        if (nomObjet.equalsIgnoreCase(potionRare().getNom())) return potionRare();
        if (nomObjet.equalsIgnoreCase(potionLegendaire().getNom())) return potionLegendaire();
        if (nomObjet.equalsIgnoreCase(collierCommun().getNom())) return collierCommun();
        if (nomObjet.equalsIgnoreCase(collierRare().getNom())) return collierRare();
        if (nomObjet.equalsIgnoreCase(collierLegendaire().getNom())) return collierLegendaire();
        if (nomObjet.equalsIgnoreCase(fioleDePoison().getNom())) return fioleDePoison();
        System.err.println("Objet inconnu lors du chargement: " + nomObjet);
        return null;
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
	public String getCibleAutorisee() {
		return cibleAutorisee;
	}
}
