package fr.esiea.menus.domaine;

import java.util.List;

/**
 * UC6 — Composer un menu.
 * Porteur : Mohamed Amine BARCHAH. Règle : RM1.
 */
public class ComposerMenu {

    private final MenuRepository menus;
    private final CatalogueRecettes recettes;

    public ComposerMenu(MenuRepository menus, CatalogueRecettes recettes) {
        this.menus = menus;
        this.recettes = recettes;
    }

    public List<Recette> platsDisponibles() {
        return recettes.plats();
    }

    public Menu composer(String nom, List<String> nomsDesPlats) {
        if (menus.trouverParNom(nom.trim()).isPresent()) {
            throw new RegleMetierException("UC6", "un menu s'appelle déjà « " + nom.trim() + " »");
        }
        Menu menu = new Menu(nom);
        for (String nomPlat : nomsDesPlats) {
            menu.ajouterPlat(recettes.trouver(nomPlat));
        }
        menu.enregistrer();
        menus.enregistrer(menu);
        return menu;
    }
}
