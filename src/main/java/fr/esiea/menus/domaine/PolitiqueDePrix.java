package fr.esiea.menus.domaine;

/**
 * Point de variation C1 : la forme des remises aux gros clients n'est pas décidée (réponse à Q7).
 * OCP : une politique avec remise s'ajoutera comme une nouvelle classe, sans modifier FixerPrixDeVente.
 * DIP : le use case dépend de cette interface, pas d'une politique concrète.
 */
public interface PolitiqueDePrix {
    double prixTheorique(CoutCalcule cout, TypeDeClient typeDeClient);
}
