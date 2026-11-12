package fr.esiea.menus.domaine;

import java.util.List;

/** Les recettes et les menus dont le coût change après un changement de prix. */
public record ImpactPrix(List<Recette> recettes, List<Menu> menus) {

    public ImpactPrix {
        recettes = List.copyOf(recettes);
        menus = List.copyOf(menus);
    }
}
