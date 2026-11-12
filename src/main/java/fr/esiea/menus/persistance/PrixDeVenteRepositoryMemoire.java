package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.PrixDeVente;
import fr.esiea.menus.domaine.PrixDeVenteRepository;

import java.util.ArrayList;
import java.util.List;

/** Prix de vente gardés en mémoire. */
public class PrixDeVenteRepositoryMemoire implements PrixDeVenteRepository {

    private final List<PrixDeVente> prix = new ArrayList<>();

    @Override
    public void enregistrer(PrixDeVente prixDeVente) {
        prix.add(prixDeVente);
    }

    @Override
    public List<PrixDeVente> pourMenu(String nomMenu) {
        return prix.stream().filter(p -> p.menu().equalsIgnoreCase(nomMenu.trim())).toList();
    }
}
