package fr.esiea.menus.domaine;

/** Levée quand une règle métier (RM1 à RM26) refuse une action. */
public class RegleMetierException extends RuntimeException {

    private final String regle;

    public RegleMetierException(String regle, String message) {
        super(regle + " · " + message);
        this.regle = regle;
    }

    public String regle() {
        return regle;
    }
}
