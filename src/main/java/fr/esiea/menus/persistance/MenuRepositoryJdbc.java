package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.ElementInconnuException;
import fr.esiea.menus.domaine.EtatMenu;
import fr.esiea.menus.domaine.Menu;
import fr.esiea.menus.domaine.MenuRepository;
import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.RecetteRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Menus et leurs plats dans PostgreSQL. */
public class MenuRepositoryJdbc implements MenuRepository {

    private record LigneMenu(String nom, String etat) {
    }

    private final Connection connexion;
    private final RecetteRepository recettes;

    public MenuRepositoryJdbc(Connection connexion, RecetteRepository recettes) {
        this.connexion = connexion;
        this.recettes = recettes;
    }

    @Override
    public void enregistrer(Menu menu) {
        int modifiees = Jdbc.miseAJour(connexion, "UPDATE menu SET etat = ? WHERE nom = ?", menu.etat().name(), menu.nom());
        if (modifiees == 0) {
            Jdbc.miseAJour(connexion, "INSERT INTO menu (nom, etat) VALUES (?, ?)", menu.nom(), menu.etat().name());
        }
        Jdbc.miseAJour(connexion, "DELETE FROM menu_plat WHERE menu_nom = ?", menu.nom());
        List<Recette> plats = menu.plats();
        for (int ordre = 0; ordre < plats.size(); ordre++) {
            Jdbc.miseAJour(connexion, "INSERT INTO menu_plat (menu_nom, ordre, recette_nom) VALUES (?, ?, ?)",
                    menu.nom(), ordre, plats.get(ordre).nom());
        }
    }

    @Override
    public Optional<Menu> trouverParNom(String nom) {
        List<LigneMenu> lignes = Jdbc.lister(connexion, "SELECT nom, etat FROM menu WHERE LOWER(nom) = LOWER(?)",
                ligne -> new LigneMenu(ligne.getString("nom"), ligne.getString("etat")), nom);
        return lignes.stream().findFirst().map(this::reconstituer);
    }

    @Override
    public List<Menu> tous() {
        List<String> noms = Jdbc.lister(connexion, "SELECT nom FROM menu ORDER BY nom", ligne -> ligne.getString("nom"));
        return noms.stream().map(this::trouverParNom).flatMap(Optional::stream).toList();
    }

    private Menu reconstituer(LigneMenu ligne) {
        List<String> nomsPlats = Jdbc.lister(connexion,
                "SELECT recette_nom FROM menu_plat WHERE menu_nom = ? ORDER BY ordre",
                resultat -> resultat.getString("recette_nom"), ligne.nom());
        List<Recette> plats = nomsPlats.stream()
                .map(nomPlat -> recettes.trouverParNom(nomPlat).orElseThrow(() -> new ElementInconnuException("plat", nomPlat)))
                .toList();
        return Menu.reconstituer(ligne.nom(), plats, EtatMenu.valueOf(ligne.etat()));
    }
}
