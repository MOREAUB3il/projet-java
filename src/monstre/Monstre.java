package monstre;

import personnage.Personnage;
import personnage.EffetTemporaire; 
import event.Mercenaire;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jeu.Main;

public class Monstre {
    private String nomOriginal;
    private String nom;
    private int pv;
    private int pvMax;
    private int forceBase;
    private int defenceBase; 
    private double maladieChance;
    private int orM;
    private int xpDonnee;

    protected List<EffetTemporaire> effetsActifs;
    protected int modificateurForceEffets;
    protected int modificateurDefenseEffets;
    

    public Monstre(String nomBase, int pvInitial, int forceInitial, int defenceInitial, double maladieChance, int orInitial, int xpInitial, int etage) {
        this.nomOriginal = nomBase;

        double scaleFactorPvForce = 1.0 + ((etage - 1) * 0.03); 
        double scaleFactorDefense = 1.0 + ((etage - 1) * 0.02); 
        if (etage <= 1) {
            scaleFactorPvForce = 1.0;
            scaleFactorDefense = 1.0;
        }
        scaleFactorPvForce = Math.min(scaleFactorPvForce, 3.0); 
        scaleFactorDefense = Math.min(scaleFactorDefense, 2.0); 

        if (nomBase.toUpperCase().contains("BOSS")) {
            this.nom = nomBase; 
        } else {
            int monstreLevelIndicator = (etage / 4) + 1;
            this.nom = nomBase + " (Nv." + monstreLevelIndicator + ")";
        }

        this.pvMax = Math.max(1, (int) (pvInitial * scaleFactorPvForce));
        this.pv = this.pvMax;
        this.forceBase = Math.max(1, (int) (forceInitial * scaleFactorPvForce));
        this.defenceBase = Math.max(0, (int) (defenceInitial * scaleFactorDefense)); 

        this.maladieChance = maladieChance;
        this.orM = orInitial + (etage / 3);
        this.xpDonnee = xpInitial + (etage / 3);

        this.effetsActifs = new ArrayList<>();
        this.modificateurForceEffets = 0;
        this.modificateurDefenseEffets = 0;
    }


    public int getForce() {
        return Math.max(1, this.forceBase + modificateurForceEffets);
    }

    public int getDefence() { 
        return Math.max(0, this.defenceBase + modificateurDefenseEffets);
    }


    public void appliquerEffet(EffetTemporaire effetAApliquer) {
        if (effetAApliquer == null) return;

        for (EffetTemporaire effetExistant : effetsActifs) {
            if (effetExistant.getNom().equals(effetAApliquer.getNom())) {
                effetExistant.reinitialiserDuree();
                System.out.println(Main.ANSI_PURPLE + this.nom + Main.ANSI_RESET + ": Durée de '" + effetExistant.getNom() + "' " + effetExistant.getEmoji() + " rafraîchie.");
                return;
            }
        }

        this.effetsActifs.add(effetAApliquer);
        System.out.println(Main.ANSI_PURPLE + this.nom + Main.ANSI_RESET + " est maintenant affecté par : " + effetAApliquer.getNom() + " " + effetAApliquer.getEmoji());

        switch (effetAApliquer.getNom()) {
            case "Force Réduite":
                this.modificateurForceEffets -= 3; 
                System.out.println("  -> Force de " + this.nom + " réduite... (Total: " + getForce() + ")");
                break;
            case "Défense Brisée":
                this.modificateurDefenseEffets -= 5; 
                System.out.println("  -> Défense de " + this.nom + " brisée ! (Total: " + getDefence() + ")");
                break;
        }
    }

    protected void annulerLogiqueEffetStats(EffetTemporaire effetExpire) {
        switch (effetExpire.getNom()) {
            case "Force Réduite":
                this.modificateurForceEffets += 3;
                System.out.println("  -> " + Main.ANSI_PURPLE + this.nom + Main.ANSI_RESET + ": L'affaiblissement de force se dissipe. (Force totale: " + getForce() + ")");
                break;
            case "Défense Brisée":
                this.modificateurDefenseEffets += 5;
                System.out.println("  -> " + Main.ANSI_PURPLE + this.nom + Main.ANSI_RESET + ": La brisure de défense est réparée. (Défense totale: " + getDefence() + ")");
                break;
        }
    }

    public void mettreAJourEffets() {
        if (effetsActifs == null || effetsActifs.isEmpty() || !this.estVivant()) {
            return;
        }
        Iterator<EffetTemporaire> iter = effetsActifs.iterator();
        while (iter.hasNext()) {
            EffetTemporaire effet = iter.next();

            if ((effet.getDureeToursRestante() > 0 || effet.estPermanent()) && this.estVivant()) {
                switch (effet.getNom()) {
                    case "Saignement":
                        subirDegatsDirects(5, "saigne 🩸"); 
                        break;
                    case "Brûlure":
                        subirDegatsDirects(8, "brûle 🔥"); 
                        break;
                    case "Brûlure Intense":
                        subirDegatsDirects(15, "brûle intensément 🔥🔥");
                        break;
                    case "Poison": 
                        subirDegatsDirects(4, "est empoisonné 🧪"); 
                        break;
                    case "Empoisonné (Fiole)": 
                        int degatsPoisonFiole = (int) (this.getPv() * 0.10);
                        degatsPoisonFiole = Math.max(1, degatsPoisonFiole); 
                        subirDegatsDirects(degatsPoisonFiole, "est empoisonné par la fiole ☠️🧪");
                        break;
                }
            }
            if (!this.estVivant()) break;

            if (!effet.estPermanent()) {
                if (!effet.decrementerDuree()) {
                    String nomCouleur = (this instanceof Monstre) ? Main.ANSI_YELLOW : Main.ANSI_PURPLE; 
                    System.out.println("L'effet '" + effet.getNom() + "' " + effet.getEmoji() + " a expiré pour " + nomCouleur + this.getNom() + Main.ANSI_RESET);
                    annulerLogiqueEffetStats(effet);
                    iter.remove();
                }
            }
        }
    }

    public void subirDegatsDirects(int degats,String raison) {
        if (degats <= 0) return;
        this.setPv(this.getPv() - degats);
        System.out.println( this.getNom() + "subit " + Main.ANSI_RED + degats + " dégâts directs" + Main.ANSI_RESET + " ! PV: " + this.pv + "/" + this.pvMax);
        if (!estVivant()) {
            System.out.println(Main.ANSI_RED + this.nom + " succombe !" + Main.ANSI_RESET);
        }
    }
    
    public String afficherEmojisEffets() {
        if (effetsActifs == null || effetsActifs.isEmpty()) {
            return "";
        }
        return effetsActifs.stream()
                           .map(EffetTemporaire::getEmoji)
                           .collect(Collectors.joining(" "));
    }

    public void afficherDetailsEffets() {
        if (effetsActifs != null && !effetsActifs.isEmpty()) {
            System.out.print(Main.ANSI_CYAN + "  Effets : " + Main.ANSI_RESET);
            String details = effetsActifs.stream()
                                         .map(EffetTemporaire::toString)
                                         .collect(Collectors.joining(", "));
            System.out.println(details);
        }
    }
    
    public List<EffetTemporaire> getEffetsActifs() {
        return this.effetsActifs;
    }

    public void attaquer(Personnage cible) {
        System.out.println(Main.ANSI_RED + this.getNom() + Main.ANSI_RESET + " attaque " + Main.ANSI_YELLOW + cible.getNom() + Main.ANSI_RESET + " !");
        int degatsInfliges = this.getForce(); 
        cible.subirDegats(degatsInfliges);

        if (new Random().nextDouble() < maladieChance) {
            cible.appliquerMaladie();
        }
        
    }
    public void subirDegats(int degatsBruts) {
        int defenseMonstre = this.getDefence();
        int degatsEffectifs = degatsBruts - defenseMonstre;

        if (degatsEffectifs < 0) degatsEffectifs = 0;
        if (degatsEffectifs == 0 && degatsBruts > 0) degatsEffectifs = 1; 

        System.out.println(Main.ANSI_YELLOW + this.nom + " subit " + degatsEffectifs + " dégâts." + Main.ANSI_RESET);
        this.setPv(this.pv - degatsEffectifs);
        System.out.println("  PV restants : " + this.pv + "/" + this.pvMax);
    }
    
    public void setPv(int pv) {
        this.pv = Math.max(0, Math.min(pv, this.pvMax));
    }

    public boolean estVivant() { return pv > 0; }
    public String getNom() { return nom; }
    public int getPv() { return pv; }
    public int getPvMax() { return pvMax; }
    public int getXpDonnee() { return xpDonnee; }
    public int getOrM() { return orM; }


    public static Monstre monstreFaible(int etage) {
        return new Monstre("Gobelin Chétif", 20, 7, 1, 0.02, 5, 25, etage);
    }
    public static Monstre monstreMoyen(int etage) {
        return new Monstre("Orque Hargneux", 50, 14, 4, 0.03, 10, 40, etage);
    }
    public static Monstre monstreFort(int etage) {
        return new Monstre("Ogre Brutal", 100, 25, 7, 0.05, 20, 100, etage);
    }
    public static Monstre boss(int etage) {
        int pvBossBase = 100 + (etage * 10);
        int forceBossBase = 25 + (int)(etage * 1.2); 
        int defenseBossBase = 8 + (etage / 3);
        int orBoss = 40 + etage * 4;
        int xpBoss = 220 + etage * 8;
        return new Monstre("BOSS Étage " + etage, pvBossBase, forceBossBase, defenseBossBase, 0.1, orBoss, xpBoss, etage);
    }
}