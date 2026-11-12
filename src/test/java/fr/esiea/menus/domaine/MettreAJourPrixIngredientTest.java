package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UC3 — Mettre à jour le prix d'un ingrédient (Système fournisseur) · Abir GUEBBACHE. */
class MettreAJourPrixIngredientTest {

    private static final LocalDate PREMIER_DECEMBRE = LocalDate.of(2026, 12, 1);

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();
    private final MettreAJourPrixIngredient uc3 = cuisine.mettreAJourPrix;

    @Test
    void leNouveauPrixEstAppliqueLAncienGardeEtLesMenusARecalculer() {
        ResultatMiseAJourPrix resultat = uc3.recevoir(new TarifFournisseur("Saumon", 21.00, UniteAchat.KILO, PREMIER_DECEMBRE));

        Ingredient saumon = cuisine.ingredients.trouver("Saumon");
        assertTrue(resultat.applique());
        assertEquals(18.50, resultat.ancienPrix().orElseThrow().valeur());
        assertEquals(2, saumon.historiquePrix().size());
        assertEquals(18.50, saumon.prixEnVigueur(Cuisine.DOUZE_NOVEMBRE).orElseThrow().valeur());
        assertEquals(21.00, saumon.prixEnVigueur(PREMIER_DECEMBRE).orElseThrow().valeur());
        assertEquals(List.of("Saumon sauce hollandaise"), resultat.impact().recettes().stream().map(Recette::nom).toList());
        assertEquals(EtatMenu.A_RECALCULER, cuisine.menus.trouverParNom("Mariage").orElseThrow().etat());
    }

    @Test
    void unPrixQuiChangeDansUneSauceRemonteJusquAuMenu() {
        ResultatMiseAJourPrix resultat = uc3.recevoir(new TarifFournisseur("Beurre doux", 10.50, UniteAchat.KILO, PREMIER_DECEMBRE));

        assertEquals(List.of("Sauce hollandaise", "Saumon sauce hollandaise"),
                resultat.impact().recettes().stream().map(Recette::nom).toList());
        assertEquals(List.of("Mariage"), resultat.impact().menus().stream().map(Menu::nom).toList());
    }

    @Test
    void unIngredientInconnuEstRejete() {
        assertThrows(ElementInconnuException.class,
                () -> uc3.recevoir(new TarifFournisseur("Truffe", 900, UniteAchat.KILO, PREMIER_DECEMBRE)));
    }

    @Test
    void unPrixNulEstRejeteEtLAncienPrixGarde() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc3.recevoir(new TarifFournisseur("Saumon", 0, UniteAchat.KILO, PREMIER_DECEMBRE)));

        assertEquals("RM13", refus.regle());
        assertEquals(1, cuisine.ingredients.trouver("Saumon").historiquePrix().size());
    }

    @Test
    void uneDateAnterieureAuDernierPrixEstRejetee() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc3.recevoir(new TarifFournisseur("Saumon", 19, UniteAchat.KILO, LocalDate.of(2026, 9, 1))));

        assertEquals("RM15", refus.regle());
    }

    @Test
    void unTarifDansUneAutreUniteEstRejete() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc3.recevoir(new TarifFournisseur("Œuf", 1.80, UniteAchat.KILO, PREMIER_DECEMBRE)));

        assertEquals("RM14", refus.regle());
    }

    @Test
    void uneVariationAnormaleAttendLaConfirmationDuChef() {
        TarifFournisseur erreurDeVirgule = new TarifFournisseur("Saumon", 185.00, UniteAchat.KILO, PREMIER_DECEMBRE);

        ResultatMiseAJourPrix enAttente = uc3.recevoir(erreurDeVirgule);

        assertFalse(enAttente.applique());
        assertEquals(1, cuisine.ingredients.trouver("Saumon").historiquePrix().size());

        ResultatMiseAJourPrix confirme = uc3.confirmer(erreurDeVirgule);

        assertTrue(confirme.applique());
        assertEquals(185.00, cuisine.ingredients.trouver("Saumon").dernierPrix().orElseThrow().valeur());
    }

    @Test
    void lePremierPrixDUnIngredientSansPrixEstAppliqueSansControle() {
        cuisine.enregistrerIngredient.enregistrer("Aneth", UniteAchat.PIECE, true, null, Cuisine.PREMIER_OCTOBRE, null);

        ResultatMiseAJourPrix resultat = uc3.recevoir(new TarifFournisseur("Aneth", 1.20, UniteAchat.PIECE, PREMIER_DECEMBRE));

        assertTrue(resultat.applique());
        assertTrue(resultat.ancienPrix().isEmpty());
    }
}
