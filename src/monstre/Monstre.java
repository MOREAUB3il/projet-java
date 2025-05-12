package monstre;

import java.util.Random;
import personnage.Personnage;
import inventaire.Inventaire;


public class Monstre {
    private String nom;
    private int pv;
    private int force;
    private int defence;
    private double maladieChance;
    private int orM;
    
    public Monstre(String type, int pv, int force, int defence, double maladieChance, int or) {
        this.nom = type;
        this.pv = pv;
        this.force = force;
        this.defence =defence;
        this.maladieChance = maladieChance;
        this.orM = or;
    }
    public static Monstre monstreFaible() {
        return new Monstre("Monstre Faible", 25, 5, 15, 1.0 / 50, 10);
    }

    public static Monstre monstreMoyen() {
        return new Monstre("Monstre Moyen", 50, 15, 25, 1.0 / 30, 20);
    }

    public static Monstre monstreFort() {
        return new Monstre("Monstre Fort", 100, 30, 45, 1.0 / 20, 30);
    }

    public static Monstre boss(int etage) {
        int pv = etage * 10 + 100;
        int force = etage + 30;
        return new Monstre("BOSS Étage " + etage, pv, force, 150, 1.0 / 20, 40 + etage);
    }
    
    public void attaquer(Personnage cible) {
        System.out.println(nom + " attaque " + cible.getNom());
        cible.subirDegats(force);
        if (new Random().nextDouble() < maladieChance) {
            System.out.println(cible.getNom() + " est tombé malade !");
        }
    }

    public void subirDegats(int degats) {
        this.pv -= degats - defence;
        if (pv < 0) pv = 0;
        System.out.println(nom + " a maintenant " + pv + " PV.");
    }
    public void estMort(Inventaire inventaire) {
    	if(pv <= 0) {
    		System.out.println(nom + " es mort " );
			inventaire.ajouterOr(orM);
			System.out.println("Vous avez gagné " + orM + " pièces d'or !");
    	}
    }

    public boolean estVivant() {
        return pv > 0;
    }

    public String getNom() { return nom; }
    public int getDefence() { return defence; }
    public int getPv() { return pv; }
    public int getOrM() {return orM;}
}
