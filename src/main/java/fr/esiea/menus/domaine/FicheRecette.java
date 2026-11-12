package fr.esiea.menus.domaine;

import java.util.List;
import java.util.Set;

/** Ce que le Chef voit en validant une recette : son coût et ses allergènes, à tous les niveaux (UC2, étape 8). */
public record FicheRecette(Recette recette, Cout cout, Set<Allergene> allergenes, List<String> ingredientsSansAllergenes) {

    public FicheRecette {
        allergenes = Set.copyOf(allergenes);
        ingredientsSansAllergenes = List.copyOf(ingredientsSansAllergenes);
    }

    /** UC2 8e : la recette est enregistrée, mais son coût ou ses allergènes sont incomplets. */
    public boolean complete() {
        return cout.complet() && ingredientsSansAllergenes.isEmpty();
    }
}
