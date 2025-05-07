package inventaire;

import personnage.Personnage;


public class Inventaire {
    private int or;

    
    private Objet[] objetsSpecifiques;

    
    private static final String[] NOMS_OBJETS_PAR_SLOT = {
        Objet.potionCommune().getNom(),     //  0
        Objet.potionRare().getNom(),        //  1
        Objet.potionLegendaire().getNom(),  //  2
        Objet.collierCommun().getNom(),     //  3
        Objet.collierRare().getNom(),       //  4
        Objet.collierLegendaire().getNom()  //  5
    };

    public Inventaire() {
    	this.or = 50;
		this.objetsSpecifiques = new Objet[6];
    }

    public boolean ajouterObjet(Objet objetAAjouter) {
        if (objetAAjouter == null) return false;

        for (int i = 0; i < NOMS_OBJETS_PAR_SLOT.length; i++) {
            if (NOMS_OBJETS_PAR_SLOT[i].equals(objetAAjouter.getNom())) {
                if (objetsSpecifiques[i] == null) { 
                    objetsSpecifiques[i] = objetAAjouter;
                    System.out.println(objetAAjouter.getNom() + " ajouté à l'inventaire (Slot " + (i + 1) + ").");
                    return true;
                } else {
                    System.out.println("Vous possédez déjà un(e) " + NOMS_OBJETS_PAR_SLOT[i] + " (Slot " + (i + 1) + "). Impossible d'en ajouter un autre.");
                    return false;
                }
            }
        }
        System.out.println("L'objet '" + objetAAjouter.getNom() + "' n'a pas de slot désigné dans cet inventaire.");
        return false;
    }

    public boolean utiliserObjet(int slotIndex, Personnage utilisateur, Personnage cible) {
        if (slotIndex < 0 || slotIndex >= objetsSpecifiques.length) {
            System.out.println("Slot d'objet invalide.");
            return false;
        }
        if (objetsSpecifiques[slotIndex] != null) {
            Objet objetAUtiliser = objetsSpecifiques[slotIndex];
            
            objetAUtiliser.utiliser(cible); 

            objetsSpecifiques[slotIndex] = null; 
            System.out.println(objetAUtiliser.getNom() + " a été retiré du slot " + (slotIndex + 1) + " après utilisation.");
            return true;
        } else {
            System.out.println("Le slot " + (slotIndex + 1) + " ("+ NOMS_OBJETS_PAR_SLOT[slotIndex] +") est vide.");
            return false;
        }
    }

    public Objet getObjetDansSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= objetsSpecifiques.length) {
            return null;
        }
        return objetsSpecifiques[slotIndex];
    }

    public void afficherInventaire() {
        System.out.println("\n--- Inventaire ---");
        System.out.println("Or: " + or);
        System.out.println("Objets (Max 1 de chaque type spécifique):");
        boolean auMoinsUnObjet = false;
        for (int i = 0; i < objetsSpecifiques.length; i++) {
            System.out.print("  Slot " + (i + 1) + " [" + NOMS_OBJETS_PAR_SLOT[i] + "]: ");
            if (objetsSpecifiques[i] != null) {
                System.out.println(objetsSpecifiques[i].getNom() + " (x1)");
                auMoinsUnObjet = true;
            } else {
                System.out.println("[Vide]");
            }
        }
        if (!auMoinsUnObjet && objetsSpecifiques.length > 0) {
            System.out.println("  (Aucun objet possédé)");
        }
        System.out.println("------------------");
    }

    public int getOr() {
        return or;
    }

    public void setOr(int or) {
        if (or >= 0) {
            this.or = or;
        }
    }

    public void ajouterOr(int montant) {
        if (montant > 0) {
            or += montant;
        }
    }

    public boolean retirerOr(int montant) {
        if (montant > 0 && or >= montant) {
            or -= montant;
            return true;
        }
        return false;
    }
}