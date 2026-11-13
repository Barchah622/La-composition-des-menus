package fr.esiea.menus.domaine;

import java.util.TreeSet;

/** LA CLASSE QUI DÉCIDE : prix de vente théorique = coût de revient total × coefficient du type de client (RM3, RM4). */
public class PolitiqueTarifaire implements PolitiqueDePrix {

    private static final double CENTIMES_PAR_EURO = 100;

    @Override
    public double prixTheorique(CoutCalcule cout, TypeDeClient typeDeClient) {
        if (!cout.complet()) {
            throw new RegleMetierException("RM3", "prix manquant pour " + new TreeSet<>(cout.ingredientsSansPrix())+ " : le prix de vente ne peut pas être fixé");
        }
        return arrondiAuCentime(cout.coutTotal() * typeDeClient.coefficient());
    }

    private static double arrondiAuCentime(double montant) {
        return Math.round(montant * CENTIMES_PAR_EURO) / CENTIMES_PAR_EURO;
    }
}
