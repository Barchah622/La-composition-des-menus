package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.PrixDeVente;
import fr.esiea.menus.domaine.PrixDeVenteRepository;
import fr.esiea.menus.domaine.TypeDeClient;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/** Prix de vente fixés par le Commercial, dans PostgreSQL. */
public class PrixDeVenteRepositoryJdbc implements PrixDeVenteRepository {

    private final Connection connexion;

    public PrixDeVenteRepositoryJdbc(Connection connexion) {
        this.connexion = connexion;
    }

    @Override
    public void enregistrer(PrixDeVente prix) {
        Jdbc.miseAJour(connexion,
                "INSERT INTO prix_de_vente (menu_nom, type_client, couverts, prix, cout_revient, date_fixation) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                prix.menu(), prix.typeDeClient().name(), prix.couverts(), prix.prix(), prix.coutRevient(), prix.dateFixation());
    }

    @Override
    public List<PrixDeVente> pourMenu(String nomMenu) {
        return Jdbc.lister(connexion,
                "SELECT menu_nom, type_client, couverts, prix, cout_revient, date_fixation FROM prix_de_vente "
                        + "WHERE LOWER(menu_nom) = LOWER(?) ORDER BY id",
                ligne -> new PrixDeVente(ligne.getString("menu_nom"), TypeDeClient.valueOf(ligne.getString("type_client")),
                        ligne.getInt("couverts"), ligne.getDouble("prix"), ligne.getDouble("cout_revient"),
                        ligne.getObject("date_fixation", LocalDate.class)),
                nomMenu.trim());
    }
}
