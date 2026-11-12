package fr.esiea.menus.persistance;

/** Une erreur de la base de données, remontée sans exception vérifiée. */
public class PersistanceException extends RuntimeException {

    public PersistanceException(String message, Throwable cause) {
        super(message + " : " + cause.getMessage(), cause);
    }
}
