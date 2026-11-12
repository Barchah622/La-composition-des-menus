package fr.esiea.menus.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Petites aides JDBC partagées par les Repository. */
final class Jdbc {

    @FunctionalInterface
    interface Ligne<T> {
        T lire(ResultSet resultat) throws SQLException;
    }

    private Jdbc() {
    }

    static int miseAJour(Connection connexion, String sql, Object... parametres) {
        try (PreparedStatement requete = connexion.prepareStatement(sql)) {
            lier(requete, parametres);
            return requete.executeUpdate();
        } catch (SQLException e) {
            throw new PersistanceException("échec de « " + sql + " »", e);
        }
    }

    static <T> List<T> lister(Connection connexion, String sql, Ligne<T> ligne, Object... parametres) {
        try (PreparedStatement requete = connexion.prepareStatement(sql)) {
            lier(requete, parametres);
            List<T> lignes = new ArrayList<>();
            try (ResultSet resultat = requete.executeQuery()) {
                while (resultat.next()) {
                    lignes.add(ligne.lire(resultat));
                }
            }
            return lignes;
        } catch (SQLException e) {
            throw new PersistanceException("échec de « " + sql + " »", e);
        }
    }

    private static void lier(PreparedStatement requete, Object[] parametres) throws SQLException {
        for (int i = 0; i < parametres.length; i++) {
            if (parametres[i] == null) {
                requete.setNull(i + 1, Types.VARCHAR);
            } else {
                requete.setObject(i + 1, parametres[i]);
            }
        }
    }

    /** Crée les tables de schema.sql si elles n'existent pas encore. */
    static void creerSchema(Connection connexion) {
        try (InputStream flux = Jdbc.class.getResourceAsStream("/schema.sql");
             Statement instruction = connexion.createStatement()) {
            String script = new String(flux.readAllBytes(), StandardCharsets.UTF_8);
            for (String commande : sansCommentaires(script).split(";")) {
                if (!commande.isBlank()) {
                    instruction.execute(commande);
                }
            }
        } catch (IOException | SQLException e) {
            throw new PersistanceException("création du schéma impossible", e);
        }
    }

    private static String sansCommentaires(String script) {
        return script.replaceAll("--[^\\n]*", "");
    }
}
