package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Allergene;
import fr.esiea.menus.domaine.Composant;
import fr.esiea.menus.domaine.EtatMenu;
import fr.esiea.menus.domaine.Ingredient;
import fr.esiea.menus.domaine.Menu;
import fr.esiea.menus.domaine.PrixDeVente;
import fr.esiea.menus.domaine.PrixIngredient;
import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.TypeDeClient;
import fr.esiea.menus.domaine.UniteAchat;
import fr.esiea.menus.domaine.UniteMesure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Les Repository JDBC, testés sur une base H2 en mode PostgreSQL avec le vrai schema.sql. */
class RepositoriesJdbcTest {

    private static final LocalDate PREMIER_OCTOBRE = LocalDate.of(2026, 10, 1);

    private Connection connexion;
    private IngredientRepositoryJdbc ingredients;
    private RecetteRepositoryJdbc recettes;
    private MenuRepositoryJdbc menus;
    private PrixDeVenteRepositoryJdbc prixDeVente;

    @BeforeEach
    void ouvrirUneBaseNeuve() throws SQLException {
        connexion = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL");
        SchemaBase.creer(connexion);
        SchemaBase.creer(connexion);
        ingredients = new IngredientRepositoryJdbc(connexion);
        recettes = new RecetteRepositoryJdbc(connexion, ingredients);
        menus = new MenuRepositoryJdbc(connexion, recettes);
        prixDeVente = new PrixDeVenteRepositoryJdbc(connexion);
    }

    @AfterEach
    void fermer() throws SQLException {
        connexion.close();
    }

    private Ingredient beurre() {
        Ingredient beurre = new Ingredient("Beurre doux", UniteAchat.KILO, false);
        beurre.appliquerPrix(new PrixIngredient(9.80, PREMIER_OCTOBRE));
        beurre.renseignerAllergenes(Set.of(Allergene.LAIT));
        ingredients.enregistrer(beurre);
        return beurre;
    }

    @Test
    void unIngredientRevientAvecSonHistoriqueDePrixEtSesAllergenes() {
        Ingredient beurre = beurre();
        beurre.appliquerPrix(new PrixIngredient(10.40, LocalDate.of(2026, 11, 10)));
        ingredients.enregistrer(beurre);
        Ingredient sel = new Ingredient("Sel", UniteAchat.KILO, false);
        sel.renseignerAllergenes(Set.of());
        ingredients.enregistrer(sel);
        ingredients.enregistrer(new Ingredient("Épices", UniteAchat.KILO, false));

        Ingredient relu = ingredients.trouverParNom("BEURRE DOUX").orElseThrow();

        assertEquals(2, relu.historiquePrix().size());
        assertEquals(9.80, relu.prixEnVigueur(LocalDate.of(2026, 11, 1)).orElseThrow().valeur());
        assertEquals(Set.of(Allergene.LAIT), relu.allergenes());
        assertTrue(ingredients.trouverParNom("Sel").orElseThrow().allergenesRenseignes());
        assertFalse(ingredients.trouverParNom("Épices").orElseThrow().allergenesRenseignes());
        assertEquals(3, ingredients.tous().size());
        assertTrue(ingredients.trouverParNom("Truffe").isEmpty());
    }

    @Test
    void unMenuRevientAvecSesPlatsSesPreparationsEtSonEtat() {
        Ingredient beurre = beurre();
        Recette sauce = Recette.preparation("Beurre blanc", 1, UniteMesure.L);
        sauce.ajouterComposant(Composant.ingredient(beurre, 500, UniteMesure.G));
        recettes.enregistrer(sauce);
        Recette plat = Recette.plat("Sole au beurre blanc");
        plat.ajouterComposant(Composant.preparation(sauce, 5, UniteMesure.CL));
        recettes.enregistrer(plat);
        recettes.enregistrer(plat);
        Menu menu = new Menu("Mariage");
        menu.ajouterPlat(plat);
        menu.enregistrer();
        menus.enregistrer(menu);
        menu.marquerChiffre();
        menus.enregistrer(menu);

        Menu relu = menus.trouverParNom("mariage").orElseThrow();

        assertEquals(EtatMenu.CHIFFRE, relu.etat());
        Recette platRelu = relu.plats().get(0);
        assertEquals("Sole au beurre blanc", platRelu.nom());
        assertEquals("Beurre blanc", platRelu.composants().get(0).preparation().nom());
        assertTrue(platRelu.utilise(beurre));
        assertEquals(Set.of(Allergene.LAIT), platRelu.allergenes());
        assertEquals(2, recettes.toutes().size());
        assertEquals(List.of("Mariage"), menus.tous().stream().map(Menu::nom).toList());
        assertTrue(recettes.trouverParNom("Inconnue").isEmpty());
        assertTrue(menus.trouverParNom("Inconnu").isEmpty());
    }

    @Test
    void unPrixDeVenteEstRelu() {
        Recette plat = Recette.plat("Plateau");
        plat.ajouterComposant(Composant.ingredient(beurre(), 10, UniteMesure.G));
        recettes.enregistrer(plat);
        Menu menu = new Menu("Cocktail");
        menu.ajouterPlat(plat);
        menus.enregistrer(menu);
        prixDeVente.enregistrer(new PrixDeVente("Cocktail", TypeDeClient.PARTICULIER, 50, 250, 100, PREMIER_OCTOBRE));

        List<PrixDeVente> relus = prixDeVente.pourMenu("cocktail");

        assertEquals(1, relus.size());
        assertEquals(TypeDeClient.PARTICULIER, relus.get(0).typeDeClient());
        assertEquals(150, relus.get(0).marge(), 0.001);
    }

    @Test
    void uneErreurSqlDevientUnePersistanceException() throws SQLException {
        connexion.close();

        assertThrows(PersistanceException.class, () -> ingredients.tous());
        assertThrows(PersistanceException.class, () -> ingredients.enregistrer(new Ingredient("Sel", UniteAchat.KILO, false)));
    }
}
