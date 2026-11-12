package fr.esiea.menus.domaine;

/** Décide si un composant peut entrer dans une recette sans qu'elle finisse par se contenir elle-même (RM7). */
public class GardienComposition {

    public boolean peutEntrer(Composant composant, Recette recette) {
        if (composant.estIngredient()) {
            return true;
        }
        Recette preparation = composant.preparation();
        return !preparation.equals(recette) && !preparation.contient(recette);
    }
}
