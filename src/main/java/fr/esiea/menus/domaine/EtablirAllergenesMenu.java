package fr.esiea.menus.domaine;

import java.time.LocalDate;

/**
 * UC5 — Établir la liste des allergènes d'un menu.
 * Porteuse : Tania HUSSAIN. Règles : RM1, RM12, RM18, RM21, RM22, RM23, RM24.
 */
public class EtablirAllergenesMenu {

    private final MenuRepository menus;
    private final VerificateurAllergenes verificateur;

    public EtablirAllergenesMenu(MenuRepository menus, VerificateurAllergenes verificateur) {
        this.menus = menus;
        this.verificateur = verificateur;
    }

    public ListeAllergenesMenu etablir(String nomMenu, LocalDate date) {
        Menu menu = menus.trouverParNom(nomMenu.trim()).orElseThrow(() -> new ElementInconnuException("menu", nomMenu));
        return verificateur.etablirListe(menu, date);
    }
}
