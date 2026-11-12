package fr.esiea.menus.console;

import fr.esiea.menus.persistance.IngredientRepositoryMemoire;
import fr.esiea.menus.persistance.MenuRepositoryMemoire;
import fr.esiea.menus.persistance.PrixDeVenteRepositoryMemoire;
import fr.esiea.menus.persistance.RecetteRepositoryMemoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Le menu console, piloté par une saisie scriptée : chaque choix lance son use case et survit aux saisies absurdes. */
class MenuConsoleTest {

    private Application application;

    @BeforeEach
    void chargerLaDemo() {
        application = Application.assembler(new IngredientRepositoryMemoire(), new RecetteRepositoryMemoire(),
                new MenuRepositoryMemoire(), new PrixDeVenteRepositoryMemoire());
        assertTrue(DonneesDeDemo.chargerSiVide(application));
        assertFalse(DonneesDeDemo.chargerSiVide(application));
    }

    private String jouer(String... lignes) {
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        Scanner entree = new Scanner(String.join("\n", lignes) + "\n");
        new MenuConsole(application, entree, new PrintStream(sortie, true, StandardCharsets.UTF_8),
                () -> LocalDate.of(2026, 11, 13)).boucle();
        return sortie.toString(StandardCharsets.UTF_8);
    }

    @Test
    void uneSaisieAbsurdeNeFaitPasPlanterLeMenu() {
        String ecran = jouer("42", "blabla", "4", "1", "beaucoup", "0", "", "0");

        assertTrue(ecran.contains("Choix inconnu"));
        assertTrue(ecran.contains("n'est pas un nombre entier"));
        assertTrue(ecran.contains("Refusé : RM2"));
        assertTrue(ecran.contains("Au revoir."));
    }

    @Test
    void laFinDeLEntreeQuitteProprement() {
        String ecran = jouer("2", "1");

        assertTrue(ecran.contains("Nom du plat"));
    }

    @Test
    void uc1EnregistrerUnIngredient() {
        String ecran = jouer("1", "Aneth", "3", "o", "", "", "9", "0");

        assertTrue(ecran.contains("« Aneth » enregistré (prix manquant) (allergènes à compléter)"));
        assertTrue(ecran.contains("Aneth · prix manquant / pièce · frais · allergènes à compléter"));
    }

    @Test
    void uc1AvecPrixEtAllergenes() {
        String ecran = jouer("1", "Moutarde", "1", "n", "6,5", "01/11/2026", "x", "10", "0");

        assertTrue(ecran.contains("Numéros entre 1 et 14"));
        assertTrue(ecran.contains("« Moutarde » enregistré."));
    }

    @Test
    void uc2ComposerUnPlatAvecUnePreparation() {
        String ecran = jouer("2", "1", "Pavé de saumon",
                "v",
                "i", "Saumon", "150", "g",
                "i", "Œuf", "1", "L",
                "p", "1", "5", "cL",
                "z",
                "r", "Truffe",
                "v", "0");

        assertTrue(ecran.contains("Refusé : RM6"));
        assertTrue(ecran.contains("Refusé : RM8"));
        assertTrue(ecran.contains("composant inconnu"));
        assertTrue(ecran.contains("Recette « Pavé de saumon » enregistrée."));
        assertTrue(ecran.contains("Allergènes : œufs, poisson, lait"));
    }

    @Test
    void uc2PreparationVarianteEtReprise() {
        String ecran = jouer(
                "2", "2", "Beurre clarifié", "500", "g", "i", "Beurre doux", "600", "g", "v",
                "2", "3", "1", "Sauce allégée", "r", "Citron", "a",
                "2", "4", "2", "p", "1", "1", "kg", "v",
                "0");

        assertTrue(ecran.contains("Coût de revient : 5,88 € pour 500 g"));
        assertTrue(ecran.contains("Recette abandonnée."));
        assertTrue(ecran.contains("Refusé : RM8"));
    }

    @Test
    void uc2SignaleUnCoutIncomplet() {
        String ecran = jouer("1", "Aneth", "3", "o", "", "",
                "2", "1", "Saumon à l'aneth", "i", "Aneth", "1", "pièce", "v", "0");

        assertTrue(ecran.contains("Coût incomplet, prix manquant pour : [Aneth]"));
        assertTrue(ecran.contains("Allergènes à compléter pour : [Aneth]"));
    }

    @Test
    void uc3RecevoirUnTarifFournisseur() {
        String ecran = jouer("3", "4", "21", "kilo", "01/12/2026", "0");

        assertTrue(ecran.contains("Prix de « Saumon » : 21,00 €"));
        assertTrue(ecran.contains("Menus à recalculer : Mariage Dupont"));
    }

    @Test
    void uc3VariationAnormaleRefuseePuisConfirmee() {
        String ecran = jouer("3", "4", "185", "kilo", "01/12/2026", "n",
                "3", "4", "185", "kilo", "01/12/2026", "o", "0");

        assertTrue(ecran.contains("Variation anormale"));
        assertTrue(ecran.contains("Ancien prix conservé."));
        assertTrue(ecran.contains("Prix de « Saumon » : 185,00 €"));
    }

    @Test
    void uc4EtUc5SurLeMenuDeDemonstration() {
        String ecran = jouer("4", "1", "120", "", "5", "1", "0");

        assertTrue(ecran.contains("Coût total pour 120 couverts"));
        assertTrue(ecran.contains("Saumon sauce hollandaise : œufs, poisson, lait"));
    }

    @Test
    void uc6ComposerUnMenu() {
        String ecran = jouer("6", "Séminaire", "1,3", "6", "Vide", "", "6", "Faux", "99", "0");

        assertTrue(ecran.contains("Menu « Séminaire » enregistré"));
        assertTrue(ecran.contains("Refusé : RM1"));
        assertTrue(ecran.contains("plat inconnu"));
    }

    @Test
    void uc7ModifierUnPrixAvecJustificatif() {
        String ecran = jouer("7", "1", "10,4", "10/11/2026", "Facture 2611-045", "o",
                "7", "1", "99", "11/11/2026", "Facture 2611-046", "12", "o",
                "7", "1", "11", "12/11/2026", "Facture 2611-047", "n", "0");

        assertTrue(ecran.contains("Nouveau prix enregistré"));
        assertTrue(ecran.contains("Refusé : RM26"));
        assertTrue(ecran.contains("Rien n'a été modifié."));
    }

    @Test
    void uc8FixerUnPrixDeVente() {
        String ecran = jouer("8", "1", "100", "1", "", "o",
                "8", "1", "100", "2", "1", "o",
                "8", "1", "100", "2", "", "n", "0");

        assertTrue(ecran.contains("Prix de vente de « Mariage Dupont » pour 100 convives (Entreprise)"));
        assertTrue(ecran.contains("Refusé : RM5"));
        assertTrue(ecran.contains("Prix non enregistré."));
        assertEquals(1, application.prixDeVente().pourMenu("Mariage Dupont").size());
    }
}
