package fr.esiea.menus.domaine;

/** Les trois unités dans lesquelles un ingrédient s'achète (RM14). */
public enum UniteAchat {
    KILO("kilo"),
    LITRE("litre"),
    PIECE("pièce");

    private final String libelle;

    UniteAchat(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }

    public static UniteAchat depuisLibelle(String texte) {
        for (UniteAchat unite : values()) {
            if (unite.libelle.equalsIgnoreCase(texte.trim()) || unite.name().equalsIgnoreCase(texte.trim())) {
                return unite;
            }
        }
        throw new RegleMetierException("RM14", "unité d'achat non autorisée : « " + texte + " » (kilo, litre ou pièce)");
    }
}
