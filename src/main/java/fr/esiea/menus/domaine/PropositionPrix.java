package fr.esiea.menus.domaine;

/** Le coût de revient et le prix théorique présentés au Commercial avant qu'il fixe le prix final (UC8). */
public record PropositionPrix(CoutCalcule cout, TypeDeClient typeDeClient, double prixTheorique) {

    public double margeSi(double prixFinal) {
        return prixFinal - cout.coutTotal();
    }
}
