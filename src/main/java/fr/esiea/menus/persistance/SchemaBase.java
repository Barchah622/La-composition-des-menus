package fr.esiea.menus.persistance;

import java.sql.Connection;

/** Prépare la base au démarrage, à partir de schema.sql. */
public final class SchemaBase {

    private SchemaBase() {
    }

    public static void creer(Connection connexion) {
        Jdbc.creerSchema(connexion);
    }
}
