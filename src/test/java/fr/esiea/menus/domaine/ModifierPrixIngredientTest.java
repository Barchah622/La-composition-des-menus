package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UC7 — Modifier le prix d'un ingrédient (saisie manuelle) · Tania HUSSAIN. */
class ModifierPrixIngredientTest {

    private static final LocalDate DIX_NOVEMBRE = LocalDate.of(2026, 11, 10);

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();

    @Test
    void leChefSaisitUnNouveauPrixAvecSaFacture() {
        ModificationPrix facture = new ModificationPrix("Beurre doux", 10.40, DIX_NOVEMBRE, "Facture 2611-045");

        ApercuModificationPrix apercu = cuisine.modifierPrix.apercu(facture);
        ImpactPrix impact = cuisine.modifierPrix.appliquer(facture, false);

        assertEquals(0.60, apercu.ecart().orElseThrow(), 0.0001);
        assertEquals(10.40, cuisine.ingredients.trouver("Beurre doux").prixEnVigueur(DIX_NOVEMBRE).orElseThrow().valeur());
        assertEquals(1, impact.menus().size());
    }

    @Test
    void sansJustificatifLaModificationEstRefusee() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> new ModificationPrix("Beurre doux", 10.40, DIX_NOVEMBRE, " "));
        assertEquals("RM25", refus.regle());
    }

    @Test
    void unEcartDePlusDeCinquantePourCentDoitEtreVerifie() {
        ModificationPrix erreurDeVirgule = new ModificationPrix("Beurre doux", 98.0, DIX_NOVEMBRE, "Facture 2611-046");

        assertTrue(cuisine.modifierPrix.apercu(erreurDeVirgule).confirmationRequise());
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> cuisine.modifierPrix.appliquer(erreurDeVirgule, false));
        assertEquals("RM26", refus.regle());

        cuisine.modifierPrix.appliquer(erreurDeVirgule, true);
        assertEquals(98.0, cuisine.ingredients.trouver("Beurre doux").dernierPrix().orElseThrow().valeur());
    }
}
