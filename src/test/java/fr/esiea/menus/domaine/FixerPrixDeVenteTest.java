package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** UC8 — Fixer le prix de vente d'un menu selon le type de client · Mohamed Amine BARCHAH. */
class FixerPrixDeVenteTest {

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();

    @Test
    void leCommercialFixeLePrixDuMariagePourUneEntreprise() {
        PropositionPrix proposition = cuisine.fixerPrixDeVente.proposer("Mariage", 100, TypeDeClient.ENTREPRISE,
                Cuisine.DOUZE_NOVEMBRE);
        double attendu = Math.round(Cuisine.coutPortionSaumon() * 100 * 3 * 100) / 100.0;

        PrixDeVente prix = cuisine.fixerPrixDeVente.fixer(proposition, proposition.prixTheorique(), Cuisine.DOUZE_NOVEMBRE);

        assertEquals(attendu, prix.prix(), 0.001);
        assertEquals(prix.prix() - prix.coutRevient(), prix.marge(), 0.001);
        assertEquals(1, cuisine.prixDeVente.pourMenu("Mariage").size());
        assertEquals(EtatMenu.TARIFE, cuisine.menus.trouverParNom("Mariage").orElseThrow().etat());
    }

    @Test
    void vendreAPerteEstInterdit() {
        PropositionPrix proposition = cuisine.fixerPrixDeVente.proposer("Mariage", 100, TypeDeClient.PARTICULIER,
                Cuisine.DOUZE_NOVEMBRE);

        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> cuisine.fixerPrixDeVente.fixer(proposition, 10.0, Cuisine.DOUZE_NOVEMBRE));
        assertEquals("RM5", refus.regle());
    }
}
