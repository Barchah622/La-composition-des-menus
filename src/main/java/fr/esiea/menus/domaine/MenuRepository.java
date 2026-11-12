package fr.esiea.menus.domaine;

import java.util.List;
import java.util.Optional;

/** Le contrat de stockage des menus. */
public interface MenuRepository {

    void enregistrer(Menu menu);

    Optional<Menu> trouverParNom(String nom);

    List<Menu> tous();
}
