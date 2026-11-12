package fr.esiea.menus.domaine;

/** Les unités dans lesquelles le Chef écrit une quantité dans une recette. */
public enum UniteMesure {
    KG("kg", UniteAchat.KILO, 1),
    G("g", UniteAchat.KILO, 0.001),
    L("L", UniteAchat.LITRE, 1),
    CL("cL", UniteAchat.LITRE, 0.01),
    ML("mL", UniteAchat.LITRE, 0.001),
    PIECE("pièce", UniteAchat.PIECE, 1);

    private final String symbole;
    private final UniteAchat uniteAchat;
    private final double facteurVersUniteAchat;

    UniteMesure(String symbole, UniteAchat uniteAchat, double facteurVersUniteAchat) {
        this.symbole = symbole;
        this.uniteAchat = uniteAchat;
        this.facteurVersUniteAchat = facteurVersUniteAchat;
    }

    public String symbole() {
        return symbole;
    }

    public UniteAchat uniteAchat() {
        return uniteAchat;
    }

    public double facteurVersUniteAchat() {
        return facteurVersUniteAchat;
    }

    public static UniteMesure depuisSymbole(String texte) {
        String saisie = texte.trim();
        for (UniteMesure unite : values()) {
            if (unite.symbole.equalsIgnoreCase(saisie) || unite.name().equalsIgnoreCase(saisie)) {
                return unite;
            }
        }
        if (saisie.equalsIgnoreCase("piece")) {
            return PIECE;
        }
        throw new RegleMetierException("RM8", "unité inconnue : « " + texte + " » (kg, g, L, cL, mL ou pièce)");
    }
}
