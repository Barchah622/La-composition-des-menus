package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.RecetteRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Recettes gardées en mémoire. */
public class RecetteRepositoryMemoire implements RecetteRepository {

    private final Map<String, Recette> parNom = new LinkedHashMap<>();

    @Override
    public void enregistrer(Recette recette) {
        parNom.put(recette.nom().toLowerCase(Locale.ROOT), recette);
    }

    @Override
    public Optional<Recette> trouverParNom(String nom) {
        return Optional.ofNullable(parNom.get(nom.toLowerCase(Locale.ROOT)));
    }

    @Override
    public List<Recette> toutes() {
        return new ArrayList<>(parNom.values());
    }
}
