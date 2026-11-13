package fr.esiea.menus.domaine;

import java.time.LocalDate;

/**
 * UC8 — Fixer le prix de vente d'un menu selon le type de client.
 * Porteur : Mohamed Amine BARCHAH. Règles : RM2, RM3, RM4, RM5.
 */
public class FixerPrixDeVente {

    private final MenuRepository menus;
    private final CalculateurCoutRevient calculateur;
    private final PolitiqueDePrix politique;
    private final PrixDeVenteRepository prixDeVente;

    public FixerPrixDeVente(MenuRepository menus, CalculateurCoutRevient calculateur, PolitiqueDePrix politique,PrixDeVenteRepository prixDeVente) {
        this.menus = menus;
        this.calculateur = calculateur;
        this.politique = politique;
        this.prixDeVente = prixDeVente;
    }

    public PropositionPrix proposer(String nomMenu, int convives, TypeDeClient typeDeClient, LocalDate date) {
        CoutCalcule cout = calculateur.calculer(trouverMenu(nomMenu), convives, date);
        return new PropositionPrix(cout, typeDeClient, politique.prixTheorique(cout, typeDeClient));
    }

    public PrixDeVente fixer(PropositionPrix proposition, double prixFinal, LocalDate date) {
        CoutCalcule cout = proposition.cout();
        PrixDeVente prix = new PrixDeVente(cout.menu(), proposition.typeDeClient(), cout.couverts(), prixFinal,cout.coutTotal(), date);
        prixDeVente.enregistrer(prix);
        Menu menu = trouverMenu(cout.menu());
        menu.marquerTarife();
        menus.enregistrer(menu);
        return prix;
    }

    private Menu trouverMenu(String nomMenu) {
        return menus.trouverParNom(nomMenu.trim()).orElseThrow(() -> new ElementInconnuException("menu", nomMenu));
    }
}
