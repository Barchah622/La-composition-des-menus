package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Les trois cas de l'exercice 4 pour le sujet C (allergènes et composition). */
class VerificateurAllergenesTest {

    private final VerificateurAllergenes verificateur = new VerificateurAllergenes();
    private final LocalDate aujourdhui = LocalDate.of(2026, 11, 12);

    @Test
    void unAllergeneDansUneSousPreparationRemonteJusquAuMenu() {
        Cuisine cuisine = new Cuisine().avecMenuMariage();
        Menu mariage = cuisine.menus.trouverParNom("Mariage").orElseThrow();

        ListeAllergenesMenu liste = verificateur.etablirListe(mariage, aujourdhui);

        assertEquals(Set.of(Allergene.POISSON, Allergene.LAIT, Allergene.OEUFS), liste.allergenesDuMenu());
    }

    @Test
    void unMenuSansAucunAllergeneLeDitExplicitement() {
        Ingredient pomme = new Ingredient("Pomme", UniteAchat.PIECE, true);
        pomme.renseignerAllergenes(Set.of());
        Recette compote = Recette.plat("Compote");
        compote.ajouterComposant(Composant.ingredient(pomme, 2, UniteMesure.PIECE));
        Menu menu = Menu.reconstituer("Goûter", List.of(compote), EtatMenu.COMPOSE);

        assertTrue(verificateur.etablirListe(menu, aujourdhui).aucunAllergene());
    }

    @Test
    void unePreparationQuiSeContientElleMemeEstRefusee() {
        Ingredient beurre = new Ingredient("Beurre", UniteAchat.KILO, false);
        Recette sauceA = Recette.preparation("Sauce A", 1, UniteMesure.L);
        Recette sauceB = Recette.preparation("Sauce B", 1, UniteMesure.L);
        sauceA.ajouterComposant(Composant.ingredient(beurre, 100, UniteMesure.G));
        sauceB.ajouterComposant(Composant.preparation(sauceA, 10, UniteMesure.CL));

        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> sauceA.ajouterComposant(Composant.preparation(sauceB, 10, UniteMesure.CL)));
        assertEquals("RM7", refus.regle());
    }

    @Test
    void unIngredientSansAllergenesRenseignesBloqueLaListe() {
        Ingredient epices = new Ingredient("Mélange d'épices", UniteAchat.KILO, false);
        Recette plat = Recette.plat("Curry");
        plat.ajouterComposant(Composant.ingredient(epices, 5, UniteMesure.G));
        Menu menu = Menu.reconstituer("Indien", List.of(plat), EtatMenu.COMPOSE);

        RegleMetierException refus = assertThrows(RegleMetierException.class, () -> verificateur.etablirListe(menu, aujourdhui));
        assertEquals("RM22", refus.regle());
    }
}
