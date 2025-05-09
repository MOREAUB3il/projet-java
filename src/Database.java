import java.sql.*;

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
        String sql = "CREATE TABLE IF NOT EXISTS joueur (" +
                     "id INTEGER PRIMARY KEY," +
                     "nom TEXT," +
                     "sante TEXT," +
                     "pv INTEGER," +
                     "pvMax INTEGER," +
                     "force INTEGER," +
                     "niveau INTEGER," +
                     "xp INTEGER," +
                     "xpPourNiveauSuivant INTEGER," +
                     "type TEXT," +
                     ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tables créées ou déjà existantes.");
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }

    public void sauvegarderJoueur(String nom, int niveau, int pv, int pvMax, int force, String sante, int xp, int xpPourNiveauSuivant, String type) {
        String sql = "INSERT INTO joueur (nom, sante, pv, pvMax, force, niveau, xp, xpPourNiveauuivant, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
					 "ON CONFLICT(nom) DO UPDATE SET " +
					 "sante = excluded.sante, " +
					 "pv = excluded.pv, " +
					 "pvMax = excluded.pvMax, " +
					 "force = excluded.force, " +
					 "niveau = excluded.niveau, " +
					 "xp = excluded.xp, " +
					 "xpPourNiveauSuivant = excluded.xpPourNiveauSuivant, " +
					 "type = excluded.type;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, sante);
            pstmt.setInt(3, pv);
            pstmt.setInt(4, pvMax);
            pstmt.setInt(5, force);
            pstmt.setInt(6, niveau);
            pstmt.setInt(7, xp);
            pstmt.setInt(8, xpPourNiveauSuivant);
            pstmt.setString(9, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
        }
    }
}
