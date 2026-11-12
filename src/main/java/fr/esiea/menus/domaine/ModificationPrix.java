package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Objects;

/** Un nouveau prix saisi à la main par le Chef, avec la référence de son justificatif (UC7, RM25). */
public record ModificationPrix(String nomIngredient, double nouveauPrix, LocalDate dateApplication,
                               String referenceJustificatif) {

    public ModificationPrix {
        Objects.requireNonNull(nomIngredient, "ingrédient");
        Objects.requireNonNull(dateApplication, "date d'application");
        if (referenceJustificatif == null || referenceJustificatif.isBlank()) {
            throw new RegleMetierException("RM25", "un changement de prix saisi à la main exige un justificatif (facture, tarif)");
        }
    }
}
