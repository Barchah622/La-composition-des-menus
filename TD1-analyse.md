# TD 1 · Analyse — Sujet C : La composition des menus

> **Livrable 1.** Un seul fichier, dans le dépôt GitLab de l'équipe.
> **Parties 1 à 3 :** poussées pendant la séance du mardi 3 novembre.
> **Parties 4 à 11 :** poussées avant le vendredi 6 novembre à 12h00.
> En cas de problème GitLab : ce fichier par mail, avant la même heure.
> Si ce n'est pas demandé dans ce modèle, ce n'est pas demandé.

---

## 1. Équipe et sujet

**Sujet :** C — La composition des menus (client : Karim T., chef de production, traiteur)

**Équipe jumelle :**

| Membre | Use case riche | Use case simple |
|---|---|---|
| Manele Hssini|UC4 — Calculer le coût de revient d'un menu |	UC1 — Enregistrer un ingrédient |
| Prénom Nom | | |
| Prénom Nom | | |

---

## 2. Nos questions à la cliente

*8 questions maximum. Une question = un point flou, dans les mots du client, et une décision qui en dépend.*

| # | Question | Type de flou | Ce que la réponse débloque |
|---|---|---|---|
| Q1 |Le « coût de revient » d'un menu, c'est seulement le coût des ingrédients, ou faut-il aussi compter la main-d'œuvre, l'énergie et les pertes ? |	non-dit  | La formule de calcul du coût (RM sur le coût de revient, UC4).|
| Q2 | Quand « une sauce entre dans trois plats », comment indiquez-vous la quantité utilisée dans chaque plat, et une préparation a-t-elle un rendement (pertes à la cuisson, quantité produite par recette) ?| ambiguïté / contradiction / non-dit / contrainte cachée | 	Le calcul récursif : on prend une part de la sauce au prorata de la quantité utilisée (UC2, UC4).|

---

## 3. Nos hypothèses

*Pour chaque point flou, l'hypothèse que vous prenez pour avancer sans réponse.*

| # | Nous supposons que… | parce que… | Si c'est faux, cela change… |
|---|---|---|---|
| H1 | | | |
| H2 | | | |

---

## 4. Les acteurs

*Qui déclenche une interaction avec le système. Au moins un acteur n'est pas humain.*

| Acteur | Humain ? | Ce qu'il veut obtenir |
|---|---|---|
| | oui / non | |

---

## 5. Les use cases

*Format « verbe + complément », du point de vue de l'acteur. 6 à 8 pour l'équipe.*

| # | Use case | Acteur principal | Porteur |
|---|---|---|---|
| UC1 | | | |
| UC2 | | | |

**Diagramme de use cases**

*Dessiné dans draw.io : le rectangle du système, les acteurs dehors, include et extends. Exportez-le en PNG dans le dossier `diagrammes/` du dépôt.*

![Diagramme de use cases](diagrammes/use-cases.png)

---

## 6. Les fiches use case

*Une fiche par use case, 2 par personne. Au moins deux scénarios d'erreur par fiche. Copiez le modèle ci-dessous autant de fois que nécessaire.*

### UC… — [verbe + complément]

- **Auteur :**
- **Acteur principal :**
- **Acteurs secondaires :**
- **Objectif :**
- **Préconditions :**

**Scénario nominal**

1.
2.
3.

**Scénarios alternatifs**

- 1a.

**Scénarios d'erreur**

- 2e.
- 3e.

**Règles métier appliquées :** RM…

---

## 7. Les règles métier

*Une règle par ligne, numérotée. Le test : avec un gardien et un carnet, sans aucun logiciel, la règle reste vraie.*

| # | Règle métier | Use cases qui l'appliquent |
|---|---|---|
| RM1 | | |
| RM2 | | |

---

## 8. Ce que l'équipe jumelle nous a fait voir

*Ce qu'elle a trouvé chez vous : 1 acteur oublié, 3 cas d'erreur oubliés, 1 hypothèse discutable. Et ce que vous avez changé.*

| Ce qu'elle nous a fait voir | Ce qu'on a changé |
|---|---|
| Acteur oublié : | |
| Cas d'erreur oublié : | |
| Cas d'erreur oublié : | |
| Cas d'erreur oublié : | |
| Hypothèse discutable : | |

---

## 9. Glossaire du domaine

*10 termes du métier de votre client, avec leur définition. Les mots du client, pas ceux de l'informatique.*

| Terme | Définition |
|---|---|
| | |

---

## 10. Confrontation avec les réponses de la cliente

*À faire après la publication des réponses, mardi soir. Pour chaque question (Q) et chaque hypothèse (H).*

| Q ou H | Réponse de la cliente | Vu / Raté / Ouvert | Ce qu'on a changé dans le fichier |
|---|---|---|---|
| Q1 | | | |
| H1 | | | |

**Ce que nous n'avions pas vu :**

**Ce qui reste ouvert après les réponses :**

---

## 11. Diagramme de séquence

*Un seul pour l'équipe, sur le use case le plus riche. Qui parle à qui, dans quel ordre. Exportez-le en PNG dans `diagrammes/`.*

**Use case choisi :**

![Diagramme de séquence](diagrammes/sequence.png)

---
