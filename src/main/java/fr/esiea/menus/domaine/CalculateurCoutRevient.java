package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Décide du coût de revient d'une recette, puis d'un menu pour un nombre de couverts (RM2, RM3, RM10, RM11, RM19). */
public class CalculateurCoutRevient {

    static final double TAUX_PERTES_FRAIS = 0.10;

    public Cout coutRecette(Recette recette, LocalDate date) {
        Cout total = Cout.NUL;
        for (Composant composant : recette.composants()) {
            total = total.plus(coutComposant(composant, date));
        }
        return total;
    }

    public CoutCalcule calculer(Menu menu, int couverts, LocalDate date) {
        if (couverts <= 0) {
            throw new RegleMetierException("RM2", "le nombre de couverts doit être strictement positif (reçu : " + couverts + ")");
        }
        menu.exigerAuMoinsUnPlat();
        Map<String, Double> coutParPlat = new LinkedHashMap<>();
        Cout parCouvert = Cout.NUL;
        for (Recette plat : menu.plats()) {
            Cout portion = coutRecette(plat, date);
            coutParPlat.merge(plat.nom(), portion.montant(), Double::sum);
            parCouvert = parCouvert.plus(portion);
        }
        return new CoutCalcule(menu.nom(), date, couverts, coutParPlat,
                parCouvert.montant(), parCouvert.montant() * couverts, parCouvert.ingredientsSansPrix());
    }

    private Cout coutComposant(Composant composant, LocalDate date) {
        if (!composant.estIngredient()) {
            return coutRecette(composant.preparation(), date).fois(composant.partDeLaPreparation());
        }
        return coutIngredient(composant.ingredient(), composant.quantiteEnUniteAchat(), date);
    }

    private Cout coutIngredient(Ingredient ingredient, double quantiteEnUniteAchat, LocalDate date) {
        Optional<PrixIngredient> prix = ingredient.prixEnVigueur(date);
        if (prix.isEmpty()) {
            return Cout.sansPrix(ingredient.nom());
        }
        double montant = quantiteEnUniteAchat * prix.get().valeur();
        if (ingredient.estFrais()) {
            montant *= 1 + TAUX_PERTES_FRAIS;
        }
        return new Cout(montant, Set.of());
    }
}
