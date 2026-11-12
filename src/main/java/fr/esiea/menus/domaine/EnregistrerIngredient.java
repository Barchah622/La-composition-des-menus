package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Set;

/**
 * UC1 — Enregistrer un ingrédient.
 * Porteuse : Manele HSSINI. Règles : RM13, RM14, RM15, RM17, RM18.
 */
public class EnregistrerIngredient {

    private final CatalogueIngredients catalogue;

    public EnregistrerIngredient(CatalogueIngredients catalogue) {
        this.catalogue = catalogue;
    }

    /**
     * @param prix       null si le prix n'est pas encore connu (3a : « prix manquant »)
     * @param allergenes null si la fiche n'est pas arrivée (5a : « à compléter »), vide pour « aucun allergène »
     */
    public Ingredient enregistrer(String nom, UniteAchat unite, boolean frais, Double prix, LocalDate dateDuPrix,
                                  Set<Allergene> allergenes) {
        Ingredient ingredient = new Ingredient(nom, unite, frais);
        if (prix != null) {
            ingredient.appliquerPrix(new PrixIngredient(prix, dateDuPrix));
        }
        if (allergenes != null) {
            ingredient.renseignerAllergenes(allergenes);
        }
        catalogue.ajouter(ingredient);
        return ingredient;
    }
}
