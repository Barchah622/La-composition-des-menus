package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Composant;
import fr.esiea.menus.domaine.ElementInconnuException;
import fr.esiea.menus.domaine.IngredientRepository;
import fr.esiea.menus.domaine.Recette;
import fr.esiea.menus.domaine.RecetteRepository;
import fr.esiea.menus.domaine.TypeRecette;
import fr.esiea.menus.domaine.UniteMesure;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Recettes et leurs composants dans PostgreSQL. */
public class RecetteRepositoryJdbc implements RecetteRepository {

    private record LigneRecette(String nom, String type, double quantiteProduite, String uniteProduite) {
    }

    private record LigneComposant(String ingredient, String preparation, double quantite, String unite) {
    }

    private final Connection connexion;
    private final IngredientRepository ingredients;

    public RecetteRepositoryJdbc(Connection connexion, IngredientRepository ingredients) {
        this.connexion = connexion;
        this.ingredients = ingredients;
    }

    @Override
    public void enregistrer(Recette recette) {
        int modifiees = Jdbc.miseAJour(connexion,
                "UPDATE recette SET type_recette = ?, quantite_produite = ?, unite_produite = ? WHERE nom = ?",
                recette.type().name(), recette.quantiteProduite(), recette.uniteProduite().name(), recette.nom());
        if (modifiees == 0) {
            Jdbc.miseAJour(connexion,
                    "INSERT INTO recette (nom, type_recette, quantite_produite, unite_produite) VALUES (?, ?, ?, ?)",
                    recette.nom(), recette.type().name(), recette.quantiteProduite(), recette.uniteProduite().name());
        }
        Jdbc.miseAJour(connexion, "DELETE FROM composant WHERE recette_nom = ?", recette.nom());
        List<Composant> composants = recette.composants();
        for (int ordre = 0; ordre < composants.size(); ordre++) {
            Composant composant = composants.get(ordre);
            Jdbc.miseAJour(connexion,
                    "INSERT INTO composant (recette_nom, ordre, ingredient_nom, preparation_nom, quantite, unite) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    recette.nom(), ordre,
                    composant.estIngredient() ? composant.ingredient().nom() : null,
                    composant.estIngredient() ? null : composant.preparation().nom(),
                    composant.quantite(), composant.unite().name());
        }
    }

    @Override
    public Optional<Recette> trouverParNom(String nom) {
        List<LigneRecette> lignes = Jdbc.lister(connexion,
                "SELECT nom, type_recette, quantite_produite, unite_produite FROM recette WHERE LOWER(nom) = LOWER(?)",
                ligne -> new LigneRecette(ligne.getString("nom"), ligne.getString("type_recette"),
                        ligne.getDouble("quantite_produite"), ligne.getString("unite_produite")),
                nom);
        return lignes.stream().findFirst().map(this::reconstituer);
    }

    @Override
    public List<Recette> toutes() {
        List<String> noms = Jdbc.lister(connexion, "SELECT nom FROM recette ORDER BY nom", ligne -> ligne.getString("nom"));
        return noms.stream().map(this::trouverParNom).flatMap(Optional::stream).toList();
    }

    private Recette reconstituer(LigneRecette ligne) {
        Recette recette = TypeRecette.valueOf(ligne.type()) == TypeRecette.PLAT
                ? Recette.plat(ligne.nom())
                : Recette.preparation(ligne.nom(), ligne.quantiteProduite(), UniteMesure.valueOf(ligne.uniteProduite()));
        List<LigneComposant> composants = Jdbc.lister(connexion,
                "SELECT ingredient_nom, preparation_nom, quantite, unite FROM composant WHERE recette_nom = ? ORDER BY ordre",
                resultat -> new LigneComposant(resultat.getString("ingredient_nom"), resultat.getString("preparation_nom"),
                        resultat.getDouble("quantite"), resultat.getString("unite")),
                ligne.nom());
        composants.forEach(composant -> recette.ajouterComposant(versComposant(composant)));
        return recette;
    }

    private Composant versComposant(LigneComposant ligne) {
        UniteMesure unite = UniteMesure.valueOf(ligne.unite());
        if (ligne.ingredient() != null) {
            return Composant.ingredient(ingredients.trouverParNom(ligne.ingredient())
                    .orElseThrow(() -> new ElementInconnuException("ingrédient", ligne.ingredient())), ligne.quantite(), unite);
        }
        return Composant.preparation(trouverParNom(ligne.preparation())
                .orElseThrow(() -> new ElementInconnuException("recette", ligne.preparation())), ligne.quantite(), unite);
    }
}
