package fr.esiea.menus.console;

import fr.esiea.menus.domaine.CalculateurCoutRevient;
import fr.esiea.menus.domaine.CalculerCoutMenu;
import fr.esiea.menus.domaine.CatalogueIngredients;
import fr.esiea.menus.domaine.CatalogueRecettes;
import fr.esiea.menus.domaine.ComposerMenu;
import fr.esiea.menus.domaine.ComposerRecette;
import fr.esiea.menus.domaine.EnregistrerIngredient;
import fr.esiea.menus.domaine.EtablirAllergenesMenu;
import fr.esiea.menus.domaine.FixerPrixDeVente;
import fr.esiea.menus.domaine.IngredientRepository;
import fr.esiea.menus.domaine.MenuRepository;
import fr.esiea.menus.domaine.MettreAJourPrixIngredient;
import fr.esiea.menus.domaine.ModifierPrixIngredient;
import fr.esiea.menus.domaine.PolitiqueTarifaire;
import fr.esiea.menus.domaine.PrixDeVenteRepository;
import fr.esiea.menus.domaine.RecetteRepository;
import fr.esiea.menus.domaine.VerificateurAllergenes;

/** Les 8 use cases, assemblés une fois pour toutes à partir des Repository choisis par le Main. */
public record Application(
        EnregistrerIngredient enregistrerIngredient,
        ComposerRecette composerRecette,
        MettreAJourPrixIngredient mettreAJourPrix,
        CalculerCoutMenu calculerCoutMenu,
        EtablirAllergenesMenu etablirAllergenes,
        ComposerMenu composerMenu,
        ModifierPrixIngredient modifierPrix,
        FixerPrixDeVente fixerPrixDeVente,
        CatalogueIngredients ingredients,
        CatalogueRecettes recettes,
        MenuRepository menus,
        PrixDeVenteRepository prixDeVente) {

    public static Application assembler(IngredientRepository depotIngredients, RecetteRepository depotRecettes,
                                        MenuRepository menus, PrixDeVenteRepository prixDeVente) {
        CatalogueIngredients ingredients = new CatalogueIngredients(depotIngredients);
        CatalogueRecettes recettes = new CatalogueRecettes(depotRecettes);
        CalculateurCoutRevient calculateur = new CalculateurCoutRevient();
        return new Application(
                new EnregistrerIngredient(ingredients),
                new ComposerRecette(recettes, ingredients, calculateur),
                new MettreAJourPrixIngredient(ingredients, recettes, menus),
                new CalculerCoutMenu(menus, calculateur),
                new EtablirAllergenesMenu(menus, new VerificateurAllergenes()),
                new ComposerMenu(menus, recettes),
                new ModifierPrixIngredient(ingredients, recettes, menus),
                new FixerPrixDeVente(menus, calculateur, new PolitiqueTarifaire(), prixDeVente),
                ingredients, recettes, menus, prixDeVente);
    }
}
