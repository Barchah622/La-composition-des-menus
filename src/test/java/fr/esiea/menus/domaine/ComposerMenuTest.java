package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** UC6 — Composer un menu · Mohamed Amine BARCHAH. */
class ComposerMenuTest {

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();

    @Test
    void unMenuEstComposeDePlatsExistants() {
        Menu menu = cuisine.composerMenu.composer("Séminaire", List.of("Saumon sauce hollandaise"));

        assertEquals(EtatMenu.COMPOSE, menu.etat());
        assertEquals(List.of("Saumon sauce hollandaise"), menu.plats().stream().map(Recette::nom).toList());
        assertEquals(List.of("Saumon sauce hollandaise"), cuisine.composerMenu.platsDisponibles().stream().map(Recette::nom).toList());
    }

    @Test
    void unMenuVideEstRefuse() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> cuisine.composerMenu.composer("Menu vide", List.of()));
        assertEquals("RM1", refus.regle());
    }

    @Test
    void unePreparationNePeutPasEtreServieCommePlat() {
        assertThrows(RegleMetierException.class,
                () -> cuisine.composerMenu.composer("Menu sauce", List.of("Sauce hollandaise")));
    }

    @Test
    void unPlatIntrouvableEstSignale() {
        assertThrows(ElementInconnuException.class, () -> cuisine.composerMenu.composer("Gala", List.of("Homard")));
    }

    @Test
    void deuxMenusNePeuventPasPorterLeMemeNom() {
        assertThrows(RegleMetierException.class,
                () -> cuisine.composerMenu.composer("mariage", List.of("Saumon sauce hollandaise")));
    }
}
