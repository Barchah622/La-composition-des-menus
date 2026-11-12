package fr.esiea.menus.domaine;

import java.time.LocalDate;

/**
 * UC2 — Composer une recette (plat ou préparation de base).
 * Porteuse : Abir GUEBBACHE. Règles : RM6, RM7, RM8, RM9, RM10, RM11, RM12.
 */
public class ComposerRecette {

    private final CatalogueRecettes recettes;
    private final CatalogueIngredients ingredients;
    private final CalculateurCoutRevient calculateur;

    public ComposerRecette(CatalogueRecettes recettes, CatalogueIngredients ingredients, CalculateurCoutRevient calculateur) {
        this.recettes = recettes;
        this.ingredients = ingredients;
        this.calculateur = calculateur;
    }

    public Recette commencerPlat(String nom) {
        recettes.exigerNomLibre(nom);
        return Recette.plat(nom);
    }

    public Recette commencerPreparation(String nom, double quantiteProduite, UniteMesure uniteProduite) {
        recettes.exigerNomLibre(nom);
        return Recette.preparation(nom, quantiteProduite, uniteProduite);
    }

    /** 1a : variante d'une recette existante, copiée sous un nouveau nom. */
    public Recette commencerVariante(String nomSource, String nouveauNom) {
        recettes.exigerNomLibre(nouveauNom);
        return recettes.trouver(nomSource).copierSous(nouveauNom);
    }

    /** Reprendre une recette déjà enregistrée pour lui ajouter des composants. */
    public Recette reprendre(String nom) {
        return recettes.trouver(nom);
    }

    public void ajouterIngredient(Recette recette, String nomIngredient, double quantite, UniteMesure unite) {
        recette.ajouterComposant(Composant.ingredient(ingredients.trouver(nomIngredient), quantite, unite));
    }

    public void ajouterPreparation(Recette recette, String nomPreparation, double quantite, UniteMesure unite) {
        recette.ajouterComposant(Composant.preparation(recettes.trouver(nomPreparation), quantite, unite));
    }

    public FicheRecette valider(Recette recette, LocalDate date) {
        recette.valider();
        if (recettes.existe(recette.nom())) {
            recettes.mettreAJour(recette);
        } else {
            recettes.ajouter(recette);
        }
        return ficheDe(recette, date);
    }

    public FicheRecette ficheDe(Recette recette, LocalDate date) {
        return new FicheRecette(recette, calculateur.coutRecette(recette, date), recette.allergenes(),
                recette.ingredientsSansAllergenes().stream().map(Ingredient::nom).toList());
    }
}
