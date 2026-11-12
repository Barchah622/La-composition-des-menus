package fr.esiea.menus.console;

import fr.esiea.menus.domaine.Allergene;
import fr.esiea.menus.domaine.ComposerRecette;
import fr.esiea.menus.domaine.EnregistrerIngredient;
import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.UniteAchat;
import fr.esiea.menus.domaine.UniteMesure;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Un jeu de données de démonstration, chargé en passant par les use cases quand la base est vide. */
public final class DonneesDeDemo {

    static final LocalDate DATE_DES_PRIX = LocalDate.of(2026, 10, 1);
    private static final Set<Allergene> AUCUN = EnumSet.noneOf(Allergene.class);

    private DonneesDeDemo() {
    }

    public static boolean chargerSiVide(Application application) {
        if (!application.ingredients().tous().isEmpty()) {
            return false;
        }
        enregistrerIngredients(application.enregistrerIngredient());
        composerRecettes(application.composerRecette());
        application.composerMenu().composer("Mariage Dupont",
                List.of("Velouté de pommes de terre", "Saumon sauce hollandaise", "Fondant au chocolat"));
        return true;
    }

    private static void enregistrerIngredients(EnregistrerIngredient uc1) {
        uc1.enregistrer("Beurre doux", UniteAchat.KILO, false, 9.80, DATE_DES_PRIX, EnumSet.of(Allergene.LAIT));
        uc1.enregistrer("Œuf", UniteAchat.PIECE, false, 0.30, DATE_DES_PRIX, EnumSet.of(Allergene.OEUFS));
        uc1.enregistrer("Citron", UniteAchat.PIECE, true, 0.50, DATE_DES_PRIX, AUCUN);
        uc1.enregistrer("Saumon", UniteAchat.KILO, true, 18.50, DATE_DES_PRIX, EnumSet.of(Allergene.POISSON));
        uc1.enregistrer("Pomme de terre", UniteAchat.KILO, true, 1.20, DATE_DES_PRIX, AUCUN);
        uc1.enregistrer("Crème liquide", UniteAchat.LITRE, false, 4.20, DATE_DES_PRIX, EnumSet.of(Allergene.LAIT));
        uc1.enregistrer("Farine", UniteAchat.KILO, false, 1.10, DATE_DES_PRIX, EnumSet.of(Allergene.GLUTEN));
        uc1.enregistrer("Sucre", UniteAchat.KILO, false, 1.50, DATE_DES_PRIX, AUCUN);
        uc1.enregistrer("Chocolat noir", UniteAchat.KILO, false, 12.00, DATE_DES_PRIX, EnumSet.of(Allergene.LAIT, Allergene.SOJA));
        uc1.enregistrer("Sel", UniteAchat.KILO, false, 0.80, DATE_DES_PRIX, AUCUN);
    }

    private static void composerRecettes(ComposerRecette uc2) {
        Recette hollandaise = uc2.commencerPreparation("Sauce hollandaise", 1, UniteMesure.L);
        uc2.ajouterIngredient(hollandaise, "Beurre doux", 500, UniteMesure.G);
        uc2.ajouterIngredient(hollandaise, "Œuf", 6, UniteMesure.PIECE);
        uc2.ajouterIngredient(hollandaise, "Citron", 1, UniteMesure.PIECE);
        uc2.valider(hollandaise, DATE_DES_PRIX);

        Recette veloute = uc2.commencerPlat("Velouté de pommes de terre");
        uc2.ajouterIngredient(veloute, "Pomme de terre", 150, UniteMesure.G);
        uc2.ajouterIngredient(veloute, "Crème liquide", 5, UniteMesure.CL);
        uc2.ajouterIngredient(veloute, "Sel", 2, UniteMesure.G);
        uc2.valider(veloute, DATE_DES_PRIX);

        Recette saumon = uc2.commencerPlat("Saumon sauce hollandaise");
        uc2.ajouterIngredient(saumon, "Saumon", 150, UniteMesure.G);
        uc2.ajouterPreparation(saumon, "Sauce hollandaise", 5, UniteMesure.CL);
        uc2.ajouterIngredient(saumon, "Pomme de terre", 200, UniteMesure.G);
        uc2.valider(saumon, DATE_DES_PRIX);

        Recette fondant = uc2.commencerPlat("Fondant au chocolat");
        uc2.ajouterIngredient(fondant, "Chocolat noir", 50, UniteMesure.G);
        uc2.ajouterIngredient(fondant, "Beurre doux", 40, UniteMesure.G);
        uc2.ajouterIngredient(fondant, "Œuf", 1, UniteMesure.PIECE);
        uc2.ajouterIngredient(fondant, "Farine", 20, UniteMesure.G);
        uc2.ajouterIngredient(fondant, "Sucre", 30, UniteMesure.G);
        uc2.valider(fondant, DATE_DES_PRIX);
    }
}
