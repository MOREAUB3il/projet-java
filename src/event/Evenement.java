package event; 

import jeu.Equipe; 
import java.util.Scanner;
import jeu.Main; 

public abstract class Evenement {
    protected String nom;
    protected String description;

    public Evenement(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public abstract void declencher(Equipe equipe, Scanner scanner, int etage, String nomJoueur);


    protected void afficherDebutEvenement() {
        System.out.println(Main.ANSI_BOLD + Main.ANSI_CYAN + "\n--- ÉVÉNEMENT : " + nom + " ---" + Main.ANSI_RESET);
        System.out.println(description);
    }
}