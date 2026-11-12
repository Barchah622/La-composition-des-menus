package fr.esiea.menus.domaine;

import java.util.Objects;

/** La quantité d'un ingrédient ou d'une préparation qui entre dans une recette (RM8, RM10). */
public final class Composant {

    private static final ConvertisseurUnites CONVERTISSEUR = new ConvertisseurUnites();

    private final Ingredient ingredient;
    private final Recette preparation;
    private final double quantite;
    private final UniteMesure unite;

    private Composant(Ingredient ingredient, Recette preparation, double quantite, UniteMesure unite) {
        this.ingredient = ingredient;
        this.preparation = preparation;
        this.quantite = quantite;
        this.unite = unite;
    }

    public static Composant ingredient(Ingredient ingredient, double quantite, UniteMesure unite) {
        Objects.requireNonNull(ingredient, "ingrédient");
        exigerQuantitePositive(quantite, ingredient.nom());
        exigerUniteCompatible(unite, ingredient.uniteAchat(), ingredient.nom());
        return new Composant(ingredient, null, quantite, unite);
    }

    public static Composant preparation(Recette preparation, double quantite, UniteMesure unite) {
        Objects.requireNonNull(preparation, "préparation");
        if (!preparation.estPreparation()) {
            throw new RegleMetierException("RM8", "« " + preparation.nom() + " » est un plat : seule une préparation peut entrer dans une recette");
        }
        exigerQuantitePositive(quantite, preparation.nom());
        exigerUniteCompatible(unite, preparation.uniteProduite().uniteAchat(), preparation.nom());
        return new Composant(null, preparation, quantite, unite);
    }

    private static void exigerQuantitePositive(double quantite, String nom) {
        if (Double.isNaN(quantite) || quantite <= 0) {
            throw new RegleMetierException("RM8", "la quantité de « " + nom + " » doit être strictement positive");
        }
    }

    private static void exigerUniteCompatible(UniteMesure unite, UniteAchat uniteAchat, String nom) {
        Objects.requireNonNull(unite, "unité");
        if (!CONVERTISSEUR.sontCompatibles(unite, uniteAchat)) {
            throw new RegleMetierException("RM8", "« " + nom + " » se compte au " + uniteAchat.libelle()
                    + " : l'unité " + unite.symbole() + " n'est pas compatible");
        }
    }

    public boolean estIngredient() {
        return ingredient != null;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public Recette preparation() {
        return preparation;
    }

    public double quantite() {
        return quantite;
    }

    public UniteMesure unite() {
        return unite;
    }

    public String nom() {
        return estIngredient() ? ingredient.nom() : preparation.nom();
    }

    /** RM20 : 150 g de saumon acheté au kilo → 0,150. */
    public double quantiteEnUniteAchat() {
        return CONVERTISSEUR.versUniteAchat(quantite, unite, ingredient.uniteAchat());
    }

    /** RM10 : 5 cL d'une sauce qui produit 1 L → 0,05 de la sauce. */
    public double partDeLaPreparation() {
        double quantiteUtilisee = quantite * unite.facteurVersUniteAchat();
        return quantiteUtilisee / preparation.quantiteProduiteEnUniteAchat();
    }

    @Override
    public String toString() {
        return quantite + " " + unite.symbole() + " de " + nom();
    }
}
