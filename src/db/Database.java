package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jeu.Equipe; 
import personnage.*; 
import inventaire.Inventaire; 
import inventaire.Objet;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:savegame.db";
    private Connection conn;

    public void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion DB: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) { 
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur à la fermeture DB: " + e.getMessage());
        }
    }

    public void createTables() {
        if (conn == null) { System.err.println("DB non connectée, impossible de créer les tables."); return; }
        String createSauvegarde = """
            CREATE TABLE IF NOT EXISTS Sauvegarde (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom_joueur TEXT NOT NULL,
                num_etage INTEGER NOT NULL,
                date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createPersonnage = """
            CREATE TABLE IF NOT EXISTS PersonnageSauvegarde (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom_personnage TEXT NOT NULL,
                classe_personnage TEXT NOT NULL, /* Important pour recréer le bon type d'objet */
                sante_etat TEXT, /* 'en pleine forme', 'malade', etc. */
                pv INTEGER NOT NULL,
                pv_max INTEGER NOT NULL,
                force_base INTEGER NOT NULL, /* Force avant modificateurs d'effets temporaires */
                defense_base_specifique INTEGER DEFAULT 0, /* Défense de base du type (pour Tank) */
                stat_specifique_valeur INTEGER DEFAULT 0, /* Pénétration, Soin, Malédiction */
                niveau INTEGER NOT NULL,
                xp INTEGER NOT NULL,
                xp_pour_niveau_suivant INTEGER NOT NULL,
                comp_lvl INTEGER DEFAULT 1,
                ult_lvl INTEGER DEFAULT 0,
                position_equipe INTEGER NOT NULL, /* 1 à 4 */
                sauvegarde_id INTEGER NOT NULL,
                FOREIGN KEY (sauvegarde_id) REFERENCES Sauvegarde(id) ON DELETE CASCADE
            );
        """;

        String createInventaireEquipe = """
            CREATE TABLE IF NOT EXISTS InventaireEquipe (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sauvegarde_id INTEGER NOT NULL UNIQUE, /* Chaque sauvegarde a un seul inventaire */
                or_equipe INTEGER NOT NULL DEFAULT 0,
                slot1_objet_nom TEXT, /* Nom de l'objet dans le slot, ou NULL si vide */
                slot2_objet_nom TEXT,
                slot3_objet_nom TEXT,
                slot4_objet_nom TEXT,
                slot5_objet_nom TEXT,
                slot6_objet_nom TEXT,
                FOREIGN KEY (sauvegarde_id) REFERENCES Sauvegarde(id) ON DELETE CASCADE
            );
        """;


        String createClassement = """
            CREATE TABLE IF NOT EXISTS Classement (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                joueur TEXT,
                score INTEGER, /* À définir comment le score est calculé */
                niveau_atteint INTEGER,
                date DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSauvegarde);
            stmt.execute(createPersonnage);
            stmt.execute(createInventaireEquipe);
            stmt.execute(createClassement);
        } catch (SQLException e) {
            System.err.println("Erreur SQL création tables : " + e.getMessage());
        }
    }

    // --- Gestion des Sauvegardes ---

    public int createSauvegarde(String nomJoueur, int numEtage) throws SQLException {
        if (conn == null) return -1;
        if (nbSauv(nomJoueur) >= 5) {
            supAncSauv(nomJoueur); 
        }

        String sql = "INSERT INTO Sauvegarde (nom_joueur, num_etage) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nomJoueur);
            stmt.setInt(2, numEtage);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void sauvegarderPersonnageDb(Personnage p, int sauvegardeId, int position) {
        if (conn == null || p == null) return;
        String sql = """
            INSERT INTO PersonnageSauvegarde (nom_personnage, classe_personnage, sante_etat, pv, pv_max, 
            force_base, defense_base_specifique, stat_specifique_valeur, niveau, xp, xp_pour_niveau_suivant, comp_lvl, ult_lvl, position_equipe, sauvegarde_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNom());
            pstmt.setString(2, p.getClass().getSimpleName());
            pstmt.setString(3, p.getSante());
            pstmt.setInt(4, p.getPv());
            pstmt.setInt(5, p.getPvMax());
            pstmt.setInt(6, p.getForceBase());
            pstmt.setInt(7, p.getDefenseBaseSpecifiqueType());
            
            int statSpecifique = 0;
            if (p instanceof Dps) statSpecifique = ((Dps)p).getPenetration();
            else if (p instanceof Mage) statSpecifique = ((Mage)p).getMalediction();
            else if (p instanceof Support) statSpecifique = ((Support)p).getSoin();
            pstmt.setInt(8, statSpecifique);

            pstmt.setInt(9, p.getNiveau());
            pstmt.setInt(10, p.getXp());
            pstmt.setInt(11, p.getXpPourNiveauSuivant());
            pstmt.setInt(12, p.getCompLvl());
            pstmt.setInt(13, p.getUltLvl());
            pstmt.setInt(14, position);
            pstmt.setInt(15, sauvegardeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde personnage " + p.getNom() + " : " + e.getMessage());
        }
    }
    
    public void sauvegarderEquipeDb(List<Personnage> equipe, int sauvegardeId) {
        if (conn == null || equipe == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM PersonnageSauvegarde WHERE sauvegarde_id = ?")) {
            pstmt.setInt(1, sauvegardeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression anciens personnages pour sauvegarde " + sauvegardeId + ": " + e.getMessage());
        }

        for (int i = 0; i < equipe.size(); i++) {
            sauvegarderPersonnageDb(equipe.get(i), sauvegardeId, i + 1);
        }
    }

    public void sauvegarderInventaireDb(Inventaire inventaire, int sauvegardeId) {
        if (conn == null || inventaire == null) return;
        try (PreparedStatement delStmt = conn.prepareStatement("DELETE FROM InventaireEquipe WHERE sauvegarde_id = ?")) {
            delStmt.setInt(1, sauvegardeId);
            delStmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("Erreur suppression ancien inventaire pour sauvegarde " + sauvegardeId + ": " + e.getMessage());
        }

        String sql = """
            INSERT INTO InventaireEquipe (sauvegarde_id, or_equipe, slot1_objet_nom, slot2_objet_nom, slot3_objet_nom, slot4_objet_nom, slot5_objet_nom, slot6_objet_nom)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sauvegardeId);
            pstmt.setInt(2, inventaire.getOr());
            for (int i = 0; i < 6; i++) { 
                Objet obj = inventaire.getObjetDansSlot(i);
                pstmt.setString(i + 3, (obj != null) ? obj.getNom() : null);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde inventaire pour sauvegarde " + sauvegardeId + ": " + e.getMessage());
        }
    }

    public List<SauvegardeInfo> getListeSauvegardes(String nomJoueur) {
        List<SauvegardeInfo> sauvegardes = new ArrayList<>();
        if (conn == null) return sauvegardes;
        String sql = "SELECT id, num_etage, date_creation FROM Sauvegarde WHERE nom_joueur = ? ORDER BY date_creation DESC LIMIT 5";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomJoueur);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sauvegardes.add(new SauvegardeInfo(rs.getInt("id"), nomJoueur, rs.getInt("num_etage"), rs.getString("date_creation")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération liste sauvegardes: " + e.getMessage());
        }
        return sauvegardes;
    }

    // --- Chargement d'une Partie ---
    public Equipe chargerEquipe(int sauvegardeId, Scanner scannerForNewPerso, String nomJoueur) {
        Equipe equipeChargee = new Equipe();
        if (conn == null) return equipeChargee;

        String sqlPersos = "SELECT * FROM PersonnageSauvegarde WHERE sauvegarde_id = ? ORDER BY position_equipe ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlPersos)) {
            pstmt.setInt(1, sauvegardeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String nomPerso = rs.getString("nom_personnage");
                String classePerso = rs.getString("classe_personnage");
                String santeEtat = rs.getString("sante_etat");
                int pv = rs.getInt("pv");
                int pvMax = rs.getInt("pv_max");
                int forceBase = rs.getInt("force_base");
                int defenseBaseSpec = rs.getInt("defense_base_specifique");
                int statSpecifiqueVal = rs.getInt("stat_specifique_valeur");
                int niveau = rs.getInt("niveau");
                int xp = rs.getInt("xp");
                int xpNext = rs.getInt("xp_pour_niveau_suivant");
                int compLvl = rs.getInt("comp_lvl");
                int ultLvl = rs.getInt("ult_lvl");

                Personnage p = null;
                switch (classePerso) {
                    case "Assassin": p = new Assassin(); break;
                    case "Barbare": p = new Barbare(); break;
                    case "Chevalier": p = new Chevalier(); break;
                    case "Enchanteur": p = new Enchanteur(); break;
                    case "Necromancien": p = new Necromancien(); break;
                    case "Paladin": p = new Paladin(); break;
                    case "Pretre": p = new Pretre(); break;
                    case "Pyromancien": p = new Pyromancien(); break;
                    default: System.err.println("Classe de personnage inconnue lors du chargement: " + classePerso); continue;
                }
                

                p.setNom(nomPerso); 
                p.setSante(santeEtat); 
                p.setPvMaxDirect(pvMax); 
                p.setPv(pv);
                p.setForceBaseDirect(forceBase); 
                p.setDefenseBaseSpecifiqueTypeDirect(defenseBaseSpec); 
                
                if (p instanceof Dps) ((Dps)p).setPenetration(statSpecifiqueVal);
                else if (p instanceof Mage) ((Mage)p).setMalediction(statSpecifiqueVal);
                else if (p instanceof Support) ((Support)p).setSoin(statSpecifiqueVal);

                p.setNiveauLoaded(niveau); 
                p.setXpLoaded(xp, xpNext);
                p.setCompLvlLoaded(compLvl);
                p.setUltLvlLoaded(ultLvl);
                
                equipeChargee.ajouterMembre(p); 
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement personnages: " + e.getMessage());
        }
        
        // Charger l'inventaire
        String sqlInventaire = "SELECT or_equipe, slot1_objet_nom, slot2_objet_nom, slot3_objet_nom, slot4_objet_nom, slot5_objet_nom, slot6_objet_nom FROM InventaireEquipe WHERE sauvegarde_id = ?";
        try(PreparedStatement pstmtInv = conn.prepareStatement(sqlInventaire)) {
            pstmtInv.setInt(1, sauvegardeId);
            ResultSet rsInv = pstmtInv.executeQuery();
            if (rsInv.next()) {
                equipeChargee.getInventaireCommun().setOr(rsInv.getInt("or_equipe"));
                for (int i = 1; i <= 6; i++) {
                    String nomObjet = rsInv.getString("slot" + i + "_objet_nom");
                    if (nomObjet != null && !nomObjet.isEmpty()) {
                        Objet obj = Objet.creerObjetParNom(nomObjet); 
                        if (obj != null) {
                            equipeChargee.getInventaireCommun().placerObjetDansSlot(obj, i - 1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement inventaire: " + e.getMessage());
        }
        return equipeChargee;
    }
    
    public int getEtageSauvegarde(int sauvegardeId) {
        if (conn == null) return 1; 
        String sql = "SELECT num_etage FROM Sauvegarde WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sauvegardeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_etage");
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération étage sauvegarde: " + e.getMessage());
        }
        return 1; 
    }


    public int nbSauv(String joueur) throws SQLException {
        if (conn == null) return 0;
        String sql = "SELECT COUNT(*) FROM Sauvegarde WHERE nom_joueur = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public void supAncSauv(String joueur) throws SQLException {
        if (conn == null) return;
        String sql = """
            DELETE FROM Sauvegarde
            WHERE id = (
                SELECT id FROM Sauvegarde
                WHERE nom_joueur = ?
                ORDER BY date_creation ASC
                LIMIT 1
            );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            stmt.executeUpdate();
            System.out.println("Ancienne sauvegarde pour " + joueur + " supprimée.");
        }
    }

    public void supNouSav(String joueur, int idSauvegardeConservee) throws SQLException {
        if (conn == null) return;
        String sql = """
            DELETE FROM Sauvegarde
            WHERE nom_joueur = ? AND date_creation > (
                SELECT date_creation FROM Sauvegarde WHERE id = ?
            );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            stmt.setInt(2, idSauvegardeConservee);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(rowsAffected + " sauvegarde(s) plus récente(s) supprimée(s) pour " + joueur + ".");
            }
        }
    }
    
    public static class SauvegardeInfo {
        public final int id;
        public final String nomJoueur;
        public final int numEtage;
        public final String dateCreation;

        public SauvegardeInfo(int id, String nomJoueur, int numEtage, String dateCreation) {
            this.id = id;
            this.nomJoueur = nomJoueur;
            this.numEtage = numEtage;
            this.dateCreation = dateCreation;
        }
    }
}