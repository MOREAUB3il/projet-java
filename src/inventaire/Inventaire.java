package inventaire;

import java.util.List;
import java.util.Scanner;

import jeu.Equipe; 
import jeu.Main;
import monstre.Monstre;
import personnage.Personnage;

public class Inventaire {
    private int or;
    private Objet[] objetsSpecifiques;

    private static final String[] NOMS_OBJETS_PAR_SLOT = new String[7];
    static {
        try {
            NOMS_OBJETS_PAR_SLOT[0] = Objet.potionCommune().getNom();
            NOMS_OBJETS_PAR_SLOT[1] = Objet.potionRare().getNom();
            NOMS_OBJETS_PAR_SLOT[2] = Objet.potionLegendaire().getNom();
            NOMS_OBJETS_PAR_SLOT[3] = Objet.collierCommun().getNom();
            NOMS_OBJETS_PAR_SLOT[4] = Objet.collierRare().getNom();
            NOMS_OBJETS_PAR_SLOT[5] = Objet.collierLegendaire().getNom();
            NOMS_OBJETS_PAR_SLOT[6] = Objet.fioleDePoison().getNom();
        } catch (NullPointerException e) {
            System.err.println(Main.ANSI_RED + "Erreur critique: Impossible d'initialiser NOMS_OBJETS_PAR_SLOT. Vérifiez Objet.java et ses méthodes factory." + Main.ANSI_RESET);
            for (int i = 0; i < NOMS_OBJETS_PAR_SLOT.length; i++) {
                if (NOMS_OBJETS_PAR_SLOT[i] == null) NOMS_OBJETS_PAR_SLOT[i] = "Slot " + (i + 1) + " Indéfini";
            }
        }
    }


    public Inventaire() {
        this.or = 10;

        this.objetsSpecifiques = new Objet[NOMS_OBJETS_PAR_SLOT.length];
    }

    public boolean ajouterObjet(Objet objetAAjouter) {
        if (objetAAjouter == null) return false;
        for (int i = 0; i < NOMS_OBJETS_PAR_SLOT.length && i < this.objetsSpecifiques.length; i++) {
            if (NOMS_OBJETS_PAR_SLOT[i].equalsIgnoreCase(objetAAjouter.getNom())) {
                if (objetsSpecifiques[i] == null) {
                    objetsSpecifiques[i] = objetAAjouter;
                    System.out.println(Main.ANSI_GREEN + objetAAjouter.getNom() + " ajouté à l'inventaire (Slot " + (i + 1) + ")." + Main.ANSI_RESET);
                    return true;
                } else {
                    System.out.println(Main.ANSI_YELLOW + "Vous possédez déjà: " + objetsSpecifiques[i].getNom() + " (Slot " + (i + 1) + ")." + Main.ANSI_RESET);
                    return false;
                }
            }
        }
        System.out.println(Main.ANSI_RED + "Slot non désigné pour: " + objetAAjouter.getNom() + Main.ANSI_RESET);
        return false;
    }

    public Objet getObjetDansSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= objetsSpecifiques.length) return null;
        return objetsSpecifiques[slotIndex];
    }

    public void afficherInventaire() {
        System.out.println(Main.ANSI_BLUE + "\n--- Inventaire de l'Équipe ---" + Main.ANSI_RESET);
        System.out.println("Or: " + Main.ANSI_YELLOW + or + Main.ANSI_RESET);
        System.out.println("Objets :");
        boolean auMoinsUnObjet = false;
        for (int i = 0; i < objetsSpecifiques.length; i++) {
            String nomAttenduSlot = NOMS_OBJETS_PAR_SLOT[i];
            System.out.print("  Slot " + (i + 1) + " [" + nomAttenduSlot + "]: ");
            if (objetsSpecifiques[i] != null) {
                System.out.println(Main.ANSI_GREEN + objetsSpecifiques[i].getNom() + Main.ANSI_RESET + " (x1)");
                auMoinsUnObjet = true;
            } else {
                System.out.println(Main.ANSI_YELLOW + "[Vide]" + Main.ANSI_RESET);
            }
        }
        if (!auMoinsUnObjet && objetsSpecifiques.length > 0) {
            System.out.println("  (Aucun objet possédé)");
        }
        System.out.println(Main.ANSI_BLUE + "--------------------------" + Main.ANSI_RESET);
    }

    public boolean utiliserObjetInteractive(Scanner scanner, Personnage utilisateur,
                                            List<Personnage> ciblesEquipe, List<Monstre> ciblesMonstres,
                                            Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu,
                                            int idSauvegardeActivePourMenu) {
        if (estVide()) {
            System.out.println(Main.ANSI_YELLOW + "L'inventaire est vide." + Main.ANSI_RESET);
            return false;
        }

        afficherInventaire();
        int choixSlotInput = Main.lireIntAvecMenuPause(scanner,
                Main.ANSI_CYAN + "Objet à utiliser (slot 1-" + objetsSpecifiques.length + ", 0 annuler, 'M' menu) : " + Main.ANSI_RESET,
                equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);

        if (!Main.continuerJeuGlobal || choixSlotInput == -999) return false;
        if (choixSlotInput == 0) {
            System.out.println(Main.ANSI_YELLOW + "Utilisation d'objet annulée." + Main.ANSI_RESET);
            return false;
        }

        int choixSlotIndex = choixSlotInput - 1;
        if (choixSlotIndex < 0 || choixSlotIndex >= objetsSpecifiques.length || objetsSpecifiques[choixSlotIndex] == null) {
            System.out.println(Main.ANSI_RED + "Slot invalide ou vide." + Main.ANSI_RESET);
            return false;
        }

        Objet objetAUtiliser = objetsSpecifiques[choixSlotIndex];
        Object cibleFinale = null;

        System.out.println("Utilisation de : " + Main.ANSI_GREEN + objetAUtiliser.getNom() + Main.ANSI_RESET);

        switch (objetAUtiliser.getCibleAutorisee()) {
            case "SOI_MEME":
                cibleFinale = utilisateur;
                break;
            case "ALLIE":
                if (ciblesEquipe == null || ciblesEquipe.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun allié." + Main.ANSI_RESET); return false; }
                System.out.println(Main.ANSI_CYAN + "Sur quel allié utiliser " + objetAUtiliser.getNom() + " ('M' menu) ?" + Main.ANSI_RESET);
                for (int i = 0; i < ciblesEquipe.size(); i++) System.out.println("  " + (i + 1) + ". " + ciblesEquipe.get(i).getNom());
                int choixAllie = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
                if (!Main.continuerJeuGlobal || choixAllie == -999) return false;
                if (choixAllie > 0 && choixAllie <= ciblesEquipe.size()) cibleFinale = ciblesEquipe.get(choixAllie - 1);
                else { System.out.println(Main.ANSI_RED + "Choix allié invalide." + Main.ANSI_RESET); return false; }
                break;
            case "ENNEMI":
                if (ciblesMonstres == null || ciblesMonstres.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun ennemi." + Main.ANSI_RESET); return false; }
                System.out.println(Main.ANSI_CYAN + "Sur quel ennemi utiliser " + objetAUtiliser.getNom() + " ('M' menu) ?" + Main.ANSI_RESET);
                for (int i = 0; i < ciblesMonstres.size(); i++) System.out.println("  " + (i + 1) + ". " + ciblesMonstres.get(i).getNom());
                int choixEnnemi = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
                if (!Main.continuerJeuGlobal || choixEnnemi == -999) return false;
                if (choixEnnemi > 0 && choixEnnemi <= ciblesMonstres.size()) cibleFinale = ciblesMonstres.get(choixEnnemi - 1);
                else { System.out.println(Main.ANSI_RED + "Choix ennemi invalide." + Main.ANSI_RESET); return false; }
                break;
            case "ALLIE_OU_ENNEMI":
                String typeCible = Main.lireStringAvecMenuPause(scanner, Main.ANSI_CYAN + "Cibler Allié (A) ou Ennemi (E) ('M' menu) ? " + Main.ANSI_RESET, equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
                if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(typeCible)) return false;
                if (typeCible.equalsIgnoreCase("A")) {
                    if (ciblesEquipe == null || ciblesEquipe.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun allié." + Main.ANSI_RESET); return false; }
                    System.out.println(Main.ANSI_CYAN + "Sur quel allié ('M' menu) ?" + Main.ANSI_RESET);
                    for (int i = 0; i < ciblesEquipe.size(); i++) System.out.println("  " + (i + 1) + ". " + ciblesEquipe.get(i).getNom());
                    int choixAllieAE = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
                    if (!Main.continuerJeuGlobal || choixAllieAE == -999) return false;
                    if (choixAllieAE > 0 && choixAllieAE <= ciblesEquipe.size()) cibleFinale = ciblesEquipe.get(choixAllieAE - 1);
                    else { System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET); return false; }
                } else if (typeCible.equalsIgnoreCase("E")) {
                    if (ciblesMonstres == null || ciblesMonstres.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun ennemi." + Main.ANSI_RESET); return false; }
                    System.out.println(Main.ANSI_CYAN + "Sur quel ennemi ('M' menu) ?" + Main.ANSI_RESET);
                    for (int i = 0; i < ciblesMonstres.size(); i++) System.out.println("  " + (i + 1) + ". " + ciblesMonstres.get(i).getNom());
                    int choixEnnemiAE = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu, idSauvegardeActivePourMenu);
                    if (!Main.continuerJeuGlobal || choixEnnemiAE == -999) return false;
                    if (choixEnnemiAE > 0 && choixEnnemiAE <= ciblesMonstres.size()) cibleFinale = ciblesMonstres.get(choixEnnemiAE - 1);
                    else { System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET); return false; }
                } else { System.out.println(Main.ANSI_RED + "Type (A/E) invalide." + Main.ANSI_RESET); return false; }
                break;
            default:
                System.out.println(Main.ANSI_RED + "Type de cible objet non géré: " + objetAUtiliser.getCibleAutorisee() + Main.ANSI_RESET);
                return false;
        }

        if (cibleFinale != null) {
            objetAUtiliser.utiliser(utilisateur, cibleFinale); 
            objetsSpecifiques[choixSlotIndex] = null; 
            System.out.println(Main.ANSI_GREEN + objetAUtiliser.getNom() + " consommé." + Main.ANSI_RESET);
            return true;
        } else {
            System.out.println(Main.ANSI_RED + "Erreur: Aucune cible finale n'a été déterminée pour l'objet." + Main.ANSI_RESET);
            return false;
        }
    }

    public boolean placerObjetDansSlot(Objet objet, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.objetsSpecifiques.length) {
            System.err.println(Main.ANSI_RED + "Erreur: Slot invalide (" + slotIndex + ")." + Main.ANSI_RESET);
            return false;
        }
        this.objetsSpecifiques[slotIndex] = objet;
        return true;
    }
    
    public int getTailleMaxObjets() { return objetsSpecifiques.length; }
    public int getOr() { return or; }
    public void setOr(int or) { this.or = Math.max(0, or); }
    public void ajouterOr(int montant) { if (montant > 0) or += montant; }
    public boolean retirerOr(int montant) {
        if (montant > 0 && or >= montant) { or -= montant; return true; }
        System.out.println(Main.ANSI_RED + "Pas assez d'or pour retirer " + montant + Main.ANSI_RESET);
        return false;
    }
    public boolean estVide() {
        for (Objet o : objetsSpecifiques) if (o != null) return false;
        return true;
    }
}