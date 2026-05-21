package ui;

import db.Database;
import db.Database.SauvegardeInfo;
import inventaire.Objet;
import jeu.Equipe;
import monstre.Monstre;
import personnage.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gère l'état du jeu et expose une API sans Scanner pour l'interface JavaFX.
 */
public class GameManager {

    public enum Phase { MENU, SELECT_PERSO, FLOOR_PREP, EVENT, COMBAT, GAME_OVER }

    // État de jeu
    private Equipe equipe;
    private int etage = 1;
    private String nomJoueur = "";
    private int idSauvegardeActive = -1;
    private List<Monstre> monstresActuels = new ArrayList<>();
    private Database database;

    // État du combat
    private int paRestants = 4;
    private final List<Personnage> personnagesAyantAgi = new ArrayList<>();
    private Personnage attaquantSelectionne = null;
    private Phase phaseActuelle = Phase.MENU;

    // Callbacks vers l'UI
    private Runnable onUpdate;
    private Runnable onCombatStart;
    private Runnable onVictoire;
    private Runnable onDefaite;
    private Runnable onEtageChange;
    private Runnable onEquipeComplete;

    public GameManager() {
        database = new Database();
        database.connect();
        database.createTables();
    }

    // ─── Callbacks ────────────────────────────────────────────────────────────

    public void setOnUpdate(Runnable r)        { this.onUpdate = r; }
    public void setOnCombatStart(Runnable r)   { this.onCombatStart = r; }
    public void setOnVictoire(Runnable r)      { this.onVictoire = r; }
    public void setOnDefaite(Runnable r)       { this.onDefaite = r; }
    public void setOnEtageChange(Runnable r)   { this.onEtageChange = r; }
    public void setOnEquipeComplete(Runnable r){ this.onEquipeComplete = r; }

    private void notifier(Runnable r) { if (r != null) r.run(); }

    // ─── Démarrage ────────────────────────────────────────────────────────────

    public List<SauvegardeInfo> getSauvegardes(String nomJoueur) {
        return database.getListeSauvegardes(nomJoueur);
    }

    public void demarrerNouvellePartie(String nom) {
        this.nomJoueur = nom;
        this.equipe = new Equipe();
        this.etage = 1;
        this.idSauvegardeActive = -1;
        this.phaseActuelle = Phase.SELECT_PERSO;
        notifier(onUpdate);
    }

    public void chargerPartie(String nom, int idSauvegarde) {
        this.nomJoueur = nom;
        this.idSauvegardeActive = idSauvegarde;
        this.equipe = database.chargerEquipe(idSauvegarde, nom);
        this.etage = database.getEtageSauvegarde(idSauvegarde);
        try {
            database.supNouSav(nom, idSauvegarde);
        } catch (SQLException ignored) {}
        this.phaseActuelle = Phase.FLOOR_PREP;
        notifier(onUpdate);
    }

    public boolean ajouterPersonnage(String classeNom) {
        Personnage p = creerPersonnage(classeNom);
        if (p == null) return false;
        if (equipe.classeEstPresente(classeNom)) return false;
        equipe.ajouterMembre(p);
        // Passer en préparation une fois au moins 1 perso ajouté
        if (!equipe.getMembres().isEmpty()) {
            phaseActuelle = Phase.FLOOR_PREP;
        }
        notifier(onUpdate);
        return true;
    }

    // ─── Préparation d'étage ─────────────────────────────────────────────────

    public void lancerCombat() {
        monstresActuels = genererMonstres(etage);
        paRestants = 4;
        personnagesAyantAgi.clear();
        attaquantSelectionne = null;
        phaseActuelle = Phase.COMBAT;
        notifier(onCombatStart);
        notifier(onUpdate);
    }

    // ─── Combat : sélection ──────────────────────────────────────────────────

    public void selectionnerAttaquant(Personnage p) {
        if (!personnagesAyantAgi.contains(p)) {
            this.attaquantSelectionne = p;
            notifier(onUpdate);
        }
    }

    // ─── Combat : actions ────────────────────────────────────────────────────

    public void attaquerMonstre(Monstre cible) {
        if (attaquantSelectionne == null || cible == null) return;
        Personnage attaquant = attaquantSelectionne;

        attaquant.mettreAJourEffets();
        if (!attaquant.estVivant()) { supprimerPersonnageMort(attaquant); return; }

        attaquant.attaque1(cible);
        if (!cible.estVivant()) gererMortMonstre(cible);

        enregistrerActionCombat(attaquant);
        verifierFinCombat();
    }

    public void utiliserUltime(Personnage attaquant, Monstre cible) {
        if (attaquant == null) return;

        attaquant.mettreAJourEffets();
        if (!attaquant.estVivant()) { supprimerPersonnageMort(attaquant); return; }

        if (attaquant instanceof Pretre) {
            ((Pretre) attaquant).ulti(equipe, null);
        } else {
            if (cible == null && !(attaquant instanceof Paladin)) return;
            attaquant.ulti(cible);
            if (attaquant instanceof Paladin) equipe.deplacerMembreAuDebut(attaquant);
            if (cible != null && !cible.estVivant()) gererMortMonstre(cible);
        }

        enregistrerActionCombat(attaquant);
        verifierFinCombat();
    }

    public boolean utiliserObjet(int slotIndex, Object cible) {
        Objet objet = equipe.getInventaireCommun().getObjetDansSlot(slotIndex);
        if (objet == null) return false;
        if (cible instanceof Personnage) {
            objet.utiliser(attaquantSelectionne != null ? attaquantSelectionne : equipe.getLeader(), cible);
        } else if (cible instanceof Monstre) {
            objet.utiliser(attaquantSelectionne != null ? attaquantSelectionne : equipe.getLeader(), cible);
        } else return false;
        equipe.getInventaireCommun().placerObjetDansSlot(null, slotIndex);
        consommerPA();
        notifier(onUpdate);
        return true;
    }

    public void echangerPositions(Personnage a, Personnage b) {
        List<Personnage> membres = equipe.getMembres();
        int ia = membres.indexOf(a), ib = membres.indexOf(b);
        if (ia >= 0 && ib >= 0) {
            java.util.Collections.swap(membres, ia, ib);
            enregistrerActionCombat(a);
            verifierFinCombat();
        }
    }

    public void passerPA() {
        consommerPA();
        notifier(onUpdate);
    }

    // ─── Tour des monstres ────────────────────────────────────────────────────

    public void executerTourMonstres() {
        for (Monstre m : new ArrayList<>(monstresActuels)) {
            if (!m.estVivant()) continue;
            if (equipe.getMembres().isEmpty()) break;

            m.mettreAJourEffets();
            if (!m.estVivant()) {
                monstresActuels.remove(m);
                continue;
            }

            boolean etourdi = m.getEffetsActifs().stream()
                    .anyMatch(e -> "Étourdi".equals(e.getNom()));
            if (etourdi) {
                System.out.println(m.getNom() + " est Étourdi et ne peut pas agir !");
                continue;
            }

            Personnage cible = equipe.getLeader();
            if (cible == null || !cible.estVivant()) continue;

            m.attaquer(cible);
            if (!cible.estVivant()) {
                System.out.println(cible.getNom() + " est vaincu !");
                equipe.getMembres().remove(cible);
                if (equipe.getMembres().isEmpty()) break;
            }
        }

        // Préparer le tour du joueur
        paRestants = 4;
        personnagesAyantAgi.clear();
        attaquantSelectionne = null;

        notifier(onUpdate);
        verifierFinCombat();
    }

    // ─── Fin de combat ────────────────────────────────────────────────────────

    private void gererMortMonstre(Monstre monstre) {
        int xp = monstre.getXpDonnee();
        List<Personnage> vivants = equipe.getMembresVivants();
        if (!vivants.isEmpty()) {
            int xpParMembre = Math.max(1, xp / vivants.size());
            for (Personnage p : vivants) p.gagnerXp(xpParMembre);
        }
        equipe.getInventaireCommun().ajouterOr(monstre.getOrM());
        System.out.println("L'équipe gagne " + monstre.getOrM() + " or !");
        monstresActuels.remove(monstre);
    }

    private void verifierFinCombat() {
        if (equipe.getMembres().isEmpty()) {
            phaseActuelle = Phase.GAME_OVER;
            notifier(onDefaite);
        } else if (monstresActuels.isEmpty()) {
            System.out.println("Victoire à l'étage " + etage + " !");
            etage++;
            phaseActuelle = Phase.FLOOR_PREP;
            notifier(onVictoire);
        } else {
            notifier(onUpdate);
        }
    }

    private void enregistrerActionCombat(Personnage p) {
        if (!personnagesAyantAgi.contains(p)) personnagesAyantAgi.add(p);
        equipe.rotationApresAction(p);
        attaquantSelectionne = null;
        consommerPA();
        verifierTousOntAgi();
    }

    private void consommerPA() {
        paRestants = Math.max(0, paRestants - 1);
        if (paRestants == 0) {
            attaquantSelectionne = null;
            notifier(onUpdate);
        }
    }

    private void verifierTousOntAgi() {
        boolean tousOntAgi = equipe.getMembres().stream()
                .allMatch(p -> personnagesAyantAgi.contains(p));
        if (tousOntAgi && paRestants > 0) {
            System.out.println("Tous les personnages ont agi ce tour.");
            paRestants = 0;
        }
    }

    private void supprimerPersonnageMort(Personnage p) {
        equipe.getMembres().remove(p);
        if (attaquantSelectionne == p) attaquantSelectionne = null;
        verifierFinCombat();
    }

    // ─── Sauvegarde ──────────────────────────────────────────────────────────

    public void sauvegarder() {
        if (database == null || nomJoueur == null || nomJoueur.isEmpty()) return;
        try {
            int newId = database.createSauvegarde(nomJoueur, etage);
            if (newId != -1) {
                database.sauvegarderEquipeDb(equipe.getMembres(), newId);
                database.sauvegarderInventaireDb(equipe.getInventaireCommun(), newId);
                System.out.println("Partie sauvegardée (ID: " + newId + ")");
            }
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (database != null) database.disconnect();
    }

    // ─── Génération de monstres ──────────────────────────────────────────────

    private List<Monstre> genererMonstres(int etage) {
        List<Monstre> groupe = new ArrayList<>();
        Random r = new Random();
        int nb;

        if (etage % 10 == 0) {
            System.out.println("!!! BOSS APPARAÎT !!!");
            groupe.add(Monstre.boss(etage));
            if (etage >= 20) {
                for (int i = 0; i < Math.min(2, etage / 15); i++)
                    groupe.add(Monstre.monstreFaible(etage));
            }
            return groupe;
        }

        if (etage < 5) nb = r.nextInt(1) + 1;
        else if (etage < 15) nb = r.nextInt(2) + 2;
        else if (etage < 30) nb = r.nextInt(3) + 3;
        else nb = r.nextInt(2) + 4;

        for (int i = 0; i < nb; i++) {
            int roll = r.nextInt(100);
            Monstre m;
            if (etage <= 10) m = Monstre.monstreFaible(etage);
            else if (etage <= 20) m = roll < 80 ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
            else if (etage <= 30) m = roll < 50 ? Monstre.monstreFaible(etage) : Monstre.monstreMoyen(etage);
            else if (etage <= 40) m = roll < 20 ? Monstre.monstreFort(etage) : Monstre.monstreMoyen(etage);
            else if (etage <= 50) m = roll < 50 ? Monstre.monstreMoyen(etage) : Monstre.monstreFort(etage);
            else m = Monstre.monstreFort(etage);
            groupe.add(m);
        }
        return groupe;
    }

    // ─── Événements ──────────────────────────────────────────────────────────

    public boolean doitDeclencherEvenement() {
        return etage > 1 && etage % 10 == 5;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public Equipe getEquipe()                         { return equipe; }
    public int getEtage()                             { return etage; }
    public String getNomJoueur()                      { return nomJoueur; }
    public List<Monstre> getMonstres()                { return monstresActuels; }
    public int getPaRestants()                        { return paRestants; }
    public Personnage getAttaquantSelectionne()       { return attaquantSelectionne; }
    public List<Personnage> getPersonnagesAyantAgi()  { return personnagesAyantAgi; }
    public Phase getPhase()                           { return phaseActuelle; }
    public Database getDatabase()                     { return database; }
    public int getIdSauvegardeActive()                { return idSauvegardeActive; }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Personnage creerPersonnage(String classe) {
        return switch (classe) {
            case "Assassin"    -> new Assassin();
            case "Barbare"     -> new Barbare();
            case "Chevalier"   -> new Chevalier();
            case "Enchanteur"  -> new Enchanteur();
            case "Necromancien"-> new Necromancien();
            case "Paladin"     -> new Paladin();
            case "Pretre"      -> new Pretre();
            case "Pyromancien" -> new Pyromancien();
            default -> null;
        };
    }
}
