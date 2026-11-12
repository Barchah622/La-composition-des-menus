package fr.esiea.menus.console;

import fr.esiea.menus.domaine.Allergene;
import fr.esiea.menus.domaine.ApercuModificationPrix;
import fr.esiea.menus.domaine.CoutCalcule;
import fr.esiea.menus.domaine.ElementInconnuException;
import fr.esiea.menus.domaine.FicheRecette;
import fr.esiea.menus.domaine.ImpactPrix;
import fr.esiea.menus.domaine.Ingredient;
import fr.esiea.menus.domaine.ListeAllergenesMenu;
import fr.esiea.menus.domaine.Menu;
import fr.esiea.menus.domaine.ModificationPrix;
import fr.esiea.menus.domaine.PrixDeVente;
import fr.esiea.menus.domaine.PrixIngredient;
import fr.esiea.menus.domaine.PropositionPrix;
import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.RegleMetierException;
import fr.esiea.menus.domaine.ResultatMiseAJourPrix;
import fr.esiea.menus.domaine.TarifFournisseur;
import fr.esiea.menus.domaine.TypeDeClient;
import fr.esiea.menus.domaine.UniteAchat;
import fr.esiea.menus.domaine.UniteMesure;
import fr.esiea.menus.persistance.PersistanceException;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Le menu console : un choix = un use case. Il appelle le domaine et ne calcule rien lui-même. */
public class MenuConsole {

    private static final String CHOIX_QUITTER = "0";

    private final Application application;
    private final Saisie saisie;
    private final PrintStream sortie;
    private final Supplier<LocalDate> aujourdhui;

    public MenuConsole(Application application, Scanner entree, PrintStream sortie, Supplier<LocalDate> aujourdhui) {
        this.application = application;
        this.saisie = new Saisie(entree, sortie);
        this.sortie = sortie;
        this.aujourdhui = aujourdhui;
    }

    public void boucle() {
        while (true) {
            afficherChoix();
            String choix;
            try {
                choix = saisie.texte("Votre choix");
            } catch (FinDeSaisie e) {
                return;
            }
            if (choix.equals(CHOIX_QUITTER)) {
                sortie.println("Au revoir.");
                return;
            }
            if (!executerSansPlanter(choix)) {
                return;
            }
        }
    }

    /** Renvoie faux si l'entrée est fermée en cours de use case. */
    private boolean executerSansPlanter(String choix) {
        try {
            executer(choix);
        } catch (FinDeSaisie e) {
            return false;
        } catch (RegleMetierException | ElementInconnuException | IllegalArgumentException | PersistanceException e) {
            sortie.println("  ✗ Refusé : " + e.getMessage());
        }
        return true;
    }

    private void afficherChoix() {
        sortie.println();
        sortie.println("=== Traiteur · Composition des menus ===");
        sortie.println(" 1. Enregistrer un ingrédient                 (UC1 · Manele)");
        sortie.println(" 2. Composer une recette                      (UC2 · Abir)");
        sortie.println(" 3. Recevoir un tarif du fournisseur          (UC3 · Abir)");
        sortie.println(" 4. Calculer le coût de revient d'un menu     (UC4 · Manele)");
        sortie.println(" 5. Établir la liste des allergènes d'un menu (UC5 · Tania)");
        sortie.println(" 6. Composer un menu                          (UC6 · Mohamed Amine)");
        sortie.println(" 7. Modifier le prix d'un ingrédient          (UC7 · Tania)");
        sortie.println(" 8. Fixer le prix de vente d'un menu          (UC8 · Mohamed Amine)");
        sortie.println(" 9. Voir le catalogue");
        sortie.println(" 0. Quitter");
    }

    private void executer(String choix) {
        switch (choix) {
            case "1" -> enregistrerIngredient();
            case "2" -> composerRecette();
            case "3" -> recevoirTarifFournisseur();
            case "4" -> calculerCoutMenu();
            case "5" -> etablirAllergenes();
            case "6" -> composerMenu();
            case "7" -> modifierPrix();
            case "8" -> fixerPrixDeVente();
            case "9" -> afficherCatalogue();
            default -> sortie.println("Choix inconnu");
        }
    }

    // ---------- UC1 · Manele ----------

    private void enregistrerIngredient() {
        String nom = saisie.texteObligatoire("Nom de l'ingrédient");
        UniteAchat unite = saisie.choisir("Unité d'achat", List.of(UniteAchat.values()), UniteAchat::libelle);
        boolean frais = saisie.ouiNon("Ingrédient frais (légume, poisson, viande) ?");
        Double prix = saisie.nombreOuVide("Prix d'achat en € par " + unite.libelle() + " [vide = pas encore connu]");
        LocalDate date = prix == null ? aujourdhui.get() : saisie.date("Prix valable à partir du", aujourdhui.get());
        Set<Allergene> allergenes = lireAllergenes();
        Ingredient ingredient = application.enregistrerIngredient().enregistrer(nom, unite, frais, prix, date, allergenes);
        sortie.println("  ✓ « " + ingredient.nom() + " » enregistré" + (prix == null ? " (prix manquant)" : "")
                + (allergenes == null ? " (allergènes à compléter)" : "") + ".");
    }

    private Set<Allergene> lireAllergenes() {
        Allergene[] liste = Allergene.values();
        for (int i = 0; i < liste.length; i++) {
            sortie.println("   " + (i + 1) + ". " + liste[i].libelle());
        }
        while (true) {
            String reponse = saisie.texte("Allergènes (numéros séparés par des virgules, 0 = aucun, vide = à compléter)");
            if (reponse.isEmpty()) {
                return null;
            }
            if (reponse.equals("0")) {
                return EnumSet.noneOf(Allergene.class);
            }
            try {
                Set<Allergene> choisis = EnumSet.noneOf(Allergene.class);
                for (String numero : reponse.split(",")) {
                    choisis.add(liste[Integer.parseInt(numero.trim()) - 1]);
                }
                return choisis;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                sortie.println("  Numéros entre 1 et " + liste.length + " attendus.");
            }
        }
    }

    // ---------- UC2 · Abir ----------

    private void composerRecette() {
        Recette recette = commencerRecette();
        sortie.println("  Recette « " + recette.nom() + " » ouverte.");
        while (true) {
            afficherComposition(recette);
            String action = saisie.texte("[i] ingrédient · [p] préparation · [r] retirer · [v] valider · [a] abandonner")
                    .toLowerCase(Locale.ROOT);
            if (action.equals("a")) {
                sortie.println("  Recette abandonnée.");
                return;
            }
            if (!action.equals("v")) {
                modifierComposition(recette, action);
                continue;
            }
            if (validerRecette(recette)) {
                return;
            }
        }
    }

    private Recette commencerRecette() {
        String genre = saisie.texte("1. Nouveau plat · 2. Nouvelle préparation · 3. Variante d'une recette · 4. Reprendre une recette");
        return switch (genre) {
            case "2" -> application.composerRecette().commencerPreparation(
                    saisie.texteObligatoire("Nom de la préparation"),
                    saisie.nombre("Quantité produite (ex. 1)"),
                    UniteMesure.depuisSymbole(saisie.texteObligatoire("Unité produite (kg, g, L, cL, mL, pièce)")));
            case "3" -> application.composerRecette().commencerVariante(
                    choisirRecette(application.recettes().toutes()).nom(),
                    saisie.texteObligatoire("Nom de la variante"));
            case "4" -> application.composerRecette().reprendre(choisirRecette(application.recettes().toutes()).nom());
            default -> application.composerRecette().commencerPlat(saisie.texteObligatoire("Nom du plat"));
        };
    }

    private void modifierComposition(Recette recette, String action) {
        try {
            switch (action) {
                case "i" -> application.composerRecette().ajouterIngredient(recette,
                        saisie.texteObligatoire("Ingrédient"), saisie.nombre("Quantité"),
                        UniteMesure.depuisSymbole(saisie.texteObligatoire("Unité (kg, g, L, cL, mL, pièce)")));
                case "p" -> application.composerRecette().ajouterPreparation(recette,
                        choisirRecette(application.recettes().preparations()).nom(), saisie.nombre("Quantité"),
                        UniteMesure.depuisSymbole(saisie.texteObligatoire("Unité (kg, g, L, cL, mL, pièce)")));
                case "r" -> recette.retirerComposant(saisie.texteObligatoire("Composant à retirer"));
                default -> sortie.println("Choix inconnu");
            }
        } catch (RegleMetierException | ElementInconnuException | IllegalArgumentException e) {
            sortie.println("  ✗ Refusé : " + e.getMessage());
        }
    }

    private boolean validerRecette(Recette recette) {
        try {
            FicheRecette fiche = application.composerRecette().valider(recette, aujourdhui.get());
            sortie.println("  ✓ Recette « " + recette.nom() + " » enregistrée.");
            sortie.println("    Coût de revient : " + euros(fiche.cout().montant())
                    + (recette.estPreparation() ? " pour " + nombre(recette.quantiteProduite()) + " " + recette.uniteProduite().symbole() : " la portion"));
            sortie.println("    Allergènes : " + libelles(fiche.allergenes()));
            if (!fiche.cout().complet()) {
                sortie.println("    ⚠ Coût incomplet, prix manquant pour : " + fiche.cout().ingredientsSansPrix());
            }
            if (!fiche.ingredientsSansAllergenes().isEmpty()) {
                sortie.println("    ⚠ Allergènes à compléter pour : " + fiche.ingredientsSansAllergenes());
            }
            return true;
        } catch (RegleMetierException e) {
            sortie.println("  ✗ Refusé : " + e.getMessage());
            return false;
        }
    }

    private void afficherComposition(Recette recette) {
        if (recette.composants().isEmpty()) {
            sortie.println("  (aucun composant)");
            return;
        }
        recette.composants().forEach(composant -> sortie.println("   - " + nombre(composant.quantite()) + " "
                + composant.unite().symbole() + " " + composant.nom() + (composant.estIngredient() ? "" : " (préparation)")));
    }

    // ---------- UC3 · Abir ----------

    private void recevoirTarifFournisseur() {
        Ingredient ingredient = choisirIngredient();
        double prix = saisie.nombre("Nouveau prix transmis (€)");
        UniteAchat unite = UniteAchat.depuisLibelle(saisie.texteObligatoire("Unité du tarif (kilo, litre, pièce)"));
        LocalDate date = saisie.date("Date d'application", aujourdhui.get());
        TarifFournisseur tarif = new TarifFournisseur(ingredient.nom(), prix, unite, date);
        ResultatMiseAJourPrix resultat = application.mettreAJourPrix().recevoir(tarif);
        if (!resultat.applique()) {
            sortie.println("  ⚠ Variation anormale : " + euros(resultat.ancienPrix().orElseThrow().valeur()) + " → "
                    + euros(resultat.nouveauPrix().valeur()) + ". Mise à jour en attente.");
            if (!saisie.ouiNon("Le Chef de production confirme-t-il ce prix ?")) {
                sortie.println("  Ancien prix conservé.");
                return;
            }
            resultat = application.mettreAJourPrix().confirmer(tarif);
        }
        sortie.println("  ✓ Prix de « " + resultat.ingredient() + " » : " + euros(resultat.nouveauPrix().valeur())
                + " à partir du " + resultat.nouveauPrix().dateApplication()
                + resultat.ancienPrix().map(ancien -> " (ancien : " + euros(ancien.valeur()) + ", gardé dans l'historique)").orElse(""));
        afficherImpact(resultat.impact());
    }

    private void afficherImpact(ImpactPrix impact) {
        sortie.println("    Recettes dont le coût change : " + noms(impact.recettes()));
        sortie.println("    Menus à recalculer : " + noms(impact.menus()));
    }

    // ---------- UC4 · Manele ----------

    private void calculerCoutMenu() {
        Menu menu = choisirMenu();
        int couverts = saisie.entier("Nombre de couverts");
        LocalDate date = saisie.date("Prix en vigueur au", aujourdhui.get());
        CoutCalcule cout = application.calculerCoutMenu().calculer(menu.nom(), couverts, date);
        cout.coutPortionParPlat().forEach((plat, montant) -> sortie.println("   - " + plat + " : " + euros(montant) + " la portion"));
        sortie.println("  Coût par couvert : " + euros(cout.coutParCouvert()));
        sortie.println("  Coût total pour " + couverts + " couverts : " + euros(cout.coutTotal()) + " (prix du " + date + ")");
        if (!cout.complet()) {
            sortie.println("  ⚠ Coût incomplet, prix manquant pour : " + cout.ingredientsSansPrix());
        }
    }

    // ---------- UC5 · Tania ----------

    private void etablirAllergenes() {
        Menu menu = choisirMenu();
        ListeAllergenesMenu liste = application.etablirAllergenes().etablir(menu.nom(), aujourdhui.get());
        liste.allergenesParPlat().forEach((plat, allergenes) -> sortie.println("   - " + plat + " : " + libelles(allergenes)));
        sortie.println("  Menu « " + liste.menu() + " » : " + (liste.aucunAllergene()
                ? "aucun des 14 allergènes réglementaires"
                : libelles(liste.allergenesDuMenu())) + " (liste du " + liste.date() + ")");
    }

    // ---------- UC6 · Mohamed Amine ----------

    private void composerMenu() {
        String nom = saisie.texteObligatoire("Nom du menu");
        List<Recette> plats = application.composerMenu().platsDisponibles();
        for (int i = 0; i < plats.size(); i++) {
            sortie.println("   " + (i + 1) + ". " + plats.get(i).nom());
        }
        String reponse = saisie.texte("Plats (numéros séparés par des virgules, dans l'ordre du service)");
        List<String> choisis = reponse.isBlank() ? List.of() : Arrays.stream(reponse.split(","))
                .map(String::trim)
                .map(numero -> platNumero(plats, numero))
                .toList();
        Menu menu = application.composerMenu().composer(nom, choisis);
        sortie.println("  ✓ Menu « " + menu.nom() + " » enregistré : " + noms(menu.plats()));
    }

    private static String platNumero(List<Recette> plats, String numero) {
        try {
            return plats.get(Integer.parseInt(numero) - 1).nom();
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new ElementInconnuException("plat", numero);
        }
    }

    // ---------- UC7 · Tania ----------

    private void modifierPrix() {
        Ingredient ingredient = choisirIngredient();
        sortie.println("  Prix en vigueur : " + ingredient.dernierPrix()
                .map(prix -> euros(prix.valeur()) + " le " + ingredient.uniteAchat().libelle() + " depuis le " + prix.dateApplication())
                .orElse("aucun (prix manquant)"));
        double nouveauPrix = saisie.nombre("Nouveau prix (€ par " + ingredient.uniteAchat().libelle() + ")");
        LocalDate date = saisie.date("Date d'application", aujourdhui.get());
        String justificatif = saisie.texte("Référence du justificatif (facture, tarif)");
        ModificationPrix modification = new ModificationPrix(ingredient.nom(), nouveauPrix, date, justificatif);
        ApercuModificationPrix apercu = application.modifierPrix().apercu(modification);
        apercu.ecart().ifPresent(ecart -> sortie.println("  Écart avec l'ancien prix : " + euros(ecart)));
        afficherImpact(apercu.impact());
        boolean variationVerifiee = false;
        if (apercu.confirmationRequise()) {
            sortie.println("  ⚠ Écart de plus de 50 % : ressaisissez le prix pour le confirmer.");
            variationVerifiee = saisie.nombre("Nouveau prix, à nouveau") == nouveauPrix;
        }
        if (!saisie.ouiNon("Confirmer la modification ?")) {
            sortie.println("  Rien n'a été modifié.");
            return;
        }
        application.modifierPrix().appliquer(modification, variationVerifiee);
        sortie.println("  ✓ Nouveau prix enregistré, l'ancien est gardé dans l'historique.");
    }

    // ---------- UC8 · Mohamed Amine ----------

    private void fixerPrixDeVente() {
        Menu menu = choisirMenu();
        int convives = saisie.entier("Nombre de convives");
        TypeDeClient type = saisie.choisir("Type de client", List.of(TypeDeClient.values()),
                client -> client.libelle() + " (× " + nombre(client.coefficient()) + ")");
        PropositionPrix proposition = application.fixerPrixDeVente().proposer(menu.nom(), convives, type, aujourdhui.get());
        sortie.println("  Coût de revient total : " + euros(proposition.cout().coutTotal()));
        sortie.println("  Prix théorique (" + type.libelle() + ") : " + euros(proposition.prixTheorique()));
        Double ajuste = saisie.nombreOuVide("Prix final [vide = prix théorique]");
        double prixFinal = ajuste == null ? proposition.prixTheorique() : ajuste;
        sortie.println("  Marge : " + euros(proposition.margeSi(prixFinal)));
        if (!saisie.ouiNon("Valider ce prix ?")) {
            sortie.println("  Prix non enregistré.");
            return;
        }
        PrixDeVente prix = application.fixerPrixDeVente().fixer(proposition, prixFinal, aujourdhui.get());
        sortie.println("  ✓ Prix de vente de « " + prix.menu() + " » pour " + prix.couverts() + " convives ("
                + prix.typeDeClient().libelle() + ") : " + euros(prix.prix()));
    }

    // ---------- Catalogue ----------

    private void afficherCatalogue() {
        sortie.println("Ingrédients :");
        for (Ingredient ingredient : application.ingredients().tous()) {
            String prix = ingredient.prixEnVigueur(aujourdhui.get()).map(PrixIngredient::valeur).map(MenuConsole::euros)
                    .orElse("prix manquant");
            String allergenes = ingredient.allergenesRenseignes() ? libelles(ingredient.allergenes()) : "allergènes à compléter";
            sortie.println("   - " + ingredient.nom() + " · " + prix + " / " + ingredient.uniteAchat().libelle()
                    + (ingredient.estFrais() ? " · frais" : "") + " · " + allergenes);
        }
        sortie.println("Préparations : " + noms(application.recettes().preparations()));
        sortie.println("Plats : " + noms(application.recettes().plats()));
        sortie.println("Menus :");
        application.menus().tous().forEach(menu -> sortie.println("   - " + menu.nom() + " [" + menu.etat().libelle() + "] : "
                + noms(menu.plats())));
    }

    // ---------- Aides d'affichage ----------

    private Ingredient choisirIngredient() {
        return saisie.choisir("Ingrédient", application.ingredients().tous(), Ingredient::nom);
    }

    private Recette choisirRecette(List<Recette> recettes) {
        return saisie.choisir("Recette", recettes, Recette::nom);
    }

    private Menu choisirMenu() {
        return saisie.choisir("Menu", application.menus().tous(), menu -> menu.nom() + " [" + menu.etat().libelle() + "]");
    }

    private static String euros(double montant) {
        return String.format(Locale.FRANCE, "%.2f €", montant);
    }

    private static String nombre(double valeur) {
        return valeur == Math.rint(valeur) ? String.valueOf((long) valeur) : String.valueOf(valeur).replace('.', ',');
    }

    private static String libelles(Set<Allergene> allergenes) {
        if (allergenes.isEmpty()) {
            return "aucun";
        }
        return allergenes.stream().sorted().map(Allergene::libelle).collect(Collectors.joining(", "));
    }

    private static String noms(List<?> elements) {
        if (elements.isEmpty()) {
            return "aucun";
        }
        return elements.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
