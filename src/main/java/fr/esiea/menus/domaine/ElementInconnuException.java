package fr.esiea.menus.domaine;

/** Levée quand on cherche un ingrédient, une recette ou un menu qui n'existe pas. */
public class ElementInconnuException extends RuntimeException {

    public ElementInconnuException(String genre, String nom) {
        super(genre + " inconnu : « " + nom + " »");
    }
}
