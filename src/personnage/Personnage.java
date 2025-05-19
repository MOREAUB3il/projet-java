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
    protected int compLvl;
    protected int ultLvl;


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
        this.compLvl = 1; 
        this.ultLvl = 0; 
    }
    public void afficherStat() {
    	System.out.println("------------------------------");
    	System.out.println(getNom());
    	System.out.println("Classe : " + this.getClass().getSimpleName() + " (" + getType() + ")");
    	System.out.println("Etat de sante :"+getSante());
    	System.out.println("Pv :"+getPv() + "/" + getPvMax());
    	System.out.println("Force :"+getForce());
        if (this instanceof Tank) System.out.println("Défense : " + ((Tank)this).getDefense());
        if (this instanceof Dps) System.out.println("Pénétration : " + ((Dps)this).getPenetration());
        if (this instanceof Mage) System.out.println("Malédiction : " + ((Mage)this).getMalediction());
        if (this instanceof Support) System.out.println("Soin : " + ((Support)this).getSoin());
    	System.out.println("Niveau :"+getNiveau());
        System.out.println("Compétence Niv.: " + getCompLvl());
        System.out.println("Ultime Niv.: " + (getUltLvl() > 0 ? getUltLvl() : "Non débloqué"));
    	afficherBarreXp();
        System.out.println("------------------------------");
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
    public int getCompLvl() {return compLvl;}
    public int getUltLvl() {return ultLvl;}

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
	        xpPourNiveauSuivant = (int) (xpPourNiveauSuivant * 1.25) + 50; 
	        System.out.println(nom + " monte au niveau " + niveau + " !");
	        augmenterStats();
	        verifierAmeliorationsCompetences(); 
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

	public static void afficherEquipeP(List<Personnage> liste) {
	    if (liste.isEmpty()) {
	        System.out.println("Aucun personnage à afficher.");
	        return;
	    }
	    for (Personnage p : liste) {
	        System.out.printf("%-25s", p.getNom());
	    }
	    System.out.println();
	    for (Personnage p : liste) {
	        System.out.printf("Etat de sante : %-10s", p.getSante());
	    }
	    System.out.println();
	    for (Personnage p : liste) {
	        System.out.printf("Pv            : %-10d", p.getPv());
	    }
	    System.out.println();
	    for (Personnage p : liste) {
	        System.out.printf("Pv max        : %-10d", p.getPvMax());
	    }
	    System.out.println();
	    for (Personnage p : liste) {
	        System.out.printf("Force         : %-10d", p.getForce());
	    }
	    System.out.println();
	    if (liste.get(0) instanceof Tank) {
	        for (Personnage p : liste) {
	            System.out.printf("Défense       : %-10d", ((Tank)p).getDefense());
	        }
	        System.out.println();
	    }
	    if (liste.get(0) instanceof Dps) {
	        for (Personnage p : liste) {
	            System.out.printf("Pénétration   : %-10d", ((Dps)p).getPenetration());
	        }
	        System.out.println();
	    }
	    if (liste.get(0) instanceof Mage) {
	        for (Personnage p : liste) {
	            System.out.printf("Malédiction   : %-10d", ((Mage)p).getMalediction());
	        }
	        System.out.println();
	    }
	    if (liste.get(0) instanceof Support) {
	        for (Personnage p : liste) {
	            System.out.printf("Soin          : %-10d", ((Support)p).getSoin());
	        }
	        System.out.println();
	    }
	    for (Personnage p : liste) {
	        System.out.printf("Niveau        : %-10d", p.getNiveau());
	    }
	    System.out.println();
	    for (Personnage p : liste) {
	        System.out.printf("%-30s", BarreXp(p));
	    }
	    System.out.println();
	}

	public abstract void attaque1(Monstre cible);

	public abstract void ulti(Monstre cible);
	
	 private void verifierAmeliorationsCompetences() {

	        if (this.niveau == 5 && this.compLvl < 2) {
	            this.compLvl = 2;
	            System.out.println("-> " + this.nom + " : Compétence améliorée (Niv. " + this.compLvl + ") !");
	        }
	        if (this.niveau == 10 && this.ultLvl == 0) {
	            this.ultLvl = 1;
	            System.out.println("-> " + this.nom + " : ULTIME DÉBLOQUÉ !");
	        }
	        if (this.niveau == 15 && this.compLvl < 3) {
	            this.compLvl = 3;
	            System.out.println("-> " + this.nom + " : Compétence améliorée (Niv. " + this.compLvl + ") !");
	        }
	        if (this.niveau == 20 && this.ultLvl < 2) {
	            this.ultLvl = 2;
	            System.out.println("-> " + this.nom + " : Ultime amélioré (Niv. " + this.ultLvl + ") !");
	        }
	        if (this.niveau == 30 && this.compLvl < 4) { 
	            this.compLvl = 4;
	            System.out.println("-> " + this.nom + " : Compétence améliorée (Niv. " + this.compLvl + ") !");
	        }
	        if (this.niveau == 40 && this.ultLvl < 3) {
	            this.ultLvl = 3;
	            System.out.println("-> " + this.nom + " : Ultime amélioré (Niv. " + this.ultLvl + ") !");
	        }
	        if (this.niveau == 50 && this.ultLvl < 4) {
	            this.ultLvl = 4;
	            System.out.println("-> " + this.nom + " : Ultime amélioré (Niv. " + this.ultLvl + ") !");
	        }
	    }
	 
	public abstract void subirDegats(int degats);
	
	public boolean estVivant() {
        return pv > 0;
    }
	protected Inventaire inventaire = new Inventaire();
    public Inventaire getInventaire() {
        return inventaire;
	}
}


