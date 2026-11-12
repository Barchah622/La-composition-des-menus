package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Décide si la liste des allergènes d'un menu est complète pour être remise au client (RM1, RM21, RM22). */
public class VerificateurAllergenes {

    public ListeAllergenesMenu etablirListe(Menu menu, LocalDate date) {
        menu.exigerAuMoinsUnPlat();
        List<String> aCompleter = menu.plats().stream()
                .flatMap(plat -> plat.ingredientsSansAllergenes().stream())
                .map(Ingredient::nom)
                .distinct()
                .toList();
        if (!aCompleter.isEmpty()) {
            throw new RegleMetierException("RM22", "allergènes à compléter pour " + aCompleter
                    + " : on ne devine jamais un allergène");
        }
        Map<String, Set<Allergene>> parPlat = new LinkedHashMap<>();
        for (Recette plat : menu.plats()) {
            parPlat.put(plat.nom(), plat.allergenes());
        }
        return new ListeAllergenesMenu(menu.nom(), date, parPlat);
    }
}
