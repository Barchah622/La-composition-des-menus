package fr.esiea.menus.domaine;

import java.util.List;

/** Range les ingrédients et refuse un nom déjà pris (RM17). */
public class CatalogueIngredients {

    private final IngredientRepository depot;

    public CatalogueIngredients(IngredientRepository depot) {
        this.depot = depot;
    }

    public void ajouter(Ingredient ingredient) {
        if (depot.trouverParNom(ingredient.nom()).isPresent()) {
            throw new RegleMetierException("RM17", "un ingrédient s'appelle déjà « " + ingredient.nom() + " »");
        }
        depot.enregistrer(ingredient);
    }

    public void mettreAJour(Ingredient ingredient) {
        depot.enregistrer(ingredient);
    }

    public Ingredient trouver(String nom) {
        return depot.trouverParNom(nom.trim()).orElseThrow(() -> new ElementInconnuException("ingrédient", nom));
    }

    public List<Ingredient> tous() {
        return depot.tous();
    }
}
