package fr.esiea.menus.domaine;

import fr.esiea.menus.persistance.IngredientRepositoryMemoire;
import fr.esiea.menus.persistance.MenuRepositoryMemoire;
import fr.esiea.menus.persistance.PrixDeVenteRepositoryMemoire;
import fr.esiea.menus.persistance.RecetteRepositoryMemoire;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Une cuisine de test, en mémoire, avec les 8 use cases branchés. */
class Cuisine {

    static final LocalDate PREMIER_OCTOBRE = LocalDate.of(2026, 10, 1);
    static final LocalDate DOUZE_NOVEMBRE = LocalDate.of(2026, 11, 12);

    final MenuRepositoryMemoire menus = new MenuRepositoryMemoire();
    final PrixDeVenteRepositoryMemoire prixDeVente = new PrixDeVenteRepositoryMemoire();
    final CatalogueIngredients ingredients = new CatalogueIngredients(new IngredientRepositoryMemoire());
    final CatalogueRecettes recettes = new CatalogueRecettes(new RecetteRepositoryMemoire());
    final CalculateurCoutRevient calculateur = new CalculateurCoutRevient();

    final EnregistrerIngredient enregistrerIngredient = new EnregistrerIngredient(ingredients);
    final ComposerRecette composerRecette = new ComposerRecette(recettes, ingredients, calculateur);
    final MettreAJourPrixIngredient mettreAJourPrix = new MettreAJourPrixIngredient(ingredients, recettes, menus);
    final CalculerCoutMenu calculerCoutMenu = new CalculerCoutMenu(menus, calculateur);
    final EtablirAllergenesMenu etablirAllergenes = new EtablirAllergenesMenu(menus, new VerificateurAllergenes());
    final ComposerMenu composerMenu = new ComposerMenu(menus, recettes);
    final ModifierPrixIngredient modifierPrix = new ModifierPrixIngredient(ingredients, recettes, menus);
    final FixerPrixDeVente fixerPrixDeVente = new FixerPrixDeVente(menus, calculateur, new PolitiqueTarifaire(), prixDeVente);

    Ingredient ingredient(String nom, UniteAchat unite, boolean frais, Double prix, Allergene... allergenes) {
        Set<Allergene> declares = allergenes.length == 0 ? EnumSet.noneOf(Allergene.class) : EnumSet.of(allergenes[0], allergenes);
        return enregistrerIngredient.enregistrer(nom, unite, frais, prix, PREMIER_OCTOBRE, declares);
    }

    /** Beurre, œuf, citron, saumon ; sauce hollandaise (1 L) ; plat « Saumon sauce hollandaise » ; menu « Mariage ». */
    Cuisine avecMenuMariage() {
        ingredient("Beurre doux", UniteAchat.KILO, false, 9.80, Allergene.LAIT);
        ingredient("Œuf", UniteAchat.PIECE, false, 0.30, Allergene.OEUFS);
        ingredient("Citron", UniteAchat.PIECE, true, 0.50);
        ingredient("Saumon", UniteAchat.KILO, true, 18.50, Allergene.POISSON);

        Recette hollandaise = composerRecette.commencerPreparation("Sauce hollandaise", 1, UniteMesure.L);
        composerRecette.ajouterIngredient(hollandaise, "Beurre doux", 500, UniteMesure.G);
        composerRecette.ajouterIngredient(hollandaise, "Œuf", 6, UniteMesure.PIECE);
        composerRecette.ajouterIngredient(hollandaise, "Citron", 1, UniteMesure.PIECE);
        composerRecette.valider(hollandaise, PREMIER_OCTOBRE);

        Recette plat = composerRecette.commencerPlat("Saumon sauce hollandaise");
        composerRecette.ajouterIngredient(plat, "Saumon", 150, UniteMesure.G);
        composerRecette.ajouterPreparation(plat, "Sauce hollandaise", 5, UniteMesure.CL);
        composerRecette.valider(plat, PREMIER_OCTOBRE);

        composerMenu.composer("Mariage", List.of("Saumon sauce hollandaise"));
        return this;
    }

    /** Coût d'une portion du plat « Saumon sauce hollandaise » avec les prix du 1er octobre. */
    static double coutPortionSaumon() {
        double saumon = 0.150 * 18.50 * 1.10;
        double hollandaiseAuLitre = 0.5 * 9.80 + 6 * 0.30 + 0.50 * 1.10;
        return saumon + 0.05 * hollandaiseAuLitre;
    }
}
