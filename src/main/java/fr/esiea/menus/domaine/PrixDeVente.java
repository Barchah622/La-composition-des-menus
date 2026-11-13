package fr.esiea.menus.domaine;

import java.time.LocalDate;
import java.util.Objects;

/** Le prix de vente retenu pour un menu et un type de client : jamais sous le coût de revient (RM5). */
public record PrixDeVente(String menu, TypeDeClient typeDeClient, int couverts, double prix,double coutRevient, LocalDate dateFixation) {

    public PrixDeVente {
        Objects.requireNonNull(typeDeClient, "type de client");
        Objects.requireNonNull(dateFixation, "date");
        if (prix < coutRevient) {
            throw new RegleMetierException("RM5", String.format("vente à perte interdite : %.2f € < coût de revient %.2f €",prix, coutRevient));
        }
    }

    public double marge() {
        return prix - coutRevient;
    }
}
