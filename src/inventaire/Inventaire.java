package inventaire;

import java.util.List;
import java.util.Scanner;

import personnage.Personnage;

public class Inventaire {
    private int or;

    
    private Objet[] objetsSpecifiques;
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";
    
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
    public boolean utiliserObjetInteractive(Scanner scanner, Personnage utilisateur, List<Personnage> ciblesPotentielles) {
        if (estVide()) {
            System.out.println(ANSI_YELLOW + "L'inventaire est vide, aucun objet à utiliser." + ANSI_RESET);
            return false;
        }

        afficherInventaire();
        System.out.print(ANSI_CYAN + "Choisissez un objet à utiliser (entrez le numéro du slot 1-" + objetsSpecifiques.length + ") ou 0 pour annuler : " + ANSI_RESET);
        
        int choixSlotInput;
        if (scanner.hasNextInt()) {
            choixSlotInput = scanner.nextInt();
        } else {
            scanner.nextLine(); 
            System.out.println(ANSI_RED + "Entrée invalide." + ANSI_RESET);
            return false;
        }
        scanner.nextLine();

        if (choixSlotInput == 0) {
            System.out.println(ANSI_YELLOW + "Utilisation d'objet annulée." + ANSI_RESET);
            return false;
        }

        int choixSlotIndex = choixSlotInput - 1; 

        if (choixSlotIndex < 0 || choixSlotIndex >= objetsSpecifiques.length || objetsSpecifiques[choixSlotIndex] == null) {
            System.out.println(ANSI_RED + "Slot invalide ou vide." + ANSI_RESET);
            return false;
        }

        Objet objetAUtiliser = objetsSpecifiques[choixSlotIndex];

       
        if (ciblesPotentielles.isEmpty()) {
            System.out.println(ANSI_RED + "Aucune cible disponible pour l'objet." + ANSI_RESET);
            return false;
        }

        System.out.println(ANSI_CYAN + "Sur quel membre de l'équipe utiliser " + ANSI_GREEN + objetAUtiliser.getNom() + ANSI_CYAN + "?" + ANSI_RESET);
        for (int i = 0; i < ciblesPotentielles.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ciblesPotentielles.get(i).getNom() + " (PV: " + ciblesPotentielles.get(i).getPv() + "/" + ciblesPotentielles.get(i).getPvMax() + ")");
        }
        System.out.print("Votre choix (numéro de personnage) ou 0 pour annuler : ");
        
        int choixCibleInput;
        if (scanner.hasNextInt()) {
            choixCibleInput = scanner.nextInt();
        } else {
            scanner.nextLine(); 
            System.out.println(ANSI_RED + "Entrée invalide." + ANSI_RESET);
            return false;
        }
        scanner.nextLine(); 

        if (choixCibleInput == 0) {
            System.out.println(ANSI_YELLOW + "Utilisation d'objet annulée." + ANSI_RESET);
            return false;
        }

        int choixCibleIndex = choixCibleInput - 1;

        if (choixCibleIndex < 0 || choixCibleIndex >= ciblesPotentielles.size()) {
            System.out.println(ANSI_RED + "Choix de cible invalide." + ANSI_RESET);
            return false;
        }

        Personnage cibleObjet = ciblesPotentielles.get(choixCibleIndex);

        
        System.out.println(ANSI_YELLOW + utilisateur.getNom() + ANSI_RESET + " utilise " + ANSI_GREEN + objetAUtiliser.getNom() + ANSI_RESET + " sur " + ANSI_YELLOW + cibleObjet.getNom() + ANSI_RESET + ".");
        objetAUtiliser.utiliser(cibleObjet);
        
        objetsSpecifiques[choixSlotIndex] = null; 
        System.out.println(ANSI_GREEN + objetAUtiliser.getNom() + ANSI_RESET + " a été consommé.");
        
        return true; 
    }

    public boolean estVide() {
        for (Objet o : objetsSpecifiques) {
            if (o != null) return false;
         }
         return true; 
    }
    public void ouvrir(Inventaire inventaireEquipe, Scanner scanner) {
         System.out.println("Bienvenue au magasin !");
         while (true) {
              System.out.println("Or de l'équipe : " + inventaireEquipe.getOr());
         }
    }
    public int getTailleMaxObjets() { return objetsSpecifiques.length; }
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