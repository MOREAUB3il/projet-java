package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jeu.Equipe;
import jeu.Main;

public class Pretre extends Support {

    ArrayList<Personnage> equipe;
    
    private static final String[] NOMS_POSSIBLES = {
        " ✨ Père Aldric du Sang Bénit 🪬 ", " ✨ Sœur Ylva de la Litanie Noire 🪬 "," ✨ Frère Caelen l’Expiateur 🪬 ", " ✨ Archinquisiteur Thamos 🪬 ", " ✨ Mère Eriss la Voix du Néant 🪬 "
    };

    public Pretre() {
        super(nomAleatoire(),"support");
    }

    private static String nomAleatoire() {
        Random rand = new Random();
        return NOMS_POSSIBLES[rand.nextInt(NOMS_POSSIBLES.length)];
    }

    public void attaque1(Monstre cible) {
        int degats = getForce();
        
        System.out.println(getNom() + "  attaque " + cible.getNom() + " avec son sceau 🪬 !");
        System.out.println("Cela inflige " + degats + " dégâts !");
        
        cible.subirDegats(degats);
    }

	public void ulti(Equipe equipeCible, Monstre cibleMonstre) { 
        if (equipeCible == null || equipeCible.getMembres().isEmpty()) {
            System.out.println(getNom() + " ne peut pas lancer son intervention sans équipe !");
            return;
        }
        if (getUltLvl() == 0) {
            System.out.println(getNom() + " n'a pas encore la faveur divine pour cet acte.");
            return;
        }

        System.out.println(Main.ANSI_BOLD + Main.ANSI_GREEN + getNom() + " implore une INTERVENTION DIVINE ! 🌟🙏" + Main.ANSI_RESET);
        List<Personnage> membresEquipe = equipeCible.getMembresVivants(); 


        int pourcentageSoin = 0;
        if (getUltLvl() == 1) pourcentageSoin = 20; 
        else if (getUltLvl() == 2) pourcentageSoin = 30; 
        else if (getUltLvl() >= 3) pourcentageSoin = 40; 

        System.out.println(Main.ANSI_GREEN + "Une vague de lumière curative enveloppe l'équipe !" + Main.ANSI_RESET);
        for (Personnage membre : membresEquipe) {
            int soinEffectif = (membre.getPvMax() * pourcentageSoin) / 100;
            membre.setPv(Math.min(membre.getPv() + soinEffectif, membre.getPvMax()));
            System.out.println("  " + membre.getNom() + " récupère " + soinEffectif + " PV. (Actuel: " + membre.getPv() + "/" + membre.getPvMax() + ")");
        }

        // 2. Purification (si ultLvl >= 2)
        if (getUltLvl() >= 2) {
            System.out.println(Main.ANSI_CYAN + "L'énergie sacrée dissipe les afflictions !" + Main.ANSI_RESET);
            for (Personnage membre : membresEquipe) {
                List<EffetTemporaire> effetsMembre = membre.getEffetsActifs(); 
                if (effetsMembre != null && !effetsMembre.isEmpty()) {
                    List<EffetTemporaire> debuffs = new ArrayList<>();
                    for (EffetTemporaire effet : effetsMembre) {
                        if (effet.estDebuff()) {
                            debuffs.add(effet);
                        }
                    }
                    if (!debuffs.isEmpty()) {
                        EffetTemporaire debuffASupprimer = debuffs.get(new Random().nextInt(debuffs.size())); 
                        membre.annulerLogiqueEffetStats(debuffASupprimer); 
                        effetsMembre.remove(debuffASupprimer);
                        System.out.println("  L'affliction '" + debuffASupprimer.getNom() + "' " + debuffASupprimer.getEmoji() + " est purifiée de " + membre.getNom() + " !");
                    }
                    }
                }
            }
        // 3. Buff de défense (si ultLvl >= 3)
        if (getUltLvl() >= 3) {
            System.out.println(Main.ANSI_GREEN + "Une barrière protectrice se forme autour de chaque allié !" + Main.ANSI_RESET);
            for (Personnage membre : membresEquipe) {
                membre.appliquerEffet(EffetTemporaire.buffDefenseMineur());
            }
        }
    }
	public void ulti(Monstre cible) {
        System.out.println(getNom() + " canalise son énergie divine, mais a besoin de son équipe pour un effet majeur.");
        if (getUltLvl() > 0) {
            System.out.println("  Une lueur apaisante l'enveloppe.");
            this.setPv(Math.min(this.getPv() + (this.getPvMax() * 10 / 100), this.getPvMax())); // Soigne 10% de ses PV max
        }
    }
 
}
