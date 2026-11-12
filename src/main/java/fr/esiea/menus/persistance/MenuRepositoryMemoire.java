package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Menu;
import fr.esiea.menus.domaine.MenuRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Menus gardés en mémoire. */
public class MenuRepositoryMemoire implements MenuRepository {

    private final Map<String, Menu> parNom = new LinkedHashMap<>();

    @Override
    public void enregistrer(Menu menu) {
        parNom.put(menu.nom().toLowerCase(Locale.ROOT), menu);
    }

    @Override
    public Optional<Menu> trouverParNom(String nom) {
        return Optional.ofNullable(parNom.get(nom.toLowerCase(Locale.ROOT)));
    }

    @Override
    public List<Menu> tous() {
        return new ArrayList<>(parNom.values());
    }
}
