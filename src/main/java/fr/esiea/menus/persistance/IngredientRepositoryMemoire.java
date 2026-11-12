package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Ingredient;
import fr.esiea.menus.domaine.IngredientRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Ingrédients gardés en mémoire : pour les tests, et pour démarrer sans base. */
public class IngredientRepositoryMemoire implements IngredientRepository {

    private final Map<String, Ingredient> parNom = new LinkedHashMap<>();

    @Override
    public void enregistrer(Ingredient ingredient) {
        parNom.put(ingredient.nom().toLowerCase(Locale.ROOT), ingredient);
    }

    @Override
    public Optional<Ingredient> trouverParNom(String nom) {
        return Optional.ofNullable(parNom.get(nom.toLowerCase(Locale.ROOT)));
    }

    @Override
    public List<Ingredient> tous() {
        return new ArrayList<>(parNom.values());
    }
}
