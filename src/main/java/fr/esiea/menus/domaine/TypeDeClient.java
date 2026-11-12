package fr.esiea.menus.domaine;

/** Le type de client et son coefficient de marge (réponse de la cliente à Q7). */
public enum TypeDeClient {
    ENTREPRISE("Entreprise", 3.0),
    PARTICULIER("Particulier", 2.5);

    private final String libelle;
    private final double coefficient;

    TypeDeClient(String libelle, double coefficient) {
        this.libelle = libelle;
        this.coefficient = coefficient;
    }

    public String libelle() {
        return libelle;
    }

    public double coefficient() {
        return coefficient;
    }
}
