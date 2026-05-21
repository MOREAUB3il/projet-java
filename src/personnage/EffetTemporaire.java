package personnage;

import jeu.Main;

public class EffetTemporaire {

    private String nom;                
    private String emoji;               
    private int dureeToursInitiale;     
    private int dureeToursRestante;     
    private boolean estDebuff;          
    
    private EffetTemporaire(String nom, String emoji, int dureeTours, boolean estDebuff) {
        this.nom = nom;
        this.emoji = emoji;
        this.dureeToursInitiale = dureeTours;
        this.dureeToursRestante = dureeTours; 
        this.estDebuff = estDebuff;
    }

    
    public String getNom() {
        return nom;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getDureeToursRestante() {
        return dureeToursRestante;
    }

    public int getDureeToursInitiale() {
        return dureeToursInitiale;
    }

    public boolean estDebuff() {
        return estDebuff;
    }

    public boolean decrementerDuree() {
        if (this.dureeToursInitiale == -1) { 
            return true;
        }
        if (this.dureeToursRestante > 0) {
            this.dureeToursRestante--;
        }
        return this.dureeToursRestante > 0;
    }


    public void reinitialiserDuree() {
        this.dureeToursRestante = this.dureeToursInitiale;
    }

    public boolean estPermanent() {
        return this.dureeToursInitiale == -1;
    }

    @Override
    public String toString() {
        String dureeAffichage;
        if (estPermanent()) {
            dureeAffichage = "(Permanent)";
        } else if (dureeToursRestante > 0) {
            dureeAffichage = "(" + dureeToursRestante + "t)";
        } else {
            dureeAffichage = "(Expiré)";
        }
        return emoji + " " + nom + " " + dureeAffichage;
    }

    public static EffetTemporaire buffForceFaible() {
        return new EffetTemporaire("Force Accrue (P)", "🔼💪", 2, false);
    }
    public static EffetTemporaire buffForceMoyen() {
        return new EffetTemporaire("Force Accrue (M)", "⏫💪", 2, false);
    }
    public static EffetTemporaire buffForceForte() {
        return new EffetTemporaire("Force Accrue (M)", "⏫💪", 3, false);
    }
    public static EffetTemporaire buffDefenseMineur() {
        return new EffetTemporaire("Défense Renforcée (P)", "🔼🛡️", 3, false);
    }
     public static EffetTemporaire buffDefenseMajeur() {
        return new EffetTemporaire("Protection Divine", "🛡️✨", 2, false);
    }
     
    public static EffetTemporaire debuffForce() {
        return new EffetTemporaire("Force Réduite", "🔽💪", 3, true);
    }
    public static EffetTemporaire debuffDefense() { // Ex: Brise-armure
        return new EffetTemporaire("Défense Brisée", "🔽🛡️", 3, true);
    }

    // Dégâts sur la durée (DoT)
    public static EffetTemporaire saignement() {
        return new EffetTemporaire("Saignement", "🩸", 2, true); // Dure 2 applications de dégâts
    }
    public static EffetTemporaire brulureLegere() {
        return new EffetTemporaire("Brûlure", "🔥", 1, true); // Dure 1 application de dégâts
    }
    public static EffetTemporaire brulureIntense() {
        return new EffetTemporaire("Brûlure Intense", "🔥🔥", 1, true); // Dure 1 application de dégâts (plus forte)
    }
    public static EffetTemporaire poison() {
        return new EffetTemporaire(Main.ANSI_GREEN +"Poison", "☠️🧪"+ Main.ANSI_RESET, 3, true); // Dure 3 applications de dégâts
    }
}
/*
    // Contrôle (Crowd Control - CC)
    public static EffetTemporaire etourdissement() {
        return new EffetTemporaire("Étourdi", "💫", 1, true); // Empêche d'agir pour 1 tour
    }
    public static EffetTemporaire silence() { // Empêche d'utiliser des compétences/magie
        return new EffetTemporaire("Silence", "🤫", 2, true);
    }
    */
    