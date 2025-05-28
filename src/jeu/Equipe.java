package jeu;

import personnage.Personnage;
import inventaire.Inventaire;
import personnage.Assassin;
import personnage.Barbare;
import personnage.Chevalier;
import personnage.Enchanteur;
import personnage.Necromancien;
import personnage.Paladin;
import personnage.Pretre;
import personnage.Pyromancien;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class Equipe {
    private List<Personnage> membres;
    private Inventaire inventaireCommun; 
    public static final int TAILLE_MAX_EQUIPE = 4;
    private int dernierEtagePropositionRecrutement = 0; 

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

    public boolean classeEstPresente(String nomClasseSimple) {
        for (Personnage p : membres) {
            if (p.getClass().getSimpleName().equalsIgnoreCase(nomClasseSimple)) {
                return true;
            }
        }
        return false;
    }

    public void ajouterMembre(Personnage personnage) {
        if (personnage == null) {
            System.out.println(Main.ANSI_RED + "Tentative d'ajouter un personnage null à l'équipe." + Main.ANSI_RESET);
            return;
        }
        if (peutAjouterMembre()) {
            if (!classeEstPresente(personnage.getClass().getSimpleName())) {
                this.membres.add(personnage);
                System.out.println(Main.ANSI_GREEN + personnage.getNom() + " a rejoint l'équipe !" + Main.ANSI_RESET);
            } else {
                System.out.println(Main.ANSI_YELLOW + "Un personnage de la classe " + personnage.getClass().getSimpleName() + " est déjà dans l'équipe. Impossible d'ajouter." + Main.ANSI_RESET);
            }
        } else {
            System.out.println(Main.ANSI_YELLOW + "L'équipe est déjà complète (" + TAILLE_MAX_EQUIPE + " personnages)." + Main.ANSI_RESET);
        }
    }

    public void choisirEtAjouterPersonnageInitial(Scanner scanner, Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu, int idSauvegardeActivePourMenu) {
        if (!peutAjouterMembre()) {
            System.out.println(Main.ANSI_YELLOW + "L'équipe est déjà au complet pour le personnage initial." + Main.ANSI_RESET);
            return;
        }
        System.out.println(Main.ANSI_BOLD + Main.ANSI_CYAN + "=== Choisissez votre premier personnage ===" + Main.ANSI_RESET);
        Personnage nouveauMembre = selectionnerClassePersonnage(scanner, equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
        
        if (!Main.continuerJeuGlobal) return; 

        if (nouveauMembre != null) {
            ajouterMembre(nouveauMembre);
            this.dernierEtagePropositionRecrutement = etagePourMenu; 
        } else {
            System.out.println(Main.ANSI_RED + "Aucun personnage initial sélectionné." + Main.ANSI_RESET);
        }
    }

    public void proposerChoixNouveauPersonnage(Scanner scanner, Equipe equipePourMenu, int etageActuelApresVictoire, String nomJoueurPourMenu, int idSauvegardeActivePourMenu) {
        if (!peutAjouterMembre()) {
            return;
        }

        boolean proposerMaintenant = false;
        String raisonProposition = "";

        if (getMembres().size() < TAILLE_MAX_EQUIPE) {
            if (getMembres().size() == 1 && etageActuelApresVictoire == 2 && dernierEtagePropositionRecrutement < 2) {
                proposerMaintenant = true; raisonProposition = "Vous avez bien commencé ! Il est temps d'agrandir votre équipe.";
            } else if (getMembres().size() == 2 && etageActuelApresVictoire == 3 && dernierEtagePropositionRecrutement < 3) {
                proposerMaintenant = true; raisonProposition = "Votre renommée grandit. Un autre aventurier doit se joindre à vous.";
            } else if (getMembres().size() == 3 && etageActuelApresVictoire == 4 && dernierEtagePropositionRecrutement < 4) {
                proposerMaintenant = true; raisonProposition = "Votre équipe est presque au complet. Un dernier allié doit se présenter.";
            }
        }

        if (!proposerMaintenant && getMembres().size() < TAILLE_MAX_EQUIPE && etageActuelApresVictoire > 4) {
            int etageFiniPrecedent = etageActuelApresVictoire - 1;
            if ( (etageFiniPrecedent / 20) > (dernierEtagePropositionRecrutement / 20) ||
                 (dernierEtagePropositionRecrutement <= 4 && etageFiniPrecedent >= 20) ) {
                proposerMaintenant = true;
                raisonProposition = "Après de nombreux défis, vous devez recruter un nouveau membre pour combler un vide ou renforcer vos rangs.";
            }
        }

        if (proposerMaintenant) {
            System.out.println(Main.ANSI_BOLD + Main.ANSI_GREEN + "\n" + raisonProposition + Main.ANSI_RESET);
            
            Personnage nouveauMembre = null;
            while (nouveauMembre == null && Main.continuerJeuGlobal) {
                nouveauMembre = selectionnerClassePersonnage(scanner, equipePourMenu, etageActuelApresVictoire, nomJoueurPourMenu, idSauvegardeActivePourMenu); // false pour forcer choix
                if (nouveauMembre == null && Main.continuerJeuGlobal) {
                     System.out.println(Main.ANSI_RED + "Vous devez sélectionner un nouveau membre pour continuer." + Main.ANSI_RESET);
                }
            }

            if (!Main.continuerJeuGlobal) return; 

            if (nouveauMembre != null) {
                ajouterMembre(nouveauMembre);
            }
            this.dernierEtagePropositionRecrutement = etageActuelApresVictoire;
        }
    }

    private Personnage selectionnerClassePersonnage(Scanner scanner, Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu, int idSauvegardeActivePourMenu) {
        String choixClasseStr;
        Personnage personnageChoisi = null;

        while (personnageChoisi == null && Main.continuerJeuGlobal) {
            System.out.println(Main.ANSI_CYAN + "Classes disponibles (un seul de chaque type par équipe) :" + Main.ANSI_RESET);
            System.out.println("  1. Assassin 🗡️ " + (classeEstPresente("Assassin") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  2. Barbare 🪓" + (classeEstPresente("Barbare") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  3. Chevalier ⚔️" + (classeEstPresente("Chevalier") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  4. Enchanteur 📜" + (classeEstPresente("Enchanteur") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  5. Nécromancien ☠️" + (classeEstPresente("Necromancien") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  6. Paladin 🔨" + (classeEstPresente("Paladin") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  7. Prêtre 🪬" + (classeEstPresente("Pretre") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));
            System.out.println("  8. Pyromancien 🔥" + (classeEstPresente("Pyromancien") ? Main.ANSI_RED + "(Pris)" + Main.ANSI_RESET : ""));

            choixClasseStr = Main.lireStringAvecMenuPause(scanner, "Votre choix (numéro, 'M' menu) : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);

            if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(choixClasseStr)) return null;
            if ("0".equals(choixClasseStr)) { System.out.println("Recrutement annulé."); return null; }

            Personnage tempPerso = null; String nomClasseChoisieSimple = "";
            switch (choixClasseStr) {
                case "1": tempPerso = new Assassin(); nomClasseChoisieSimple = "Assassin"; break;
                case "2": tempPerso = new Barbare(); nomClasseChoisieSimple = "Barbare"; break;
                case "3": tempPerso = new Chevalier(); nomClasseChoisieSimple = "Chevalier"; break;
                case "4": tempPerso = new Enchanteur(); nomClasseChoisieSimple = "Enchanteur"; break;
                case "5": tempPerso = new Necromancien(); nomClasseChoisieSimple = "Necromancien"; break;
                case "6": tempPerso = new Paladin(); nomClasseChoisieSimple = "Paladin"; break;
                case "7": tempPerso = new Pretre(); nomClasseChoisieSimple = "Pretre"; break;
                case "8": tempPerso = new Pyromancien(); nomClasseChoisieSimple = "Pyromancien"; break;
                default: System.out.println(Main.ANSI_RED + "Choix de classe invalide." + Main.ANSI_RESET); continue;
            }
            if (tempPerso != null) {
                if (!classeEstPresente(nomClasseChoisieSimple)) personnageChoisi = tempPerso;
                else System.out.println(Main.ANSI_RED + "Classe " + nomClasseChoisieSimple + " déjà présente." + Main.ANSI_RESET);
            }
        }
        return personnageChoisi;
    }
    
    public boolean changerDePlace(Scanner scanner, Personnage pQuiVeutBouger, Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu, int idSauvegardeActivePourMenu) {
        if (membres.size() < 2) {
            System.out.println(Main.ANSI_YELLOW + "Pas assez de membres pour changer de place." + Main.ANSI_RESET);
            return false;
        }
        List<Personnage> coequipiersPossibles = new ArrayList<>(membres);
        coequipiersPossibles.remove(pQuiVeutBouger);
        if (coequipiersPossibles.isEmpty()) { System.out.println(Main.ANSI_RED + "Erreur : Aucun coéquipier." + Main.ANSI_RESET); return false; }

        System.out.println(Main.ANSI_CYAN + "Avec qui " + Main.ANSI_YELLOW + pQuiVeutBouger.getNom() + Main.ANSI_CYAN + " doit-il échanger ?" + Main.ANSI_RESET);
        for (int i = 0; i < coequipiersPossibles.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + coequipiersPossibles.get(i).getNom());
        }
        
        int choixCoequipierInput = Main.lireIntAvecMenuPause(scanner, "Choix (numéro, 0 annuler, 'M' menu) : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);

        if (!Main.continuerJeuGlobal || choixCoequipierInput == -999) return false; 
        if (choixCoequipierInput == 0) { System.out.println(Main.ANSI_YELLOW + "Changement annulé." + Main.ANSI_RESET); return false; }

        int choixIndex = choixCoequipierInput - 1;
        if (choixIndex < 0 || choixIndex >= coequipiersPossibles.size()) { System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET); return false; }

        Personnage coequipierChoisi = coequipiersPossibles.get(choixIndex);
        int indexPActif = membres.indexOf(pQuiVeutBouger);
        int indexCoe = membres.indexOf(coequipierChoisi);

        if (indexPActif != -1 && indexCoe != -1) {
            Collections.swap(membres, indexPActif, indexCoe);
            System.out.println(Main.ANSI_GREEN + pQuiVeutBouger.getNom() + " a échangé sa place avec " + coequipierChoisi.getNom() + "." + Main.ANSI_RESET);
            return true;
        }
        System.out.println(Main.ANSI_RED + "Erreur interne lors du changement de place." + Main.ANSI_RESET);
        return false;
    }
    
    public List<Personnage> getMembresVivants() {
        List<Personnage> vivants = new ArrayList<>();
        if (this.membres != null) for (Personnage p : this.membres) if (p != null && p.estVivant()) vivants.add(p);
        return vivants;
    }

    public int getMembresVivantsCount() {
        int count = 0;
        if (this.membres != null) for (Personnage p : this.membres) if (p != null && p.estVivant()) count++;
        return count;
    }

    public void afficherStatEquipe() {
        if (membres == null || membres.isEmpty()) { System.out.println("L'équipe est vide."); return; }
        System.out.println(Main.ANSI_BOLD + Main.ANSI_GREEN + "\n=== VOTRE ÉQUIPE ===" + Main.ANSI_RESET);
        Personnage.afficherEquipeP(membres);
    }

    public Personnage getLeader() {
        return (membres == null || membres.isEmpty()) ? null : membres.get(0);
    }

    public void deplacerMembreEnFin(Personnage membre) {
        if (membres != null && membres.contains(membre)) { membres.remove(membre); membres.add(membre); }
    }

    public void deplacerMembreAuDebut(Personnage membre) {
        if (membres != null && membres.contains(membre)) { membres.remove(membre); membres.add(0, membre); }
    }

    public void rotationApresAction(Personnage joueurActif) {
        if (membres != null && membres.contains(joueurActif)) {
            membres.remove(joueurActif);
            membres.add(joueurActif);
        }
    }
}