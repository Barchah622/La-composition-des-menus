package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** La liste datée des allergènes d'un menu, telle qu'on la remet au client (RM21, RM24). */
public class ListeAllergenesMenu {

    private final String menu;
    private final LocalDate date;
    private final Map<String, Set<Allergene>> allergenesParPlat;
    private boolean aRevoir;

    public ListeAllergenesMenu(String menu, LocalDate date, Map<String, Set<Allergene>> allergenesParPlat) {
        this.menu = menu;
        this.date = date;
        this.allergenesParPlat = Collections.unmodifiableMap(new LinkedHashMap<>(allergenesParPlat));
    }

    /** RM21 : un menu contient un allergène dès qu'un seul de ses plats le contient. */
    public Set<Allergene> allergenesDuMenu() {
        Set<Allergene> tous = EnumSet.noneOf(Allergene.class);
        allergenesParPlat.values().forEach(tous::addAll);
        return tous;
    }

    public boolean aucunAllergene() {
        return allergenesDuMenu().isEmpty();
    }

    public void marquerARevoir() {
        aRevoir = true;
    }

    public boolean aRevoir() {
        return aRevoir;
    }

    public String menu() {
        return menu;
    }

    public LocalDate date() {
        return date;
    }

    public Map<String, Set<Allergene>> allergenesParPlat() {
        return allergenesParPlat;
    }
}
