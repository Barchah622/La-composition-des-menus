package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculateurCoutRevientTest {

    private final CalculateurCoutRevient calculateur = new CalculateurCoutRevient();
    private final LocalDate aujourdhui = LocalDate.of(2026, 11, 12);

    private static Ingredient ingredient(String nom, UniteAchat unite, boolean frais, double prix) {
        Ingredient ingredient = new Ingredient(nom, unite, frais);
        ingredient.appliquerPrix(new PrixIngredient(prix, LocalDate.of(2026, 10, 1)));
        return ingredient;
    }

    @Test
    void cinqCentilitresDUneSauceASixQuaranteLeLitreCoutentTrenteDeuxCentimes() {
        Recette sauce = Recette.preparation("Sauce hollandaise", 1, UniteMesure.L);
        sauce.ajouterComposant(Composant.ingredient(ingredient("Base", UniteAchat.LITRE, false, 6.40), 1, UniteMesure.L));
        Recette plat = Recette.plat("Saumon");
        plat.ajouterComposant(Composant.preparation(sauce, 5, UniteMesure.CL));

        assertEquals(0.32, calculateur.coutRecette(plat, aujourdhui).montant(), 0.0001);
    }

    @Test
    void lesIngredientsFraisComptentDixPourCentDePertes() {
        Recette plat = Recette.plat("Pavé de saumon");
        plat.ajouterComposant(Composant.ingredient(ingredient("Saumon", UniteAchat.KILO, true, 20), 150, UniteMesure.G));

        assertEquals(3.30, calculateur.coutRecette(plat, aujourdhui).montant(), 0.0001);
    }

    @Test
    void leCoutDUnMenuEstLaSommeDesPortionsFoisLesCouverts() {
        Recette plat = Recette.plat("Frites");
        plat.ajouterComposant(Composant.ingredient(ingredient("Huile", UniteAchat.LITRE, false, 2), 10, UniteMesure.CL));
        Menu menu = Menu.reconstituer("Buffet", List.of(plat, plat), EtatMenu.COMPOSE);

        CoutCalcule cout = calculateur.calculer(menu, 120, aujourdhui);

        assertEquals(0.40, cout.coutParCouvert(), 0.0001);
        assertEquals(48.00, cout.coutTotal(), 0.0001);
    }

    @Test
    void unIngredientSansPrixRendLeCoutIncomplet() {
        Recette plat = Recette.plat("Risotto");
        plat.ajouterComposant(Composant.ingredient(new Ingredient("Safran", UniteAchat.KILO, false), 1, UniteMesure.G));

        Cout cout = calculateur.coutRecette(plat, aujourdhui);

        assertFalse(cout.complet());
        assertEquals(java.util.Set.of("Safran"), cout.ingredientsSansPrix());
    }

    @Test
    void zeroCouvertEstRefuse() {
        Menu menu = Menu.reconstituer("Vide", List.of(Recette.plat("Eau")), EtatMenu.COMPOSE);

        RegleMetierException refus = assertThrows(RegleMetierException.class, () -> calculateur.calculer(menu, 0, aujourdhui));
        assertEquals("RM2", refus.regle());
    }
}
