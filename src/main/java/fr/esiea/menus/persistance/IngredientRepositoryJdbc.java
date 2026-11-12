package fr.esiea.menus.persistance;

import fr.esiea.menus.domaine.Allergene;
import fr.esiea.menus.domaine.Ingredient;
import fr.esiea.menus.domaine.IngredientRepository;
import fr.esiea.menus.domaine.PrixIngredient;
import fr.esiea.menus.domaine.UniteAchat;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Ingrédients et historique de leurs prix dans PostgreSQL. */
public class IngredientRepositoryJdbc implements IngredientRepository {

    private static final String SEPARATEUR = ",";

    private final Connection connexion;

    public IngredientRepositoryJdbc(Connection connexion) {
        this.connexion = connexion;
    }

    @Override
    public void enregistrer(Ingredient ingredient) {
        String allergenes = ingredient.allergenesRenseignes() ? versTexte(ingredient.allergenes()) : null;
        int modifiees = Jdbc.miseAJour(connexion,
                "UPDATE ingredient SET unite_achat = ?, frais = ?, allergenes = ? WHERE nom = ?",
                ingredient.uniteAchat().name(), ingredient.estFrais(), allergenes, ingredient.nom());
        if (modifiees == 0) {
            Jdbc.miseAJour(connexion, "INSERT INTO ingredient (nom, unite_achat, frais, allergenes) VALUES (?, ?, ?, ?)",
                    ingredient.nom(), ingredient.uniteAchat().name(), ingredient.estFrais(), allergenes);
        }
        Jdbc.miseAJour(connexion, "DELETE FROM prix_ingredient WHERE ingredient_nom = ?", ingredient.nom());
        for (PrixIngredient prix : ingredient.historiquePrix()) {
            Jdbc.miseAJour(connexion,
                    "INSERT INTO prix_ingredient (ingredient_nom, valeur, date_application) VALUES (?, ?, ?)",
                    ingredient.nom(), prix.valeur(), prix.dateApplication());
        }
    }

    @Override
    public Optional<Ingredient> trouverParNom(String nom) {
        List<Ingredient> trouves = Jdbc.lister(connexion,
                "SELECT nom, unite_achat, frais, allergenes FROM ingredient WHERE LOWER(nom) = LOWER(?)",
                ligne -> reconstituer(ligne.getString("nom"), ligne.getString("unite_achat"),
                        ligne.getBoolean("frais"), ligne.getString("allergenes")),
                nom);
        trouves.forEach(this::chargerPrix);
        return trouves.stream().findFirst();
    }

    @Override
    public List<Ingredient> tous() {
        List<String> noms = Jdbc.lister(connexion, "SELECT nom FROM ingredient ORDER BY nom", ligne -> ligne.getString("nom"));
        return noms.stream().map(this::trouverParNom).flatMap(Optional::stream).toList();
    }

    private static Ingredient reconstituer(String nom, String unite, boolean frais, String allergenes) {
        Ingredient ingredient = new Ingredient(nom, UniteAchat.valueOf(unite), frais);
        if (allergenes != null) {
            ingredient.renseignerAllergenes(depuisTexte(allergenes));
        }
        return ingredient;
    }

    private void chargerPrix(Ingredient ingredient) {
        Jdbc.lister(connexion,
                "SELECT valeur, date_application FROM prix_ingredient WHERE ingredient_nom = ? ORDER BY date_application",
                ligne -> new PrixIngredient(ligne.getDouble("valeur"), ligne.getObject("date_application", LocalDate.class)),
                ingredient.nom()).forEach(ingredient::appliquerPrix);
    }

    private static String versTexte(Set<Allergene> allergenes) {
        return allergenes.stream().map(Allergene::name).collect(Collectors.joining(SEPARATEUR));
    }

    private static Set<Allergene> depuisTexte(String texte) {
        Set<Allergene> allergenes = EnumSet.noneOf(Allergene.class);
        Arrays.stream(texte.split(SEPARATEUR)).filter(nom -> !nom.isBlank()).map(Allergene::valueOf).forEach(allergenes::add);
        return allergenes;
    }
}
