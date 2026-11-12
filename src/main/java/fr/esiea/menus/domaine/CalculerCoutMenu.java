package fr.esiea.menus.domaine;

import java.time.LocalDate;

/**
 * UC4 — Calculer le coût de revient d'un menu.
 * Porteuse : Manele HSSINI. Règles : RM2, RM3, RM10, RM11, RM15, RM19, RM20.
 */
public class CalculerCoutMenu {

    private final MenuRepository menus;
    private final CalculateurCoutRevient calculateur;

    public CalculerCoutMenu(MenuRepository menus, CalculateurCoutRevient calculateur) {
        this.menus = menus;
        this.calculateur = calculateur;
    }

    public CoutCalcule calculer(String nomMenu, int couverts, LocalDate datePrix) {
        Menu menu = menus.trouverParNom(nomMenu.trim()).orElseThrow(() -> new ElementInconnuException("menu", nomMenu));
        CoutCalcule cout = calculateur.calculer(menu, couverts, datePrix);
        if (cout.complet()) {
            menu.marquerChiffre();
            menus.enregistrer(menu);
        }
        return cout;
    }
}
