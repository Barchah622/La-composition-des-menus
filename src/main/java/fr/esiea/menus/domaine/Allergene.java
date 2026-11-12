package fr.esiea.menus.domaine;

/** Les 14 allergènes réglementaires à signaler au client (RM23). */
public enum Allergene {
    GLUTEN("gluten"),
    CRUSTACES("crustacés"),
    OEUFS("œufs"),
    POISSON("poisson"),
    ARACHIDES("arachides"),
    SOJA("soja"),
    LAIT("lait"),
    FRUITS_A_COQUE("fruits à coque"),
    CELERI("céleri"),
    MOUTARDE("moutarde"),
    SESAME("sésame"),
    SULFITES("sulfites"),
    LUPIN("lupin"),
    MOLLUSQUES("mollusques");

    private final String libelle;

    Allergene(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }
}
