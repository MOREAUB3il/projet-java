package personnage;
import monstre.Monstre;

import java.util.List;
import java.util.Random;

import inventaire.Inventaire;

public abstract class Personnage {
    private String nom;
    private String sante;
    private int pv;
    private int pvMax;
    private int force;
    protected int niveau;
    protected int xp;
    protected int xpPourNiveauSuivant;
    private String type;


    public Personnage(String nom, String type) {
        this.nom = nom;
        this.sante = santeAleatoire();
        this.pvMax = getPvParSante(sante);
        this.force = getForceParSante(sante);
        this.pv = pvMax;
        this.niveau = 1;
        this.xp = 0;
        this.xpPourNiveauSuivant = 100;
        this.type = type;
    }
    public void afficherStat() {
    	System.out.println(getNom());
    	System.out.println("Etat de sante :"+getSante());
    	System.out.println("Pv :"+getPv());
    	System.out.println("Pv max :"+getPvMax());
    	System.out.println("Force :"+getForce());
    	System.out.println("Niveau :"+getNiveau());
    	afficherBarreXp();
    }
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getSante() {
		return sante;
	}

	public void setSante(String sante) {
		this.sante = sante;
	}
	public int getPv() { return pv; }

	public void setPv(int pv) {
	    this.pv = Math.max(0, Math.min(pv, pvMax)); 
	}

	public int getPvMax() { return pvMax; }

	public void setPvMax(int pvMax) {
	     this.pvMax = pvMax;
	     if (pv > pvMax) {
	        pv = pvMax;
	      }
	}
	
	public int getForce() {
		return force;
	}
	public void setForce(int force) {
        if (force >= 0) {
            this.force = force;
        }
    }
	public int getNiveau() {
        return niveau;
    }
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
    public String getType() {
		return type;
	}
    public String getType(String type) { return type; }
    public int getXp() { return xp; }
    public int getXpPourNiveauSuivant() { return xpPourNiveauSuivant; }
    

	private static final String[] SANTE_POSSIBLES = {
	        "en plein forme","en plein forme","en plein forme","en plein forme","en plein forme","en plein forme",
	        "fatigué", "fatigué","fatigué",
	        "épuisé", "épuisé",
	        "sur la fin"
	    };
	protected static String santeAleatoire() {
	       Random rand = new Random();
	       return SANTE_POSSIBLES[rand.nextInt(SANTE_POSSIBLES.length)];
	   }
	private int getPvParSante(String sante) {
		switch (sante) {
	        case "plein forme": return (this instanceof Tank) ? 50 : (this instanceof Support) ? 40 : 30;
	        case "fatigué": return (this instanceof Tank) ? 45 : (this instanceof Support) ? 35 : 25;
	        case "épuisé": return (this instanceof Tank) ? 35 : (this instanceof Support) ? 25 : 20;
	        case "sur la fin": return (this instanceof Tank) ? 25 : (this instanceof Support) ? 20 : 15;
	        default: return 20;
		}
    }
	private int getForceParSante(String sante) {
        switch (sante) {
            case "plein forme": return (this instanceof Tank || this instanceof Support) ? 10 : 20;
            case "fatigué":  return (this instanceof Tank || this instanceof Support) ? 8 : 17;
            case "épuisé":  return (this instanceof Tank || this instanceof Support) ? 6 : 14;
            case "sur la fin":  return (this instanceof Tank || this instanceof Support) ? 2 : 10;
            default: return 20;
        }
    }
	
	public void gagnerNiveau() {
	    this.niveau++;
	    System.out.println(nom + " passe au niveau " + niveau + " !");
	}
	public void gagnerXp(int quantite) {
	    xp += quantite;
	    System.out.println(nom + " gagne " + quantite + " XP ! (Total : " + xp + "/" + xpPourNiveauSuivant + ")");
	    afficherBarreXp(); 

	    while (xp >= xpPourNiveauSuivant) {
	        xp -= xpPourNiveauSuivant;
	        niveau++;
	        xpPourNiveauSuivant *= 1.5;
	        System.out.println(nom + " monte au niveau " + niveau + " !");
	        augmenterStats();
	        afficherBarreXp(); 
	    }
	}

	protected void augmenterStats() {}
	
	protected void afficherBarreXp() {
	    int tailleBarre = 20; 
	    double ratio = (double) xp / xpPourNiveauSuivant;
	    int nbBlocs = (int) (ratio * tailleBarre);

	    StringBuilder barre = new StringBuilder("[");
	    for (int i = 0; i < tailleBarre; i++) {
	        if (i < nbBlocs) {
	            barre.append("█"); 
	        } else {
	            barre.append("░");
	        }
	    }
	    barre.append("]");

	    System.out.println("XP : " + xp + "/" + xpPourNiveauSuivant + " " + barre);
	}
	private static String BarreXp(Personnage p) {
	    int tailleBarre = 20;
	    double ratio = (double) p.getXp() / p.getXpPourNiveauSuivant();
	    int nbBlocs = (int) (ratio * tailleBarre);

	    StringBuilder barre = new StringBuilder();
	    barre.append("XP : ")
	         .append(p.getXp())
	         .append("/")
	         .append(p.getXpPourNiveauSuivant())
	         .append(" [");
	    for (int i = 0; i < tailleBarre; i++) {
	        barre.append(i < nbBlocs ? "█" : "░");
	    }
	    barre.append("]");
	    return barre.toString();
	}

	public static void afficherEquipe(List<Personnage> liste) {
	    if (liste.isEmpty()) {
	        System.out.println("Aucun personnage à afficher.");
	        return;
	    }

	    // En-têtes : noms
	    for (Personnage p : liste) {
	        System.out.printf("%-25s", p.getNom());
	    }
	    System.out.println();

	    // Santé
	    for (Personnage p : liste) {
	        System.out.printf("Etat de sante : %-10s", p.getSante());
	    }
	    System.out.println();

	    // PV
	    for (Personnage p : liste) {
	        System.out.printf("Pv            : %-10d", p.getPv());
	    }
	    System.out.println();

	    // PV Max
	    for (Personnage p : liste) {
	        System.out.printf("Pv max        : %-10d", p.getPvMax());
	    }
	    System.out.println();

	    // Force
	    for (Personnage p : liste) {
	        System.out.printf("Force         : %-10d", p.getForce());
	    }
	    System.out.println();

	    // Niveau
	    for (Personnage p : liste) {
	        System.out.printf("Niveau        : %-10d", p.getNiveau());
	    }
	    System.out.println();
	 // XP bar
	    for (Personnage p : liste) {
	        System.out.printf("%-30s", BarreXp(p));
	    }
	    System.out.println();
	}

	public abstract void attaque1(Monstre cible);
	
	public abstract void subirDegats(int degats);
	
	public boolean estVivant() {
        return pv > 0;
    }
	protected Inventaire inventaire = new Inventaire();
    public Inventaire getInventaire() {
        return inventaire;
	}
}


