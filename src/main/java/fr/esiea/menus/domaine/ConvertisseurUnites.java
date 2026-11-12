package fr.esiea.menus.domaine;

/** Ramène une quantité dans l'unité d'achat de l'ingrédient (RM20). */
public class ConvertisseurUnites {

    public boolean sontCompatibles(UniteMesure unite, UniteAchat uniteAchat) {
        return unite.uniteAchat() == uniteAchat;
    }

    public double versUniteAchat(double quantite, UniteMesure unite, UniteAchat uniteAchat) {
        if (!sontCompatibles(unite, uniteAchat)) {
            throw new RegleMetierException("RM20",
                    "impossible de ramener des " + unite.symbole() + " à l'unité d'achat « " + uniteAchat.libelle() + " »");
        }
        return quantite * unite.facteurVersUniteAchat();
    }
}
