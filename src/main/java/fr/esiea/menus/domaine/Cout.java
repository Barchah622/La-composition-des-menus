package fr.esiea.menus.domaine;

import java.util.Set;
import java.util.TreeSet;

/** Un montant en euros, avec les ingrédients qu'on n'a pas pu chiffrer faute de prix (RM3). */
public record Cout(double montant, Set<String> ingredientsSansPrix) {

    public static final Cout NUL = new Cout(0, Set.of());

    public Cout {
        ingredientsSansPrix = Set.copyOf(ingredientsSansPrix);
    }

    public static Cout sansPrix(String nomIngredient) {
        return new Cout(0, Set.of(nomIngredient));
    }

    public boolean complet() {
        return ingredientsSansPrix.isEmpty();
    }

    public Cout plus(Cout autre) {
        Set<String> manquants = new TreeSet<>(ingredientsSansPrix);
        manquants.addAll(autre.ingredientsSansPrix);
        return new Cout(montant + autre.montant, manquants);
    }

    public Cout fois(double facteur) {
        return new Cout(montant * facteur, ingredientsSansPrix);
    }
}
