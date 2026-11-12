package fr.esiea.menus.domaine;

import java.util.List;

/** Range les recettes et refuse un nom déjà pris (RM9). */
public class CatalogueRecettes {

    private final RecetteRepository depot;

    public CatalogueRecettes(RecetteRepository depot) {
        this.depot = depot;
    }

    public void exigerNomLibre(String nom) {
        if (depot.trouverParNom(nom.trim()).isPresent()) {
            throw new RegleMetierException("RM9", "une recette s'appelle déjà « " + nom.trim() + " »");
        }
    }

    public void ajouter(Recette recette) {
        exigerNomLibre(recette.nom());
        depot.enregistrer(recette);
    }

    public void mettreAJour(Recette recette) {
        depot.enregistrer(recette);
    }

    public boolean existe(String nom) {
        return depot.trouverParNom(nom.trim()).isPresent();
    }

    public Recette trouver(String nom) {
        return depot.trouverParNom(nom.trim()).orElseThrow(() -> new ElementInconnuException("recette", nom));
    }

    public List<Recette> toutes() {
        return depot.toutes();
    }

    public List<Recette> plats() {
        return toutes().stream().filter(recette -> !recette.estPreparation()).toList();
    }

    public List<Recette> preparations() {
        return toutes().stream().filter(Recette::estPreparation).toList();
    }
}
