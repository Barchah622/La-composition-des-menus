package fr.esiea.menus.domaine;

import java.util.List;

/** Le contrat de stockage des prix de vente fixés par le Commercial. */
public interface PrixDeVenteRepository {

    void enregistrer(PrixDeVente prixDeVente);

    List<PrixDeVente> pourMenu(String nomMenu);
}
