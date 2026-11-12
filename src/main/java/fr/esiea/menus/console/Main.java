package fr.esiea.menus.console;

import fr.esiea.menus.persistance.IngredientRepositoryJdbc;
import fr.esiea.menus.persistance.IngredientRepositoryMemoire;
import fr.esiea.menus.persistance.MenuRepositoryJdbc;
import fr.esiea.menus.persistance.MenuRepositoryMemoire;
import fr.esiea.menus.persistance.PrixDeVenteRepositoryJdbc;
import fr.esiea.menus.persistance.PrixDeVenteRepositoryMemoire;
import fr.esiea.menus.persistance.RecetteRepositoryJdbc;
import fr.esiea.menus.persistance.RecetteRepositoryMemoire;
import fr.esiea.menus.persistance.SchemaBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

/** Point d'entrée : la connexion se crée ici et se passe aux constructeurs des Repository. */
public final class Main {

    private static final String URL_PAR_DEFAUT = "jdbc:postgresql://localhost:5432/menus";
    private static final String UTILISATEUR_PAR_DEFAUT = "menus";
    private static final String MOT_DE_PASSE_PAR_DEFAUT = "menus";
    private static final String OPTION_MEMOIRE = "--memoire";

    private Main() {
    }

    public static void main(String[] arguments) {
        boolean memoireDemandee = Arrays.asList(arguments).contains(OPTION_MEMOIRE);
        Application application = (memoireDemandee ? Optional.<Application>empty() : avecPostgres())
                .orElseGet(Main::enMemoire);
        if (DonneesDeDemo.chargerSiVide(application)) {
            System.out.println("Données de démonstration chargées.");
        }
        new MenuConsole(application, new Scanner(System.in), System.out, LocalDate::now).boucle();
    }

    private static Optional<Application> avecPostgres() {
        String url = variable("MENUS_DB_URL", URL_PAR_DEFAUT);
        try {
            Connection connexion = DriverManager.getConnection(url,
                    variable("MENUS_DB_USER", UTILISATEUR_PAR_DEFAUT),
                    variable("MENUS_DB_PASSWORD", MOT_DE_PASSE_PAR_DEFAUT));
            SchemaBase.creer(connexion);
            System.out.println("Base PostgreSQL branchée : " + url);
            IngredientRepositoryJdbc ingredients = new IngredientRepositoryJdbc(connexion);
            RecetteRepositoryJdbc recettes = new RecetteRepositoryJdbc(connexion, ingredients);
            return Optional.of(Application.assembler(ingredients, recettes,
                    new MenuRepositoryJdbc(connexion, recettes), new PrixDeVenteRepositoryJdbc(connexion)));
        } catch (SQLException e) {
            System.out.println("Base PostgreSQL injoignable (" + e.getMessage() + ") : les données restent en mémoire.");
            return Optional.empty();
        }
    }

    private static Application enMemoire() {
        return Application.assembler(new IngredientRepositoryMemoire(), new RecetteRepositoryMemoire(),
                new MenuRepositoryMemoire(), new PrixDeVenteRepositoryMemoire());
    }

    private static String variable(String nom, String parDefaut) {
        String valeur = System.getenv(nom);
        return valeur == null || valeur.isBlank() ? parDefaut : valeur;
    }
}
