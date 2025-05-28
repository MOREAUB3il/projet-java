package personnage;

import monstre.Monstre;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import jeu.Main;


public abstract class Personnage {
    private String nom;
    private String sante; 
    private int pv;
    private int pvMax;    
    private int forceBase; 
    protected int niveau;
    protected int xp;
    protected int xpPourNiveauSuivant;
    private String type; 
    protected int compLvl;
    protected int ultLvl;

    protected List<EffetTemporaire> effetsActifs;
    protected int modificateurForceEffets;
    protected int modificateurDefenseEffets;
    protected int defenseBaseSpecifiqueType; 

    public static final String SANTE_PLEINE_FORME = "en pleine forme";
    public static final String SANTE_FATIGUE = "fatigué";
    public static final String SANTE_MALADE = "malade";
    public static final String SANTE_EPUISE = "épuisé";
    public static final String SANTE_SUR_LA_FIN = "sur la fin";


    public Personnage(String nom, String type) {
        this.nom = nom;
        this.type = type; 
        this.sante = santeAleatoire();

        this.effetsActifs = new ArrayList<>();
        this.modificateurForceEffets = 0;
        this.modificateurDefenseEffets = 0;


        actualiserStatsSelonSante(); 

        this.pv = this.pvMax; 
        this.niveau = 1;
        this.xp = 0;
        this.xpPourNiveauSuivant = 50;
        this.compLvl = 1;
        this.ultLvl = 0;
    }


    public int getForce() {
        return Math.max(1, this.forceBase + modificateurForceEffets);
    }

    public int getDefense() {
        int defenseEffective = this.defenseBaseSpecifiqueType + modificateurDefenseEffets;
        return Math.max(0, defenseEffective);
    }


    public String getSante() { return sante; }

    public void setSante(String nouvelleSante) {
        if (nouvelleSante == null || nouvelleSante.trim().isEmpty()) return;
        String[] etatsValides = {SANTE_PLEINE_FORME, SANTE_FATIGUE, SANTE_MALADE, SANTE_EPUISE, SANTE_SUR_LA_FIN};
        boolean estValide = false;
        for (String etat : etatsValides) if (etat.equals(nouvelleSante)) { estValide = true; break; }

        if (estValide && !this.sante.equals(nouvelleSante)) {
            System.out.println("\u001B[35m" + this.nom + " passe de l'état '" + this.sante + "' à '" + nouvelleSante + "'.\u001B[0m");
            this.sante = nouvelleSante;
            actualiserStatsSelonSante();
        }
    }

    protected void actualiserStatsSelonSante() {
        int pvBaseAncienne = this.pvMax;
        int pvMaxDeBasePourType;
        int forceDeBaseInitialePourType; 


        if (this instanceof Tank) {
            pvMaxDeBasePourType = 50;
            forceDeBaseInitialePourType = 10;
            this.defenseBaseSpecifiqueType = 10; 
        } else if (this instanceof Support) {
            pvMaxDeBasePourType = 40;
            forceDeBaseInitialePourType = 10;
            this.defenseBaseSpecifiqueType = 0;
        } else { 
            pvMaxDeBasePourType = 30;
            forceDeBaseInitialePourType = 20;
            this.defenseBaseSpecifiqueType = 0;
        }


        switch (this.sante) {
            case SANTE_PLEINE_FORME: this.pvMax = pvMaxDeBasePourType; this.forceBase = forceDeBaseInitialePourType; break;
            case SANTE_FATIGUE: this.pvMax = (int) (pvMaxDeBasePourType * 0.9); this.forceBase = (int) (forceDeBaseInitialePourType * 0.85); break;
            case SANTE_MALADE: this.pvMax = (int) (pvMaxDeBasePourType * 0.75); this.forceBase = (int) (forceDeBaseInitialePourType * 0.70); break;
            case SANTE_EPUISE: this.pvMax = (int) (pvMaxDeBasePourType * 0.7); this.forceBase = (int) (forceDeBaseInitialePourType * 0.60); break; 
            case SANTE_SUR_LA_FIN: this.pvMax = (int) (pvMaxDeBasePourType * 0.5); this.forceBase = (int) (forceDeBaseInitialePourType * 0.40); break; 
            default: this.pvMax = (int) (pvMaxDeBasePourType * 0.9); this.forceBase = (int) (forceDeBaseInitialePourType * 0.85); break;
        }
        this.pvMax = Math.max(1, this.pvMax); 
        this.forceBase = Math.max(1, this.forceBase); 

        if (this.pv > this.pvMax) this.pv = this.pvMax;
        if (this.pv == 0 && this.pvMax > 0 && pvBaseAncienne == 0) this.pv = 1;

        // System.out.println("Stats de " + this.nom + " actualisées (" + this.sante + "): PV Max=" + this.pvMax + ", Force Base=" + this.forceBase);
    }

    public void appliquerMaladie() {
        if (this.sante.equals(SANTE_SUR_LA_FIN) || this.sante.equals(SANTE_EPUISE) || this.sante.equals(SANTE_MALADE)) {
            System.out.println("\u001B[35m" + this.nom + " est déjà mal en point ou malade." + "\u001B[0m");
        } else {
            System.out.println("\u001B[35m" + this.getNom() + " tombe malade !" + "\u001B[0m");
            setSante(SANTE_MALADE);
        }
    }


    public void appliquerEffet(EffetTemporaire effetAApliquer) {
        if (effetAApliquer == null) return;
        for (EffetTemporaire effetExistant : effetsActifs) {
            if (effetExistant.getNom().equals(effetAApliquer.getNom())) {
                effetExistant.reinitialiserDuree();
                System.out.println(this.nom + ": Durée de '" + effetExistant.getNom() + "' " + effetExistant.getEmoji() + " rafraîchie.");
                return;
            }
        }
        this.effetsActifs.add(effetAApliquer);
        System.out.println(this.nom + " est maintenant affecté par : " + effetAApliquer.getNom() + " " + effetAApliquer.getEmoji());
        switch (effetAApliquer.getNom()) {
            case "Force Accrue (P)": this.modificateurForceEffets += 2; System.out.println("  -> Force augmentée !"); break;
            case "Force Accrue (M)": this.modificateurForceEffets += 5; System.out.println("  -> Force grandement augmentée !"); break;
            case "Force Réduite": this.modificateurForceEffets -= 3; System.out.println("  -> Force réduite..."); break;
            case "Défense Renforcée (P)": this.modificateurDefenseEffets += 3; System.out.println("  -> Défense renforcée !"); break;
            case "Protection Divine": this.modificateurDefenseEffets += 10; System.out.println("  -> Protection Divine active !"); break; 
            case "Défense Brisée": this.modificateurDefenseEffets -= 5; System.out.println("  -> Défense brisée !"); break;
        }
    }

    protected void annulerLogiqueEffetStats(EffetTemporaire effetExpire) {
        switch (effetExpire.getNom()) {
            case "Force Accrue (P)": this.modificateurForceEffets -= 2; System.out.println("  -> " + this.nom + ": Buff de force (P) dissipé."); break;
            case "Force Accrue (M)": this.modificateurForceEffets -= 5; System.out.println("  -> " + this.nom + ": Buff de force (M) dissipé."); break;
            case "Force Réduite": this.modificateurForceEffets += 3; System.out.println("  -> " + this.nom + ": Malus de force dissipé."); break;
            case "Défense Renforcée (P)": this.modificateurDefenseEffets -= 3; System.out.println("  -> " + this.nom + ": Buff de défense dissipé."); break;
            case "Protection Divine": this.modificateurDefenseEffets -= 10; System.out.println("  -> " + this.nom + ": Protection Divine dissipée."); break;
            case "Défense Brisée": this.modificateurDefenseEffets += 5; System.out.println("  -> " + this.nom + ": Malus de défense dissipé."); break;
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
                    String nomCouleur = (this instanceof Personnage) ? Main.ANSI_YELLOW : Main.ANSI_PURPLE; 
                    System.out.println("L'effet '" + effet.getNom() + "' " + effet.getEmoji() + " a expiré pour " + nomCouleur + this.getNom() + Main.ANSI_RESET);
                    annulerLogiqueEffetStats(effet);
                    iter.remove();
                }
            }
        }
    }

    public void subirDegatsDirects(int degats, String messageAction) {
        if (degats <= 0) return;
        System.out.print(this.nom + " " + messageAction + "... ");
        this.setPv(this.getPv() - degats);
        System.out.println("et subit " + "\u001B[31m" + degats + " dégâts directs\u001B[0m" + " ! PV: " + this.pv + "/" + this.pvMax);
        if (!estVivant()) {
            System.out.println(Main.ANSI_RED + this.nom + " succombe !" + Main.ANSI_RESET );
        }
    }
    
    public String afficherEmojisEffets() {
        if (effetsActifs == null || effetsActifs.isEmpty()) return "";
        return effetsActifs.stream().map(EffetTemporaire::getEmoji).collect(Collectors.joining(" "));
    }

    public String getDetailsEffetsString() {
        if (effetsActifs == null || effetsActifs.isEmpty()) return "Aucun effet actif.";
        return effetsActifs.stream().map(EffetTemporaire::toString).collect(Collectors.joining(", "));
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getPv() { return pv; }
    public void setPv(int pv) { this.pv = Math.max(0, Math.min(pv, this.pvMax)); }
    public int getPvMax() { return pvMax; }
    public void setPvMax(int pvMax) { 
        this.pvMax = Math.max(1, pvMax);
        if (this.pv > this.pvMax) this.pv = this.pvMax;
    }
    public int getDefenseBaseSpecifiqueType() { return defenseBaseSpecifiqueType; }
    public int getForceBase() { return forceBase; }
    public void setForceBase(int force) { this.forceBase = Math.max(1, force); } 
    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; } 
    public String getType() { return type; }
    public int getXp() { return xp; }
    public int getXpPourNiveauSuivant() { return xpPourNiveauSuivant; }
    public int getCompLvl() { return compLvl; }
    public int getUltLvl() { return ultLvl; }
    public List<EffetTemporaire> getEffetsActifs() { return effetsActifs; }


    private static final String[] SANTE_POSSIBLES = {
        SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,SANTE_PLEINE_FORME,
        SANTE_FATIGUE, SANTE_FATIGUE,SANTE_FATIGUE,SANTE_FATIGUE,
        SANTE_EPUISE, SANTE_EPUISE,SANTE_EPUISE,
        SANTE_SUR_LA_FIN
    };
    protected static String santeAleatoire() {
        Random rand = new Random();
        return SANTE_POSSIBLES[rand.nextInt(SANTE_POSSIBLES.length)];
    }

    public void gagnerXp(int quantite) {
        if (quantite <= 0) return;
        this.xp += quantite;
        System.out.println("\u001B[32m" + nom + " gagne " + quantite + " XP !\u001B[0m");
        
        while (this.xp >= this.xpPourNiveauSuivant && this.xpPourNiveauSuivant > 0) {
            this.xp -= this.xpPourNiveauSuivant;
            this.niveau++;
            this.xpPourNiveauSuivant = (int) (this.xpPourNiveauSuivant * 1.15) ;
            System.out.println("\u001B[1m\u001B[32m" + nom + " monte au niveau " + this.niveau + " !\u001B[0m");
            augmenterStats(); 
            verifierAmeliorationsCompetences();
        }
        afficherBarreXp();
    }

    protected abstract void augmenterStats();

    private void verifierAmeliorationsCompetences() {
        if (this.niveau == 5 && this.compLvl < 2) { this.compLvl = 2; System.out.println(Main.ANSI_YELLOW + "-> " + this.nom + ": Compétence améliorée (Niv. 2) !" + Main.ANSI_RESET); }
        if (this.niveau == 10 && this.ultLvl == 0) { this.ultLvl = 1; System.out.println(Main.ANSI_YELLOW + "-> " + this.nom + ": ULTIME DÉBLOQUÉ !" + Main.ANSI_RESET); }
        if (this.niveau == 15 && this.compLvl < 3) { this.compLvl = 3; System.out.println(Main.ANSI_YELLOW + "-> " + this.nom + ": Compétence améliorée (Niv. 3) !" + Main.ANSI_RESET); }
        if (this.niveau == 20 && this.ultLvl < 2) { this.ultLvl = 2; System.out.println(Main.ANSI_YELLOW + "-> " + this.nom + ": Ultime amélioré (Niv. 2) !" + Main.ANSI_RESET); }
    }

    protected void afficherBarreXp() {
        int tailleBarre = 20;
        double ratio = (xpPourNiveauSuivant == 0) ? 1.0 : (double) xp / xpPourNiveauSuivant;
        int nbBlocs = (int) (ratio * tailleBarre);
        StringBuilder barre = new StringBuilder("[");
        for (int i = 0; i < tailleBarre; i++) barre.append(i < nbBlocs ? "█" : "░");
        barre.append("]");
        System.out.println("  XP : " + "\u001B[33m" + xp + "/" + xpPourNiveauSuivant + "\u001B[0m" + " " + barre);
    }

    private static String BarreXpString(Personnage p) {
        int tailleBarre = 15;
        double ratio = (p.getXpPourNiveauSuivant() == 0) ? 1.0 : (double) p.getXp() / p.getXpPourNiveauSuivant();
        int nbBlocs = (int) (ratio * tailleBarre);
        StringBuilder barre = new StringBuilder();
        barre.append("XP [");
        for (int i = 0; i < tailleBarre; i++) barre.append(i < nbBlocs ? "█" : "░");
        barre.append("] ").append(p.getXp()).append("/").append(p.getXpPourNiveauSuivant());
        return barre.toString();
    }

    public void afficherStat() {
        String bold = "\u001B[1m"; String reset = "\u001B[0m";
        System.out.println(bold + getNom() + " " + afficherEmojisEffets() + reset);
        System.out.println("  Classe : " + this.getClass().getSimpleName() + " (" + getType() + ")");
        System.out.println("  Santé  : " + getSante());
        System.out.println("  PV     : " + getPv() + "/" + getPvMax());
        System.out.println("  Force  : " + getForce() + " (Base: " + forceBase + ")");
        System.out.println("  Défense: " + getDefense() + " (Base: " + defenseBaseSpecifiqueType + ")");
        if (this instanceof Dps) System.out.println("  Pénétration: " + ((Dps)this).getPenetration());
        if (this instanceof Support) System.out.println("  Soin Bonus: " + ((Support)this).getSoin());
        if (this instanceof Mage) System.out.println("  Malédiction: " + ((Mage)this).getMalediction());
        System.out.println("  Niveau : " + getNiveau());
        System.out.println("  CompLvl: " + getCompLvl() + ", UltLvl : " + (getUltLvl() > 0 ? getUltLvl() : "N/A"));
        afficherBarreXp();
        if (effetsActifs != null && !effetsActifs.isEmpty()) System.out.println("  Effets : " + getDetailsEffetsString());
        System.out.println("------------------------------");
    }

    public static void afficherEquipeP(List<Personnage> liste) {
         if (liste == null || liste.isEmpty()) {
            System.out.println("Aucun personnage à afficher.");
            return;
        }
        int largeurColonne = 35; 
        String formatCellule = "%-" + largeurColonne + "s";
        String separateurLigne = String.join("", Collections.nCopies(liste.size(), String.format(formatCellule, "----------------------------------")));

        for (Personnage p : liste) {
            String nomEtEmojis = p.getNom() + " " + p.afficherEmojisEffets();
            System.out.printf(formatCellule, nomEtEmojis.strip());
        }
        System.out.println("\n" + separateurLigne);
        String[] labels = {"Santé", "PV", "Force", "Défense", "Stat Spéc.", "Niveau", "CompLvl", "UltLvl", "XP"};
        for (String label : labels) {
            for (Personnage p : liste) {
                String valeur = "";
                switch (label) {
                    case "Santé": valeur = "Santé: " + p.getSante(); break;
                    case "PV": valeur = "PV: " + p.getPv() + "/" + p.getPvMax(); break;
                    case "Force": valeur = "Force: " + p.getForce(); break;
                    case "Défense": valeur = "Défense: " + p.getDefense(); break;
                    case "Stat Spéc.":
                        if (p instanceof Dps) valeur = "Péné: " + ((Dps)p).getPenetration();
                        else if (p instanceof Support) valeur = "Soin: " + ((Support)p).getSoin();
                        else if (p instanceof Mage) valeur = "Maled: " + ((Mage)p).getMalediction();
                        else valeur = ""; 
                        break;
                    case "Niveau": valeur = "Niveau: " + p.getNiveau(); break;
                    case "CompLvl": valeur = "CompLvl: " + p.getCompLvl(); break;
                    case "UltLvl": valeur = "UltLvl: " + (p.getUltLvl() > 0 ? p.getUltLvl() : "N/A"); break;
                    case "XP": valeur = BarreXpString(p); break;
                }
                if (label.equals("Stat Spéc.") && valeur.isEmpty() && liste.stream().noneMatch(perso -> (perso instanceof Dps || perso instanceof Support || perso instanceof Mage))) {
                } else if (label.equals("Stat Spéc.") && valeur.isEmpty()){
                     System.out.printf(formatCellule, ""); 
                }
                 else {
                    System.out.printf(formatCellule, valeur);
                }
            }
            boolean doitImprimerLigne = true;
            if (label.equals("Stat Spéc.")) {
                doitImprimerLigne = liste.stream().anyMatch(perso -> (perso instanceof Dps || perso instanceof Support || perso instanceof Mage));
            }
            if (doitImprimerLigne) {
                System.out.println();
            }
        }
        System.out.println(separateurLigne);
        boolean detailsAffichesTitre = false;
        for (Personnage p : liste) {
            if (p.effetsActifs != null && !p.effetsActifs.isEmpty()) {
                if (!detailsAffichesTitre) { System.out.println("\nDétails des Effets Actifs :"); detailsAffichesTitre = true; }
                System.out.println("  " + p.getNom() + ": " + p.getDetailsEffetsString());
            }
        }
        if (detailsAffichesTitre) System.out.println();
    }

    public abstract void attaque1(Monstre cible);
    public abstract void ulti(Monstre cible);
    public abstract void subirDegats(int degatsBruts); 

    public boolean estVivant() { return pv > 0; }
    public void setPvMaxDirect(int pvMax) { this.pvMax = pvMax; }
    public void setForceBaseDirect(int forceBase) { this.forceBase = forceBase; }
    public void setDefenseBaseSpecifiqueTypeDirect(int defBase) { this.defenseBaseSpecifiqueType = defBase; }
    public void setNiveauLoaded(int niveau) { this.niveau = niveau; }
    public void setXpLoaded(int xp, int xpNext) { this.xp = xp; this.xpPourNiveauSuivant = xpNext; }
    public void setCompLvlLoaded(int compLvl) { this.compLvl = compLvl; }
    public void setUltLvlLoaded(int ultLvl) { this.ultLvl = ultLvl; }
}