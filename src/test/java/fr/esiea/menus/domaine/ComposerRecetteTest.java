package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UC2 — Composer une recette · Abir GUEBBACHE. */
class ComposerRecetteTest {

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();
    private final ComposerRecette uc2 = cuisine.composerRecette;

    @Test
    void unPlatAvecUnePreparationAfficheSonCoutEtSesAllergenesATousLesNiveaux() {
        Recette plat = uc2.commencerPlat("Pavé de saumon, hollandaise");
        uc2.ajouterIngredient(plat, "Saumon", 150, UniteMesure.G);
        uc2.ajouterPreparation(plat, "Sauce hollandaise", 5, UniteMesure.CL);

        FicheRecette fiche = uc2.valider(plat, Cuisine.DOUZE_NOVEMBRE);

        assertEquals(Cuisine.coutPortionSaumon(), fiche.cout().montant(), 0.0001);
        assertEquals(Set.of(Allergene.POISSON, Allergene.LAIT, Allergene.OEUFS), fiche.allergenes());
        assertTrue(fiche.complete());
        assertTrue(cuisine.recettes.existe("Pavé de saumon, hollandaise"));
    }

    @Test
    void unNomDejaPrisEstRefuse() {
        RegleMetierException refus = assertThrows(RegleMetierException.class, () -> uc2.commencerPlat("sauce HOLLANDAISE"));
        assertEquals("RM9", refus.regle());
    }

    @Test
    void uneRecetteSansComposantNePeutPasEtreValidee() {
        Recette vide = uc2.commencerPlat("Assiette vide");

        RegleMetierException refus = assertThrows(RegleMetierException.class, () -> uc2.valider(vide, Cuisine.DOUZE_NOVEMBRE));
        assertEquals("RM6", refus.regle());
        assertFalse(cuisine.recettes.existe("Assiette vide"));
    }

    @Test
    void desLitresPourDesOeufsVendusALaPieceSontRefuses() {
        Recette omelette = uc2.commencerPlat("Omelette");

        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc2.ajouterIngredient(omelette, "Œuf", 1, UniteMesure.L));
        assertEquals("RM8", refus.regle());
    }

    @Test
    void uneQuantiteNegativeEstRefusee() {
        Recette omelette = uc2.commencerPlat("Omelette");

        assertThrows(RegleMetierException.class, () -> uc2.ajouterIngredient(omelette, "Œuf", -2, UniteMesure.PIECE));
    }

    @Test
    void unIngredientInconnuEstSignale() {
        Recette plat = uc2.commencerPlat("Tarte");

        assertThrows(ElementInconnuException.class, () -> uc2.ajouterIngredient(plat, "Rhubarbe", 200, UniteMesure.G));
    }

    @Test
    void uneSauceQuiFinitParSeContenirElleMemeEstRefusee() {
        Recette mousseline = uc2.commencerPreparation("Sauce mousseline", 1, UniteMesure.L);
        uc2.ajouterPreparation(mousseline, "Sauce hollandaise", 80, UniteMesure.CL);
        uc2.valider(mousseline, Cuisine.DOUZE_NOVEMBRE);
        Recette hollandaise = uc2.reprendre("Sauce hollandaise");

        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> uc2.ajouterPreparation(hollandaise, "Sauce mousseline", 5, UniteMesure.CL));
        assertEquals("RM7", refus.regle());
    }

    @Test
    void uneVarianteCopieLaRecetteSousUnNouveauNom() {
        Recette variante = uc2.commencerVariante("Sauce hollandaise", "Sauce hollandaise allégée");
        variante.retirerComposant("Beurre doux");

        FicheRecette fiche = uc2.valider(variante, Cuisine.DOUZE_NOVEMBRE);

        assertEquals(2, fiche.recette().composants().size());
        assertEquals(3, cuisine.recettes.trouver("Sauce hollandaise").composants().size());
    }

    @Test
    void unIngredientSansPrixNiAllergenesEstSignaleMaisLaRecetteEstEnregistree() {
        cuisine.enregistrerIngredient.enregistrer("Aneth", UniteAchat.PIECE, true, null, Cuisine.PREMIER_OCTOBRE, null);
        Recette plat = uc2.commencerPlat("Saumon à l'aneth");
        uc2.ajouterIngredient(plat, "Saumon", 150, UniteMesure.G);
        uc2.ajouterIngredient(plat, "Aneth", 1, UniteMesure.PIECE);

        FicheRecette fiche = uc2.valider(plat, Cuisine.DOUZE_NOVEMBRE);

        assertFalse(fiche.complete());
        assertEquals(Set.of("Aneth"), fiche.cout().ingredientsSansPrix());
        assertEquals(java.util.List.of("Aneth"), fiche.ingredientsSansAllergenes());
    }
}
