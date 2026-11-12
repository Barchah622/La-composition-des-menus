package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Objects;

/** Un prix d'achat valable à partir d'une date (RM13). */
public record PrixIngredient(double valeur, LocalDate dateApplication) {

    public PrixIngredient {
        Objects.requireNonNull(dateApplication, "date d'application");
        if (Double.isNaN(valeur) || valeur <= 0) {
            throw new RegleMetierException("RM13", "le prix d'achat doit être strictement positif (reçu : " + valeur + ")");
        }
    }
}
