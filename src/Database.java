import java.sql.*;
import java.util.ArrayList;
import personnage.Personnage;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:savegame.db";
    private Connection conn;

    public void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connexion SQLite réussie.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur à la fermeture : " + e.getMessage());
        }
    }

    public void createTables() {
        String createSauvegarde = """
            CREATE TABLE IF NOT EXISTS Sauvegarde (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom_sauvegarde TEXT,
                date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
                joueur TEXT,
                numEtage INTEGER
            );
        """;

        String createPersonnage = """
            CREATE TABLE IF NOT EXISTS Personnage (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT,
                sante TEXT,
                pv INTEGER,
                pv_max INTEGER,
                force INTEGER,
                niveau INTEGER,
                xp INTEGER,
                xp_pour_niveau_suivant INTEGER,
                type TEXT,
                position INTEGER,
                sauvegarde_id INTEGER,
                FOREIGN KEY (sauvegarde_id) REFERENCES Sauvegarde(id) ON DELETE CASCADE
            );
        """;

        String createClassement = """
            CREATE TABLE IF NOT EXISTS Classement (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                joueur TEXT,
                score INTEGER,
                niveau_atteint INTEGER,
                date DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSauvegarde);
            stmt.execute(createPersonnage);
            stmt.execute(createClassement);
            System.out.println("Tables créées ou déjà existantes.");
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }

    public void sauvegarderPersonnage(Personnage p, int sauvegardeId, int position) {
        if (conn == null) return;

        String sql = """
            INSERT INTO Personnage (nom, sante, pv, pv_max, force, niveau, xp, xp_pour_niveau_suivant, type, position, sauvegarde_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNom());
            pstmt.setString(2, p.getSante());
            pstmt.setInt(3, p.getPv());
            pstmt.setInt(4, p.getPvMax());
            pstmt.setInt(5, p.getForce());
            pstmt.setInt(6, p.getNiveau());
            pstmt.setInt(7, p.getXp());
            pstmt.setInt(8, p.getXpPourNiveauSuivant());
            pstmt.setString(9, p.getType());
            pstmt.setInt(10, position);
            pstmt.setInt(11, sauvegardeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du personnage : " + e.getMessage());
        }
    }

    public void sauvegarderEquipe(ArrayList<Personnage> equipe, int sauvegardeId) {
        if (conn == null) return;
        for (int i = 0; i < equipe.size() && i < 4; i++) {
            sauvegarderPersonnage(equipe.get(i), sauvegardeId, i + 1);
        }
    }

    public void recupererPersonnage(int sauvegardeId) {
        if (conn == null) return;

        String sql = "SELECT * FROM Personnage WHERE sauvegarde_id = ? ORDER BY position";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sauvegardeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Nom: " + rs.getString("nom"));
                    System.out.println("Sante: " + rs.getString("sante"));
                    System.out.println("Pv: " + rs.getInt("pv"));
                    System.out.println("Pv Max: " + rs.getInt("pv_max"));
                    System.out.println("Force: " + rs.getInt("force"));
                    System.out.println("Niveau: " + rs.getInt("niveau"));
                    System.out.println("XP: " + rs.getInt("xp"));
                    System.out.println("XP pour Niveau Suivant: " + rs.getInt("xp_pour_niveau_suivant"));
                    System.out.println("Type: " + rs.getString("type"));
                    System.out.println("Position: " + rs.getInt("position"));
                    System.out.println("----------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération : " + e.getMessage());
        }
    }

    public void supprimerPersonnage(String nom) {
        if (conn == null) return;

        String sql = "DELETE FROM Personnage WHERE nom = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.executeUpdate();
            System.out.println("Personnage supprimé : " + nom);
        } catch (SQLException e) {
            System.err.println("Erreur de suppression : " + e.getMessage());
        }
    }

    public int nbSauv(String joueur) throws SQLException {
        if (conn == null) return 0;

        String sql = "SELECT COUNT(*) FROM Sauvegarde WHERE joueur = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void supAncSauv(String joueur) throws SQLException {
        if (conn == null) return;

        String sql = """
            DELETE FROM Sauvegarde
            WHERE id = (
                SELECT id FROM Sauvegarde
                WHERE joueur = ?
                ORDER BY date_creation ASC
                LIMIT 1
            );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            stmt.executeUpdate();
        }
    }

    public void supNouSav(int idSauvegarde, String joueur) throws SQLException {
        if (conn == null) return;

        String sql = """
            DELETE FROM Sauvegarde
            WHERE joueur = ? AND date_creation > (
                SELECT date_creation FROM Sauvegarde WHERE id = ?
            );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joueur);
            stmt.setInt(2, idSauvegarde);
            stmt.executeUpdate();
        }
    }

    public int createSauvegarde(String joueur, int numEtage) throws SQLException {
        if (conn == null) return -1;

        if (nbSauv(joueur) >= 5) {
            supAncSauv(joueur);
        }

        String sql = "INSERT INTO Sauvegarde (joueur, numEtage) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, joueur);
            stmt.setInt(2, numEtage);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // ID de la nouvelle sauvegarde
                }
            }
        }

        return -1;
    }
}
