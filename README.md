# La composition des menus · Sujet C

Conception d'application · 4A ESIEA · client : Karim T., chef de production chez un traiteur.

Application console Java 21 qui compose des recettes et des menus, calcule leur coût de revient, établit leurs allergènes et fixe un prix de vente selon le type de client.

| Livrable | Fichier |
|---|---|
| TD1 · Analyse | [TD1-analyse.md](TD1-analyse.md) |
| TD2 · Modèle | [TD2.md](TD2.md) |
| TD3 · Code | [TD3.md](TD3.md) · `src/` |
| Présentation du 13/11 | [presentation/Presentation-Sujet-C.pptx](presentation/Presentation-Sujet-C.pptx) |

```bash
docker compose up -d        # PostgreSQL
mvn test                    # 64 tests
mvn -q compile exec:java    # l'application
```

## Équipe

| Membre | Use cases |
|---|---|
| Manele HSSINI | UC1 Enregistrer un ingrédient · UC4 Calculer le coût de revient d'un menu |
| Abir GUEBBACHE | UC2 Composer une recette · UC3 Mettre à jour le prix d'un ingrédient |
| Tania HUSSAIN | UC5 Établir la liste des allergènes · UC7 Modifier le prix d'un ingrédient |
| Mohamed Amine BARCHAH | UC6 Composer un menu · UC8 Fixer le prix de vente d'un menu |
