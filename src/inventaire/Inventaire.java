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
        Objet.collierLegendaire().getNom(),  //  5
        Objet.fioleDePoison().getNom()      //  6
    };

    public Inventaire() {
    	this.or = 50;
		this.objetsSpecifiques = new Objet[7];
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
            
            objetAUtiliser.utiliser(cible, objetAUtiliser); 

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
    public boolean utiliserObjetInteractive(Scanner scanner, Personnage utilisateur,List<Personnage> ciblesEquipe, List<Monstre> ciblesMonstres,Equipe equipePourMenu, int etagePourMenu, String nomJoueurPourMenu) {
		if (estVide()) {
			System.out.println(Main.ANSI_YELLOW + "L'inventaire est vide." + Main.ANSI_RESET);
			return false;
		}
		
		afficherInventaire();
		int choixSlotInput = Main.lireIntAvecMenuPause(scanner,
			Main.ANSI_CYAN + "Objet à utiliser (slot 1-" + objetsSpecifiques.length + ", 0 annuler, 'M' menu) : " + Main.ANSI_RESET,
			equipePourMenu, etagePourMenu, nomJoueurPourMenu);
		
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
				if (ciblesEquipe.isEmpty()) {
					System.out.println(Main.ANSI_RED + "Aucun allié à cibler." + Main.ANSI_RESET);
					return false;
				}
				System.out.println(Main.ANSI_CYAN + "Sur quel allié utiliser " + objetAUtiliser.getNom() + " ('M' menu) ?" + Main.ANSI_RESET);
				for (int i = 0; i < ciblesEquipe.size(); i++) {
					System.out.println("  " + (i + 1) + ". " + ciblesEquipe.get(i).getNom());
				}
				int choixAllieInput = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu);
					if (!Main.continuerJeuGlobal || choixAllieInput == -999) return false;
					if (choixAllieInput > 0 && choixAllieInput <= ciblesEquipe.size()) {
						cibleFinale = ciblesEquipe.get(choixAllieInput - 1);
					} else {
						System.out.println(Main.ANSI_RED + "Choix d'allié invalide." + Main.ANSI_RESET);
						return false;
					}
					break;
				
				case "ENNEMI":
					if (ciblesMonstres.isEmpty()) {
						System.out.println(Main.ANSI_RED + "Aucun ennemi à cibler." + Main.ANSI_RESET);
						return false;
					}
					System.out.println(Main.ANSI_CYAN + "Sur quel ennemi utiliser " + objetAUtiliser.getNom() + " ('M' menu) ?" + Main.ANSI_RESET);
					for (int i = 0; i < ciblesMonstres.size(); i++) {
						System.out.println("  " + (i + 1) + ". " + ciblesMonstres.get(i).getNom());
					}
					int choixEnnemiInput = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu);
					if (!Main.continuerJeuGlobal || choixEnnemiInput == -999) return false;
					if (choixEnnemiInput > 0 && choixEnnemiInput <= ciblesMonstres.size()) {
						cibleFinale = ciblesMonstres.get(choixEnnemiInput - 1);
					} else {
						System.out.println(Main.ANSI_RED + "Choix d'ennemi invalide." + Main.ANSI_RESET);
						return false;
					}
					break;
			
				case "ALLIE_OU_ENNEMI":
					String typeDeCibleStr = Main.lireStringAvecMenuPause(scanner, 
							Main.ANSI_CYAN + "Cibler un allié (A) ou un ennemi (E) ('M' menu) ? " + Main.ANSI_RESET, 
							equipePourMenu, etagePourMenu, nomJoueurPourMenu);
					if (!Main.continuerJeuGlobal || "QUIT_GAME_INTERNAL".equals(typeDeCibleStr)) return false;
			
					if (typeDeCibleStr.equalsIgnoreCase("A")) {
						// Copier la logique de "ALLIE"
						if (ciblesEquipe.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun allié." + Main.ANSI_RESET); return false; }
						System.out.println(Main.ANSI_CYAN + "Sur quel allié ('M' menu) ?" + Main.ANSI_RESET);
						for (int i = 0; i < ciblesEquipe.size(); i++) { System.out.println("  " + (i + 1) + ". " + ciblesEquipe.get(i).getNom()); }
						int choixAllieAE = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu);
						if (!Main.continuerJeuGlobal || choixAllieAE == -999) return false;
						if (choixAllieAE > 0 && choixAllieAE <= ciblesEquipe.size()) cibleFinale = ciblesEquipe.get(choixAllieAE - 1);
						else { System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET); return false; }
						} else if (typeDeCibleStr.equalsIgnoreCase("E")) {

							if (ciblesMonstres.isEmpty()) { System.out.println(Main.ANSI_RED + "Aucun ennemi." + Main.ANSI_RESET); return false; }
							System.out.println(Main.ANSI_CYAN + "Sur quel ennemi ('M' menu) ?" + Main.ANSI_RESET);
							for (int i = 0; i < ciblesMonstres.size(); i++) { System.out.println("  " + (i + 1) + ". " + ciblesMonstres.get(i).getNom()); }
							int choixEnnemiAE = Main.lireIntAvecMenuPause(scanner, "Choix : ", equipePourMenu, etagePourMenu, nomJoueurPourMenu);
							if (!Main.continuerJeuGlobal || choixEnnemiAE == -999) return false;
							if (choixEnnemiAE > 0 && choixEnnemiAE <= ciblesMonstres.size()) cibleFinale = ciblesMonstres.get(choixEnnemiAE - 1);
							else { System.out.println(Main.ANSI_RED + "Choix invalide." + Main.ANSI_RESET); return false; }
						} else {
							System.out.println(Main.ANSI_RED + "Type de cible (A/E) invalide." + Main.ANSI_RESET);
							return false;
						}
					break;
			
			// TODO: Gérer "TOUS_ALLIES", "TOUS_ENNEMIS" si nécessaire (pas de choix de cible individuelle)
			// case "TOUS_ALLIES":
			//     for (Personnage pAllie : ciblesEquipe) { objetAUtiliser.utiliser(utilisateur, pAllie); }
			//     objetsSpecifiques[choixSlotIndex] = null; // Consommer une seule fois
			//     return true; // Pas besoin de cibleFinale
			
				default:
					System.out.println(Main.ANSI_RED + "Type de cible de l'objet non géré : " + objetAUtiliser.getCibleAutorisee() + Main.ANSI_RESET);
					return false;
				}
			
				if (cibleFinale != null) {
					objetAUtiliser.utiliser(utilisateur, cibleFinale);
					objetsSpecifiques[choixSlotIndex] = null;
					return true;
				} else {

					System.out.println(Main.ANSI_RED + "Erreur: Aucune cible finale déterminée pour l'objet." + Main.ANSI_RESET);
					return false;
				}
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
    public boolean estVide() {
    	for(Objet o : objetsSpecifiques) {
    		if(o != null) {
    			return false;
    		}
    	
    	}
    	return true;
    }
}