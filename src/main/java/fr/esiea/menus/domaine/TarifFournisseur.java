package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Objects;

/** Un nouveau prix transmis par le Système fournisseur, avec sa date d'application (UC3). */
public record TarifFournisseur(String referenceIngredient, double prix, UniteAchat unite, LocalDate dateApplication) {

    public TarifFournisseur {
        Objects.requireNonNull(referenceIngredient, "référence de l'ingrédient");
        Objects.requireNonNull(unite, "unité");
        Objects.requireNonNull(dateApplication, "date d'application");
    }
}
