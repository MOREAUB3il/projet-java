import java.util.Random;

public abstract class Personnage {
    private String nom;
    private String sante;
    private int pv;
    private int pvMax;
    private int force;
    protected int niveau;
    protected int xp;
    protected int xpPourNiveauSuivant;

    public Personnage(String nom) {
        this.nom = nom;
        this.sante = santeAleatoire();
        this.pvMax = getPvParSante(sante);
        this.force = getForceParSante(sante);
        this.pvMax = pv;
        this.niveau = 1;
        this.xp = 0;
        this.xpPourNiveauSuivant = 100;
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
	        case "plein forme": return (this instanceof Chevalier) ? 50 : (this instanceof Pretre) ? 40 : 30;
	        case "fatigué": return (this instanceof Chevalier) ? 45 : (this instanceof Pretre) ? 35 : 25;
	        case "épuisé": return (this instanceof Chevalier) ? 35 : (this instanceof Pretre) ? 25 : 20;
	        case "sur la fin": return (this instanceof Chevalier) ? 25 : (this instanceof Pretre) ? 20 : 15;
	        default: return 20;
		}
    }
	private int getForceParSante(String sante) {
        switch (sante) {
            case "plein forme": return (this instanceof Chevalier || this instanceof Pretre) ? 10 : 20;
            case "fatigué":  return (this instanceof Chevalier || this instanceof Pretre) ? 8 : 17;
            case "épuisé":  return (this instanceof Chevalier || this instanceof Pretre) ? 6 : 14;
            case "sur la fin":  return (this instanceof Chevalier || this instanceof Pretre) ? 2 : 10;
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

	public abstract void attaque1(Monstre cible);
	
	public abstract void subirDegats(int degats);
	
	protected ArrayList<Objet> inventaire = new ArrayList<Objet>();
	
	protected int or = 50;
	
	public int getOr() {
	    return or;
	}

	public void setOr(int or) {
		if(or>=0) {
			this.or = or;
		}
	}

	public void ajouterOr(int montant) {
	    or += montant;
	}

	public boolean retirerOr(int montant) {
	    if (or >= montant) {
	        or -= montant;
	        return true;
	    }
	    return false;
	}

	}


