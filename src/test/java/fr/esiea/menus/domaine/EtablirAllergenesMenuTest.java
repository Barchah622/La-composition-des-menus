package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** UC5 — Établir la liste des allergènes d'un menu · Tania HUSSAIN. */
class EtablirAllergenesMenuTest {

    private final Cuisine cuisine = new Cuisine().avecMenuMariage();

    @Test
    void laListeDuMariageEstDonneePlatParPlatPuisPourLeMenu() {
        ListeAllergenesMenu liste = cuisine.etablirAllergenes.etablir("Mariage", Cuisine.DOUZE_NOVEMBRE);

        assertEquals(Set.of(Allergene.POISSON, Allergene.LAIT, Allergene.OEUFS),
                liste.allergenesParPlat().get("Saumon sauce hollandaise"));
        assertEquals(Cuisine.DOUZE_NOVEMBRE, liste.date());
        assertFalse(liste.aRevoir());
        liste.marquerARevoir();
        assertEquals(true, liste.aRevoir());
    }
}
