package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Le résultat d'un calcul de coût de revient d'un menu, à une date et pour un nombre de couverts. */
public record CoutCalcule(String menu, LocalDate date, int couverts, Map<String, Double> coutPortionParPlat,
                          double coutParCouvert, double coutTotal, Set<String> ingredientsSansPrix) {

    public CoutCalcule {
        coutPortionParPlat = Collections.unmodifiableMap(new LinkedHashMap<>(coutPortionParPlat));
        ingredientsSansPrix = Set.copyOf(ingredientsSansPrix);
    }

    public boolean complet() {
        return ingredientsSansPrix.isEmpty();
    }
}
