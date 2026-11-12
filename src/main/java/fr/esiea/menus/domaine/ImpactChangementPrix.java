package fr.esiea.menus.domaine;

import java.util.Collection;

/** Décide quelles préparations, quels plats et quels menus doivent être recalculés après un changement de prix (RM16). */
public class ImpactChangementPrix {

    public ImpactPrix aRecalculer(Ingredient ingredient, Collection<Recette> recettes, Collection<Menu> menus) {
        return new ImpactPrix(
                recettes.stream().filter(recette -> recette.utilise(ingredient)).toList(),
                menus.stream().filter(menu -> menu.utilise(ingredient)).toList());
    }
}
