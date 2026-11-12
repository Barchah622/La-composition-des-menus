package fr.esiea.menus.domaine;

import java.util.List;
import java.util.Optional;

/** Ce que produit la réception d'un tarif fournisseur (UC3). */
public record ResultatMiseAJourPrix(Statut statut, String ingredient, Optional<PrixIngredient> ancienPrix,
                                    PrixIngredient nouveauPrix, ImpactPrix impact) {

    public enum Statut {
        APPLIQUE,
        EN_ATTENTE_CONFIRMATION
    }

    static ResultatMiseAJourPrix applique(String ingredient, Optional<PrixIngredient> ancien, PrixIngredient nouveau,
                                          ImpactPrix impact) {
        return new ResultatMiseAJourPrix(Statut.APPLIQUE, ingredient, ancien, nouveau, impact);
    }

    static ResultatMiseAJourPrix enAttente(String ingredient, PrixIngredient ancien, PrixIngredient nouveau) {
        return new ResultatMiseAJourPrix(Statut.EN_ATTENTE_CONFIRMATION, ingredient, Optional.of(ancien), nouveau,
                new ImpactPrix(List.of(), List.of()));
    }

    public boolean applique() {
        return statut == Statut.APPLIQUE;
    }
}
