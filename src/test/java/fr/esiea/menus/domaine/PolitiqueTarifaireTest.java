package fr.esiea.menus.domaine;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** LA CLASSE QUI DÉCIDE : un cas normal, un cas limite, un cas d'erreur. Aucune base de données nécessaire. */
class PolitiqueTarifaireTest {

    private final PolitiqueDePrix politique = new PolitiqueTarifaire();

    private static CoutCalcule cout(double total, String... ingredientsSansPrix) {
        return new CoutCalcule("Mariage", LocalDate.of(2026, 11, 13), 100, Map.of(), total / 100, total,
                Set.of(ingredientsSansPrix));
    }

    @Test
    void uneEntreprisePaieTroisFoisLeCoutDeRevient() {
        assertEquals(3000.00, politique.prixTheorique(cout(1000), TypeDeClient.ENTREPRISE), 0.001);
    }

    @Test
    void unParticulierPaieDeuxFoisEtDemieArrondiAuCentime() {
        assertEquals(83.33, politique.prixTheorique(cout(33.333), TypeDeClient.PARTICULIER), 0.001);
    }

    @Test
    void unCoutIncompletEmpecheDeFixerUnPrix() {
        RegleMetierException refus = assertThrows(RegleMetierException.class,
                () -> politique.prixTheorique(cout(500, "Safran"), TypeDeClient.ENTREPRISE));
        assertEquals("RM3", refus.regle());
    }
}
