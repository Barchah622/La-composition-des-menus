package fr.esiea.menus.domaine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Une suite de plats proposée pour un événement (RM1). */
public class Menu {

    private final String nom;
    private final List<Recette> plats = new ArrayList<>();
    private EtatMenu etat = EtatMenu.EN_COMPOSITION;

    public Menu(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("un menu doit avoir un nom");
        }
        this.nom = nom.trim();
    }

    /** Pour la persistance : recrée un menu tel qu'il a été enregistré. */
    public static Menu reconstituer(String nom, List<Recette> plats, EtatMenu etat) {
        Menu menu = new Menu(nom);
        menu.plats.addAll(plats);
        menu.etat = etat;
        return menu;
    }

    public void ajouterPlat(Recette plat) {
        if (plat.estPreparation()) {
            throw new RegleMetierException("RM1", "« " + plat.nom() + " » est une préparation : un menu ne contient que des plats");
        }
        plats.add(plat);
    }

    public void retirerPlat(String nomPlat) {
        boolean retire = plats.removeIf(plat -> plat.nom().equalsIgnoreCase(nomPlat.trim()));
        if (!retire) {
            throw new ElementInconnuException("plat", nomPlat);
        }
    }

    public void enregistrer() {
        exigerAuMoinsUnPlat();
        etat = EtatMenu.COMPOSE;
    }

    public void exigerAuMoinsUnPlat() {
        if (plats.isEmpty()) {
            throw new RegleMetierException("RM1", "menu vide : « " + nom + " » doit contenir au moins un plat");
        }
    }

    public void marquerChiffre() {
        etat = EtatMenu.CHIFFRE;
    }

    public void marquerTarife() {
        etat = EtatMenu.TARIFE;
    }

    public void marquerARecalculer() {
        etat = EtatMenu.A_RECALCULER;
    }

    public boolean utilise(Ingredient ingredient) {
        return plats.stream().anyMatch(plat -> plat.utilise(ingredient));
    }

    public String nom() {
        return nom;
    }

    public List<Recette> plats() {
        return List.copyOf(plats);
    }

    public EtatMenu etat() {
        return etat;
    }

    @Override
    public boolean equals(Object autre) {
        return autre instanceof Menu menu && nom.toLowerCase(Locale.ROOT).equals(menu.nom.toLowerCase(Locale.ROOT));
    }

    @Override
    public int hashCode() {
        return nom.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return nom;
    }
}
