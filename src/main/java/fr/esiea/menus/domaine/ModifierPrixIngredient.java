package fr.esiea.menus.domaine;

import java.util.Optional;

/**
 * UC7 — Modifier le prix d'un ingrédient (saisie manuelle par le Chef).
 * Porteuse : Tania HUSSAIN. Règles : RM13, RM14, RM15, RM16, RM25, RM26.
 */
public class ModifierPrixIngredient {

    private final CatalogueIngredients ingredients;
    private final CatalogueRecettes recettes;
    private final MenuRepository menus;
    private final ControleurVariationPrix controleur = new ControleurVariationPrix();
    private final ImpactChangementPrix impact = new ImpactChangementPrix();

    public ModifierPrixIngredient(CatalogueIngredients ingredients, CatalogueRecettes recettes, MenuRepository menus) {
        this.ingredients = ingredients;
        this.recettes = recettes;
        this.menus = menus;
    }

    public ApercuModificationPrix apercu(ModificationPrix modification) {
        Ingredient ingredient = ingredients.trouver(modification.nomIngredient());
        PrixIngredient nouveau = new PrixIngredient(modification.nouveauPrix(), modification.dateApplication());
        ingredient.verifierNouveauPrix(nouveau);
        Optional<PrixIngredient> ancien = ingredient.dernierPrix();
        boolean confirmationRequise = ancien.isPresent()
                && controleur.exigeConfirmation(ancien.get().valeur(), nouveau.valeur());
        return new ApercuModificationPrix(ingredient.nom(), ancien, nouveau,
                impact.aRecalculer(ingredient, recettes.toutes(), menus.tous()), confirmationRequise);
    }

    /** @param variationVerifiee vrai si le Chef a ressaisi le prix pour confirmer un écart de plus de 50 % (RM26) */
    public ImpactPrix appliquer(ModificationPrix modification, boolean variationVerifiee) {
        ApercuModificationPrix apercu = apercu(modification);
        if (apercu.confirmationRequise() && !variationVerifiee) {
            throw new RegleMetierException("RM26", "le nouveau prix s'écarte de plus de 50 % de l'ancien : il doit être vérifié");
        }
        Ingredient ingredient = ingredients.trouver(modification.nomIngredient());
        ingredient.appliquerPrix(apercu.nouveauPrix());
        ingredients.mettreAJour(ingredient);
        for (Menu menu : apercu.impact().menus()) {
            menu.marquerARecalculer();
            menus.enregistrer(menu);
        }
        return apercu.impact();
    }
}
