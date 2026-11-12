package fr.esiea.menus.domaine;

/** Décide si un nouveau prix s'écarte trop de l'ancien pour être appliqué sans vérification (RM26). */
public class ControleurVariationPrix {

    static final double SEUIL_VARIATION = 0.50;

    public boolean exigeConfirmation(double ancienPrix, double nouveauPrix) {
        double ecartRelatif = Math.abs(nouveauPrix - ancienPrix) / ancienPrix;
        return ecartRelatif > SEUIL_VARIATION;
    }
}
