package fr.esiea.menus.domaine;

import java.util.List;
import java.util.Optional;

/** Le contrat de stockage des ingrédients : le domaine ne connaît que cette interface (DIP). */
public interface IngredientRepository {

    void enregistrer(Ingredient ingredient);

    Optional<Ingredient> trouverParNom(String nom);

    List<Ingredient> tous();
}
