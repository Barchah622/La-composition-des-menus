package fr.esiea.menus.domaine;

import java.util.Optional;

/**
 * UC3 — Mettre à jour le prix d'un ingrédient (acteur : Système fournisseur).
 * Porteuse : Abir GUEBBACHE. Règles : RM13, RM14, RM15, RM16 (+ variation anormale, scénario 3e').
 */
public class MettreAJourPrixIngredient {

    private final CatalogueIngredients ingredients;
    private final CatalogueRecettes recettes;
    private final MenuRepository menus;
    private final ControleurVariationPrix controleur = new ControleurVariationPrix();
    private final ImpactChangementPrix impact = new ImpactChangementPrix();

    public MettreAJourPrixIngredient(CatalogueIngredients ingredients, CatalogueRecettes recettes, MenuRepository menus) {
        this.ingredients = ingredients;
        this.recettes = recettes;
        this.menus = menus;
    }

    public ResultatMiseAJourPrix recevoir(TarifFournisseur tarif) {
        return traiter(tarif, false);
    }

    /** 3e' : le Chef de production confirme une variation anormale mise en attente. */
    public ResultatMiseAJourPrix confirmer(TarifFournisseur tarif) {
        return traiter(tarif, true);
    }

    private ResultatMiseAJourPrix traiter(TarifFournisseur tarif, boolean confirmeParLeChef) {
        Ingredient ingredient = ingredients.trouver(tarif.referenceIngredient());
        exigerMemeUnite(tarif, ingredient);
        PrixIngredient nouveau = new PrixIngredient(tarif.prix(), tarif.dateApplication());
        ingredient.verifierNouveauPrix(nouveau);

        Optional<PrixIngredient> ancien = ingredient.dernierPrix();
        if (!confirmeParLeChef && variationAnormale(ancien, nouveau)) {
            return ResultatMiseAJourPrix.enAttente(ingredient.nom(), ancien.get(), nouveau);
        }
        ingredient.appliquerPrix(nouveau);
        ingredients.mettreAJour(ingredient);
        return ResultatMiseAJourPrix.applique(ingredient.nom(), ancien, nouveau, signalerARecalculer(ingredient));
    }

    private static void exigerMemeUnite(TarifFournisseur tarif, Ingredient ingredient) {
        if (tarif.unite() != ingredient.uniteAchat()) {
            throw new RegleMetierException("RM14", "le tarif est au " + tarif.unite().libelle() + " mais « "
                    + ingredient.nom() + " » s'achète au " + ingredient.uniteAchat().libelle()
                    + " : la conversion doit être renseignée par le Chef");
        }
    }

    private boolean variationAnormale(Optional<PrixIngredient> ancien, PrixIngredient nouveau) {
        return ancien.isPresent() && controleur.exigeConfirmation(ancien.get().valeur(), nouveau.valeur());
    }

    private ImpactPrix signalerARecalculer(Ingredient ingredient) {
        ImpactPrix aRecalculer = impact.aRecalculer(ingredient, recettes.toutes(), menus.tous());
        for (Menu menu : aRecalculer.menus()) {
            menu.marquerARecalculer();
            menus.enregistrer(menu);
        }
        return aRecalculer;
    }
}
