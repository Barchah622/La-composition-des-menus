package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UC1 — Enregistrer un ingrédient · Manele HSSINI. */
class EnregistrerIngredientTest {

    private final Cuisine cuisine = new Cuisine();
    private final EnregistrerIngredient uc1 = cuisine.enregistrerIngredient;

    @Test
    void leBeurreDouxEstEnregistreAvecSonPrixEtSesAllergenes() {
        uc1.enregistrer("Beurre doux", UniteAchat.KILO, false, 9.80, Cuisine.PREMIER_OCTOBRE, Set.of(Allergene.LAIT));

        Ingredient beurre = cuisine.ingredients.trouver("beurre doux");
        assertEquals(9.80, beurre.prixEnVigueur(Cuisine.DOUZE_NOVEMBRE).orElseThrow().valeur());
        assertEquals(Set.of(Allergene.LAIT), beurre.allergenes());
    }

    @Test
    void deuxIngredientsNePeuventPasPorterLeMemeNom() {
        uc1.enregistrer("Crème fraîche", UniteAchat.LITRE, false, 5.0, Cuisine.PREMIER_OCTOBRE, Set.of(Allergene.LAIT));

        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc1.enregistrer("crème fraîche", UniteAchat.LITRE, false, 5.0, Cuisine.PREMIER_OCTOBRE, Set.of()));
        assertEquals("RM17", refus.regle());
    }

    @Test
    void aucunAllergeneNEstPasLaMemeChoseQueAllergenesACompleter() {
        Ingredient sel = uc1.enregistrer("Sel", UniteAchat.KILO, false, 0.80, Cuisine.PREMIER_OCTOBRE, Set.of());
        Ingredient epices = uc1.enregistrer("Épices", UniteAchat.KILO, false, null, Cuisine.PREMIER_OCTOBRE, null);

        assertTrue(sel.allergenesRenseignes());
        assertFalse(epices.allergenesRenseignes());
        assertTrue(epices.dernierPrix().isEmpty());
    }

    @Test
    void uneUniteNonAutoriseeEstRefusee() {
        RegleMetierException refus = assertThrows(RegleMetierException.class, () -> UniteAchat.depuisLibelle("botte"));
        assertEquals("RM14", refus.regle());
    }
}
