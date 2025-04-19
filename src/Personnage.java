import java.util.Random;

public abstract class Personnage {
    private String nom;
    private String estMalade;
    protected int niveau;
    protected int xp;
    protected int xpPourNiveauSuivant;

    public Personnage(String nom) {
        this.nom = nom;
        setEstMalade(santeAleatoire());
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
	public String getEstMalade() {
		return estMalade;
	}

	public void setEstMalade(String estMalade) {
		this.estMalade = estMalade;
	}
	public int getNiveau() {
        return niveau;
    }
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

	private static final String[] SANTE_POSSIBLES = {
	        "plein forme", "fatigué", "épuisé", "sur la fin"
	    };
	private static String santeAleatoire() {
	       Random rand = new Random();
	       return SANTE_POSSIBLES[rand.nextInt(SANTE_POSSIBLES.length)];
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

	}

/* je travaille dessus
    public void seSoigner() {
        if (!estMalade) {
            System.out.println(nom + " n'a pas besoin d'être soigné.");
        } else {
            pv += 10;
            estMalade = false;
            System.out.println(nom + " est maintenant en pleine forme après avoir été soigné.");
        }
    }

    public void prendreDegats(int degats) {
        pv -= degats;
        if (pv <= 0) {
            pv = 0;
            System.out.println(nom + " a été vaincu.");
        } else {
            if (Math.random() < 0.2) {
                estMalade = true;
                System.out.println(nom + " est tombé malade après avoir subi des dégâts.");
            }
        }
    }

    public boolean isVivant() {
        return pv > 0;
    }

*/  

