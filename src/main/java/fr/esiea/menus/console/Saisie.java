package fr.esiea.menus.console;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/** Lit ce que tape l'utilisateur et redemande tant que la saisie est absurde. */
class Saisie {

    private static final DateTimeFormatter FORMAT_FRANCAIS = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;
    private final PrintStream sortie;

    Saisie(Scanner scanner, PrintStream sortie) {
        this.scanner = scanner;
        this.sortie = sortie;
    }

    String texte(String question) {
        sortie.print(question + " : ");
        if (!scanner.hasNextLine()) {
            throw new FinDeSaisie();
        }
        return scanner.nextLine().trim();
    }

    String texteObligatoire(String question) {
        while (true) {
            String reponse = texte(question);
            if (!reponse.isEmpty()) {
                return reponse;
            }
            sortie.println("  Une réponse est attendue.");
        }
    }

    double nombre(String question) {
        while (true) {
            Double valeur = nombreOuVide(question);
            if (valeur != null) {
                return valeur;
            }
            sortie.println("  Un nombre est attendu.");
        }
    }

    /** Renvoie null si l'utilisateur ne tape rien. */
    Double nombreOuVide(String question) {
        while (true) {
            String reponse = texte(question);
            if (reponse.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(reponse.replace(',', '.'));
            } catch (NumberFormatException e) {
                sortie.println("  « " + reponse + " » n'est pas un nombre, recommencez.");
            }
        }
    }

    int entier(String question) {
        while (true) {
            String reponse = texte(question);
            try {
                return Integer.parseInt(reponse);
            } catch (NumberFormatException e) {
                sortie.println("  « " + reponse + " » n'est pas un nombre entier, recommencez.");
            }
        }
    }

    boolean ouiNon(String question) {
        return texte(question + " (o/n)").toLowerCase().startsWith("o");
    }

    LocalDate date(String question, LocalDate parDefaut) {
        while (true) {
            String reponse = texte(question + " [vide = " + parDefaut.format(FORMAT_FRANCAIS) + "]");
            if (reponse.isEmpty()) {
                return parDefaut;
            }
            try {
                return reponse.contains("/") ? LocalDate.parse(reponse, FORMAT_FRANCAIS) : LocalDate.parse(reponse);
            } catch (DateTimeParseException e) {
                sortie.println("  Date attendue au format jj/mm/aaaa.");
            }
        }
    }

    <T> T choisir(String question, List<T> options, Function<T, String> libelle) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("rien à choisir pour l'instant");
        }
        for (int i = 0; i < options.size(); i++) {
            sortie.println("   " + (i + 1) + ". " + libelle.apply(options.get(i)));
        }
        while (true) {
            int choix = entier(question);
            if (choix >= 1 && choix <= options.size()) {
                return options.get(choix - 1);
            }
            sortie.println("  Choisissez entre 1 et " + options.size() + ".");
        }
    }
}
