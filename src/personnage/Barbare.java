package personnage;


import monstre.Monstre;
import java.util.Random;

import jeu.Main;

public  class Barbare extends Dps {
    
    private static final String[] NOMS_POSSIBLES = {
    		" 🔪 Gorvok Crâne-Sanglant 🪓 "," 🔪 Olaf le Berserker 🪓 "," 🔪 Varkh le Hurleur d’Abîme 🪓 ","🔪 Korgash le Maudit 🪓 "," 🔪 Dragan Croc-de-Fer 🪓 "
    };

    public Barbare() {
        super(nomAleatoire(),"dps");
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }
   
    public void attaque1(Monstre cible) {
        if (cible == null || !cible.estVivant()) {
            System.out.println(getNom() + " rugit mais ne trouve pas de cible valide !");
            return;
        }
        int forceEffective = getForce();
        int penetrationEffective = getPenetration();
        int defenseCibleReduite = Math.max(0, cible.getDefence() - penetrationEffective);
        int degatsBruts = forceEffective + (penetrationEffective / 2);
        double multiplicateurCompLvl = 1.0;
        int bonusChanceSaignement = 0;

        if (getCompLvl() == 1) {
            multiplicateurCompLvl = 1.0;
            bonusChanceSaignement = 0;
        } else if (getCompLvl() == 2) {
            multiplicateurCompLvl = 1.2; 
            bonusChanceSaignement = 10; 
        } else if (getCompLvl() >= 3) {
            multiplicateurCompLvl = 1.4; 
            bonusChanceSaignement = 20;  
        }
        
        int degatsFinaux = (int) (degatsBruts * multiplicateurCompLvl);
        degatsFinaux = Math.max(1, degatsFinaux);

        System.out.println(Main.ANSI_CYAN + getNom() + Main.ANSI_RESET + " charge et frappe " + Main.ANSI_RED + cible.getNom() + Main.ANSI_RESET + " avec sa hache 🪓 !");
        System.out.println("La frappe inflige " + Main.ANSI_YELLOW + degatsFinaux + Main.ANSI_RESET + " dégâts bruts !");
        cible.subirDegats(degatsFinaux); 
        int chanceSaignementBase = 30; 
        if (cible.estVivant() && new Random().nextInt(100) < (chanceSaignementBase + bonusChanceSaignement)) {
            System.out.println(Main.ANSI_RED + cible.getNom() + Main.ANSI_RESET + " commence à saigner abondamment 🩸 !");
            cible.appliquerEffet(EffetTemporaire.saignement());
        }
    }
    
    public void ulti(Monstre cible) {
        if (cible == null || !cible.estVivant()) {
            System.out.println(getNom() + " cherche une cible pour déchaîner sa fureur, mais en vain !");
            return;
        }
        if (getUltLvl() == 0) {
            System.out.println(getNom() + " n'est pas assez enragé pour cela.");
            return;
        }
        
        System.out.println(Main.ANSI_BOLD + Main.ANSI_RED + getNom() + " entre dans une RAGE SANGLUINAIRE et se jette sur " + cible.getNom() + " ! ⚔️🩸" +Main.ANSI_RESET);

        int forceEffective = getForce();
        int penetrationEffective = getPenetration();
        int degatsUlti = forceEffective * 2 + penetrationEffective * 3; 

        double pourcentageRegen = 0;
        if (getUltLvl() == 1) {
            pourcentageRegen = 0.15; 
        } else if (getUltLvl() == 2) {
            degatsUlti = (int)(degatsUlti * 1.3); 
            pourcentageRegen = 0.30; 
        } else { 
            degatsUlti = (int)(degatsUlti * 1.6); 
            pourcentageRegen = 0.60; 
        }
        degatsUlti = Math.max(forceEffective, degatsUlti); 

        System.out.println("Sa fureur inflige " + Main.ANSI_YELLOW + degatsUlti + Main.ANSI_RESET + " dégâts dévastateurs !");
        int pvAvantDegatsMonstre = cible.getPv();
        cible.subirDegats(degatsUlti);
        int degatsReellementInfligesAuMonstre = pvAvantDegatsMonstre - cible.getPv(); 

        if (degatsReellementInfligesAuMonstre > 0) {
            int soinRecu = (int) (degatsReellementInfligesAuMonstre * pourcentageRegen);
            soinRecu = Math.max(0, soinRecu); 
            if (soinRecu > 0) {
                this.setPv(Math.min(this.getPv() + soinRecu, this.getPvMax()));
                System.out.println(Main.ANSI_GREEN + getNom() + " se régénère de " + soinRecu + " PV grâce à la ferveur du combat ! (PV: " + getPv() + "/" + getPvMax() + ")" + Main.ANSI_RESET);
            }
        }
    }
}