package fr.esiea.menus.domaine;

import java.util.List;
import java.util.Optional;

/** Le contrat de stockage des recettes (plats et préparations). */
public interface RecetteRepository {

    void enregistrer(Recette recette);

    Optional<Recette> trouverParNom(String nom);

    List<Recette> toutes();
}
