package fr.esiea.menus.domaine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Un plat (pour une portion) ou une préparation (pour une quantité produite), décrit par ses composants (RM6, RM12). */
public class Recette {

    private static final GardienComposition GARDIEN = new GardienComposition();
    private static final double UNE_PORTION = 1;

    private final String nom;
    private final TypeRecette type;
    private final double quantiteProduite;
    private final UniteMesure uniteProduite;
    private final List<Composant> composants = new ArrayList<>();

    private Recette(String nom, TypeRecette type, double quantiteProduite, UniteMesure uniteProduite) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("une recette doit avoir un nom");
        }
        this.nom = nom.trim();
        this.type = type;
        this.quantiteProduite = quantiteProduite;
        this.uniteProduite = uniteProduite;
    }

    public static Recette plat(String nom) {
        return new Recette(nom, TypeRecette.PLAT, UNE_PORTION, UniteMesure.PIECE);
    }

    public static Recette preparation(String nom, double quantiteProduite, UniteMesure uniteProduite) {
        Objects.requireNonNull(uniteProduite, "unité produite");
        if (Double.isNaN(quantiteProduite) || quantiteProduite <= 0) {
            throw new RegleMetierException("RM8", "la quantité produite par une préparation doit être strictement positive");
        }
        return new Recette(nom, TypeRecette.PREPARATION, quantiteProduite, uniteProduite);
    }

    public void ajouterComposant(Composant composant) {
        if (!GARDIEN.peutEntrer(composant, this)) {
            throw new RegleMetierException("RM7", "« " + composant.nom() + " » contient déjà « " + nom
                    + " » : une préparation ne peut pas se contenir elle-même");
        }
        composants.add(composant);
    }

    public void retirerComposant(String nomComposant) {
        boolean retire = composants.removeIf(composant -> composant.nom().equalsIgnoreCase(nomComposant.trim()));
        if (!retire) {
            throw new ElementInconnuException("composant", nomComposant);
        }
    }

    public void valider() {
        if (composants.isEmpty()) {
            throw new RegleMetierException("RM6", "la recette « " + nom + " » n'a aucun composant");
        }
    }

    /** Vrai si cette recette utilise l'autre, directement ou via une préparation, à n'importe quel niveau. */
    public boolean contient(Recette autre) {
        for (Composant composant : composants) {
            if (composant.estIngredient()) {
                continue;
            }
            Recette sousPreparation = composant.preparation();
            if (sousPreparation.equals(autre) || sousPreparation.contient(autre)) {
                return true;
            }
        }
        return false;
    }

    public boolean utilise(Ingredient ingredient) {
        for (Composant composant : composants) {
            if (composant.estIngredient() && composant.ingredient().equals(ingredient)) {
                return true;
            }
            if (!composant.estIngredient() && composant.preparation().utilise(ingredient)) {
                return true;
            }
        }
        return false;
    }

    /** RM12 : un allergène est retenu dès qu'un seul composant le contient, même une sauce dans une sauce. */
    public Set<Allergene> allergenes() {
        Set<Allergene> trouves = EnumSet.noneOf(Allergene.class);
        for (Composant composant : composants) {
            trouves.addAll(composant.estIngredient()
                    ? composant.ingredient().allergenes()
                    : composant.preparation().allergenes());
        }
        return trouves;
    }

    public List<Ingredient> ingredientsSansAllergenes() {
        Set<Ingredient> manquants = new LinkedHashSet<>();
        for (Composant composant : composants) {
            if (!composant.estIngredient()) {
                manquants.addAll(composant.preparation().ingredientsSansAllergenes());
                continue;
            }
            if (!composant.ingredient().allergenesRenseignes()) {
                manquants.add(composant.ingredient());
            }
        }
        return List.copyOf(manquants);
    }

    public Recette copierSous(String nouveauNom) {
        Recette copie = new Recette(nouveauNom, type, quantiteProduite, uniteProduite);
        copie.composants.addAll(composants);
        return copie;
    }

    public String nom() {
        return nom;
    }

    public TypeRecette type() {
        return type;
    }

    public boolean estPreparation() {
        return type == TypeRecette.PREPARATION;
    }

    public double quantiteProduite() {
        return quantiteProduite;
    }

    public UniteMesure uniteProduite() {
        return uniteProduite;
    }

    public double quantiteProduiteEnUniteAchat() {
        return quantiteProduite * uniteProduite.facteurVersUniteAchat();
    }

    public List<Composant> composants() {
        return List.copyOf(composants);
    }

    @Override
    public boolean equals(Object autre) {
        return autre instanceof Recette recette && cle().equals(recette.cle());
    }

    @Override
    public int hashCode() {
        return cle().hashCode();
    }

    private String cle() {
        return nom.toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return nom;
    }
}
