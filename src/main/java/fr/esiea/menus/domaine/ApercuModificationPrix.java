package fr.esiea.menus.domaine;

import java.util.Optional;

/** Ce que le Chef voit avant de confirmer un nouveau prix (UC7, étape 5). */
public record ApercuModificationPrix(String ingredient, Optional<PrixIngredient> ancienPrix, PrixIngredient nouveauPrix,
                                     ImpactPrix impact, boolean confirmationRequise) {

    public Optional<Double> ecart() {
        return ancienPrix.map(ancien -> nouveauPrix.valeur() - ancien.valeur());
    }
}
