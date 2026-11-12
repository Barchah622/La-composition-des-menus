package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Un produit acheté chez un fournisseur, tel qu'on l'utilise en cuisine (RM14, RM15, RM18). */
public class Ingredient {

    private final String nom;
    private final UniteAchat uniteAchat;
    private final boolean frais;
    private final List<PrixIngredient> historiquePrix = new ArrayList<>();
    private Set<Allergene> allergenes;

    public Ingredient(String nom, UniteAchat uniteAchat, boolean frais) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("un ingrédient doit avoir un nom");
        }
        this.nom = nom.trim();
        this.uniteAchat = Objects.requireNonNull(uniteAchat, "unité d'achat");
        this.frais = frais;
    }

    public String nom() {
        return nom;
    }

    public UniteAchat uniteAchat() {
        return uniteAchat;
    }

    public boolean estFrais() {
        return frais;
    }

    /** Une liste vide veut dire « aucun allergène », déclaré explicitement (RM18). */
    public void renseignerAllergenes(Set<Allergene> allergenesDeclares) {
        this.allergenes = allergenesDeclares.isEmpty()
                ? EnumSet.noneOf(Allergene.class)
                : EnumSet.copyOf(allergenesDeclares);
    }

    public boolean allergenesRenseignes() {
        return allergenes != null;
    }

    public Set<Allergene> allergenes() {
        if (allergenes == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(allergenes);
    }

    /** RM15 : un nouveau prix ne s'applique jamais avant (ni le même jour que) le dernier prix enregistré. */
    public void verifierNouveauPrix(PrixIngredient nouveau) {
        Optional<PrixIngredient> dernier = dernierPrix();
        if (dernier.isEmpty()) {
            return;
        }
        if (!nouveau.dateApplication().isAfter(dernier.get().dateApplication())) {
            throw new RegleMetierException("RM15", "le nouveau prix de « " + nom + " » doit s'appliquer après le "
                    + dernier.get().dateApplication() + ", date du dernier prix enregistré");
        }
    }

    public void appliquerPrix(PrixIngredient nouveau) {
        verifierNouveauPrix(nouveau);
        historiquePrix.add(nouveau);
    }

    public Optional<PrixIngredient> prixEnVigueur(LocalDate date) {
        PrixIngredient enVigueur = null;
        for (PrixIngredient prix : historiquePrix) {
            if (!prix.dateApplication().isAfter(date)) {
                enVigueur = prix;
            }
        }
        return Optional.ofNullable(enVigueur);
    }

    public Optional<PrixIngredient> dernierPrix() {
        if (historiquePrix.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(historiquePrix.get(historiquePrix.size() - 1));
    }

    public List<PrixIngredient> historiquePrix() {
        return List.copyOf(historiquePrix);
    }

    @Override
    public boolean equals(Object autre) {
        return autre instanceof Ingredient ingredient && cle().equals(ingredient.cle());
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
