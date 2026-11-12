package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UC4 — Calculer le coût de revient d'un menu · Manele HSSINI. */
class CalculerCoutMenuTest {

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();

    @Test
    void leCoutDuMariagePourCentVingtCouvertsDescendJusquAuxIngredientsDeLaSauce() {
        CoutCalcule cout = cuisine.calculerCoutMenu.calculer("Mariage", 120, Cuisine.DOUZE_NOVEMBRE);

        assertTrue(cout.complet());
        assertEquals(Cuisine.coutPortionSaumon(), cout.coutParCouvert(), 0.0001);
        assertEquals(Cuisine.coutPortionSaumon() * 120, cout.coutTotal(), 0.0001);
        assertEquals(EtatMenu.CHIFFRE, cuisine.menus.trouverParNom("Mariage").orElseThrow().etat());
    }

    @Test
    void unNombreDeCouvertsNegatifEstRefuse() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> cuisine.calculerCoutMenu.calculer("Mariage", -3, Cuisine.DOUZE_NOVEMBRE));
        assertEquals("RM2", refus.regle());
    }

    @Test
    void unMenuInconnuEstSignale() {
        assertThrows(ElementInconnuException.class, () -> cuisine.calculerCoutMenu.calculer("Baptême", 10, Cuisine.DOUZE_NOVEMBRE));
    }
}
