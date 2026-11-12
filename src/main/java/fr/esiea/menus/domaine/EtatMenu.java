package fr.esiea.menus.domaine;

/** Les états du Menu (diagramme d'états du TD2). */
public enum EtatMenu {
    EN_COMPOSITION("En composition"),
    COMPOSE("Composé"),
    CHIFFRE("Chiffré"),
    TARIFE("Tarifé"),
    DEVIS_VALIDE("Devis validé"),
    A_RECALCULER("À recalculer");

    private final String libelle;

    EtatMenu(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }
}
