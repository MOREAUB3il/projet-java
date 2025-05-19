package jeu; 

import personnage.Personnage;
import inventaire.Inventaire;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;


import personnage.Assassin;
import personnage.Barbare;
import personnage.Chevalier;
import personnage.Enchanteur;
import personnage.Necromancien;
import personnage.Paladin;
import personnage.Pretre;
import personnage.Pyromancien;


public class Equipe {
	public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";
    private List<Personnage> membres;
    private Inventaire inventaireCommun;
    public static final int TAILLE_MAX_EQUIPE = 4;

    public Equipe() {
        this.membres = new ArrayList<>();
        this.inventaireCommun = new Inventaire(); 
    }

    public List<Personnage> getMembres() {
        return membres;
    }

    public Inventaire getInventaireCommun() {
        return inventaireCommun;
    }

    public boolean peutAjouterMembre() {
        return membres.size() < TAILLE_MAX_EQUIPE;
    }

    public boolean classeEstPresente(String typeClasse) {
        for (Personnage p : membres) {
            if (p.getClass().getSimpleName().equalsIgnoreCase(typeClasse)) {
                return true;
            }
        }
        return false;
    }

    public void ajouterMembre(Personnage personnage) {
        if (peutAjouterMembre() && !classeEstPresente(personnage.getClass().getSimpleName())) {
            this.membres.add(personnage);
            System.out.println(personnage.getNom() + " a rejoint l'équipe !");
        } else if (!peutAjouterMembre()) {
            System.out.println("L'équipe est déjà complète (" + TAILLE_MAX_EQUIPE + " personnages).");
        } else {
            System.out.println("Un personnage de la classe " + personnage.getClass().getSimpleName() + " est déjà dans l'équipe.");
        }
    }

    public void choisirEtAjouterPersonnageInitial(Scanner scanner) {
        if (!peutAjouterMembre()) {
            System.out.println("L'équipe est déjà au complet pour le personnage initial.");
            return;
        }
        System.out.println("Choisissez votre premier personnage :");
        Personnage nouveauMembre = selectionnerClassePersonnage(scanner);
        if (nouveauMembre != null) {
            ajouterMembre(nouveauMembre);
        }
    }

    public void proposerChoixNouveauPersonnage(Scanner scanner, int etage) {

        boolean proposerChoix = false;
        if (membres.size() == 1 && etage > 1) { 
            proposerChoix = true;
        } else if (membres.size() == 2 && etage > 2) { 
             proposerChoix = true;
        } else if (membres.size() == 3 && etage > 3) { 
             proposerChoix = true;
        }


        if (peutAjouterMembre() && proposerChoix) {
            System.out.println("\nFélicitations pour avoir atteint ce palier ! Vous pouvez ajouter un nouveau membre à votre équipe.");
            System.out.println("Choisissez un nouveau personnage pour rejoindre votre équipe :");
            Personnage nouveauMembre = selectionnerClassePersonnage(scanner);
            if (nouveauMembre != null) {
                ajouterMembre(nouveauMembre);
            }
        }
    }


    private Personnage selectionnerClassePersonnage(Scanner scanner) {
        String choixClasse;
        Personnage personnageChoisi = null;

        while (personnageChoisi == null) {
            System.out.println("Classes disponibles (un seul de chaque type par équipe) :");
            System.out.println("  1. Assassin " + (classeEstPresente("Assassin") ? "(Déjà pris)" : ""));
            System.out.println("  2. Barbare " + (classeEstPresente("Barbare") ? "(Déjà pris)" : ""));
            System.out.println("  3. Chevalier " + (classeEstPresente("Chevalier") ? "(Déjà pris)" : ""));
            System.out.println("  4. Enchanteur " + (classeEstPresente("Enchanteur") ? "(Déjà pris)" : ""));
            System.out.println("  5. Nécromancien " + (classeEstPresente("Necromancien") ? "(Déjà pris)" : ""));
            System.out.println("  6. Paladin " + (classeEstPresente("Paladin") ? "(Déjà pris)" : ""));
            System.out.println("  7. Prêtre " + (classeEstPresente("Pretre") ? "(Déjà pris)" : ""));
            System.out.println("  8. Pyromancien " + (classeEstPresente("Pyromancien") ? "(Déjà pris)" : ""));
            System.out.print("Votre choix (numéro) : ");
            choixClasse = scanner.nextLine();

            Personnage tempPerso = null;
            String nomClasseChoisie = "";

            switch (choixClasse) {
                case "1": tempPerso = new Assassin(); nomClasseChoisie = "Assassin"; break;
                case "2": tempPerso = new Barbare(); nomClasseChoisie = "Barbare"; break;
                case "3": tempPerso = new Chevalier(); nomClasseChoisie = "Chevalier"; break;
                case "4": tempPerso = new Enchanteur(); nomClasseChoisie = "Enchanteur"; break;
                case "5": tempPerso = new Necromancien(); nomClasseChoisie = "Necromancien"; break;
                case "6": tempPerso = new Paladin(); nomClasseChoisie = "Paladin"; break;
                case "7": tempPerso = new Pretre(); nomClasseChoisie = "Pretre"; break;
                case "8": tempPerso = new Pyromancien(); nomClasseChoisie = "Pyromancien"; break;
                default:
                    System.out.println("Choix de classe invalide. Veuillez réessayer.");
                    continue; 
            }

            if (tempPerso != null) {
                if (!classeEstPresente(nomClasseChoisie)) {
                    personnageChoisi = tempPerso;
                } else {
                    System.out.println("La classe " + nomClasseChoisie + " est déjà présente dans votre équipe. Choisissez une autre classe.");
                }
            }
        }
        return personnageChoisi;
    }
    public boolean changerDePlace(Scanner scanner, Personnage pQuiVeutBouger) {
        if (membres.size() < 2) {
            System.out.println(ANSI_YELLOW + "Pas assez de membres dans l'équipe pour changer de place." + ANSI_RESET);
            return false;
        }

        List<Personnage> coequipiersPossibles = new ArrayList<>(membres);
        coequipiersPossibles.remove(pQuiVeutBouger); 

        if (coequipiersPossibles.isEmpty()) {
            
            System.out.println(ANSI_RED + "Erreur : Aucun coéquipier avec qui échanger." + ANSI_RESET);
            return false;
        }

        System.out.println(ANSI_CYAN + "Avec quel coéquipier " + ANSI_YELLOW + pQuiVeutBouger.getNom() + ANSI_CYAN + " doit-il échanger sa place ?" + ANSI_RESET);
        for (int i = 0; i < coequipiersPossibles.size(); i++) {
            Personnage coequipier = coequipiersPossibles.get(i);
            System.out.println("  " + (i + 1) + ". " + coequipier.getNom() + " (Position actuelle: " + (membres.indexOf(coequipier) + 1) + ")");
        }
        System.out.print("Votre choix (numéro) ou 0 pour annuler : ");

        int choixCoequipierInput;
        if (scanner.hasNextInt()) {
            choixCoequipierInput = scanner.nextInt();
        } else {
            scanner.nextLine();
            System.out.println(ANSI_RED + "Entrée invalide." + ANSI_RESET);
            return false;
        }
        scanner.nextLine(); 

        if (choixCoequipierInput == 0) {
            System.out.println(ANSI_YELLOW + "Changement de place annulé." + ANSI_RESET);
            return false;
        }

        int choixCoequipierIndexDansListeOptions = choixCoequipierInput - 1;

        if (choixCoequipierIndexDansListeOptions < 0 || choixCoequipierIndexDansListeOptions >= coequipiersPossibles.size()) {
            System.out.println(ANSI_RED + "Choix de coéquipier invalide." + ANSI_RESET);
            return false;
        }

        Personnage coequipierChoisi = coequipiersPossibles.get(choixCoequipierIndexDansListeOptions);

        
        int indexPQuiVeutBouger = membres.indexOf(pQuiVeutBouger);
        int indexCoequipierChoisi = membres.indexOf(coequipierChoisi);

        if (indexPQuiVeutBouger == -1 || indexCoequipierChoisi == -1) {
            System.out.println(ANSI_RED + "Erreur interne : un des personnages n'a pas été trouvé dans l'équipe." + ANSI_RESET);
            return false; 
        }

        
        Collections.swap(membres, indexPQuiVeutBouger, indexCoequipierChoisi);

        System.out.println(ANSI_GREEN + pQuiVeutBouger.getNom() + " a échangé sa place avec " + coequipierChoisi.getNom() + "." + ANSI_RESET);
        System.out.println("Nouvel ordre de l'équipe :");
        

        return true; 
    }

    public void afficherStatEquipe() {
        if (membres.isEmpty()) {
            System.out.println("L'équipe est vide.");
            return;
        }
        System.out.println("\n=== VOTRE ÉQUIPE ===");
        Personnage.afficherEquipeP(membres); 
        System.out.println("====================");
    }

    public Personnage getLeader() {
        if (membres.isEmpty()) {
            return null;
        }
        return membres.get(0); 
    }

    public void deplacerMembreEnFin(Personnage membre) {
        if (membres.contains(membre)) {
            membres.remove(membre);
            membres.add(membre);
        }
    }

    public void deplacerMembreAuDebut(Personnage membre) {
        if (membres.contains(membre)) {
            membres.remove(membre);
            membres.add(0, membre);
        }
    }
     public void rotationApresAction(Personnage joueurActif) {
        if (membres.contains(joueurActif)) {
            membres.remove(joueurActif);
            membres.add(joueurActif); 
        }
    }
}