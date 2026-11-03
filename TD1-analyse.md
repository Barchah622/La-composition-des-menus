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
| Abir Guebbache | UC2 — Composer une recette (plat ou préparation de base) | UC3 — Mettre à jour le prix d'un ingrédient |
| Prénom Nom | | |

---

## 2. Nos questions à la cliente

*8 questions maximum. Une question = un point flou, dans les mots du client, et une décision qui en dépend.*

| # | Question | Type de flou | Ce que la réponse débloque |
|---|---|---|---|
| Q1 |Le « coût de revient » d'un menu, c'est seulement le coût des ingrédients, ou faut-il aussi compter la main-d'œuvre, l'énergie et les pertes ? |	non-dit  | La formule de calcul du coût (RM sur le coût de revient, UC4).|
| Q2 | Quand « une sauce entre dans trois plats », comment indiquez-vous la quantité utilisée dans chaque plat, et une préparation a-t-elle un rendement (pertes à la cuisson, quantité produite par recette) ?| ambiguïté  | 	Le calcul récursif : on prend une part de la sauce au prorata de la quantité utilisée (UC2, UC4).|
| Q3 | Quand le prix d'un ingrédient change chez le fournisseur, le coût des menus déjà proposés ou vendus à un client doit-il changer aussi, ou garde-t-on le prix du jour du devis ? | contrainte cachée | Faut-il garder l'historique des prix des ingrédients et figer le coût d'un menu au moment du devis (UC3, UC4). |
| Q4 | Une recette est-elle écrite pour un nombre de portions fixe, et faut-il recalculer les quantités automatiquement selon le nombre de couverts d'une commande ? | non-dit | Ce qu'on enregistre dans une recette (quantités pour N portions) et la règle de mise à l'échelle des quantités (UC2, UC5). |

---

## 3. Nos hypothèses

*Pour chaque point flou, l'hypothèse que vous prenez pour avancer sans réponse.*

| # | Nous supposons que… | parce que… | Si c'est faux, cela change… |
|---|---|---|---|
| H1 |	le coût de revient ne compte que le coût des ingrédients | c'est le seul coût que le mail permet de relier aux plats|la formule de UC4 : il faut ajouter des coûts fixes ou horaires par recette. |
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
| UC2 | Composer une recette | Chef de production | Abir Guebbache |
| UC3 | Mettre à jour le prix d'un ingrédient | Chef de production (ou fournisseur) | Abir Guebbache |

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

### UC2 — Composer une recette

- **Auteur :** Abir Guebbache
- **Acteur principal :** Chef de production (Karim T.)
- **Acteurs secondaires :** aucun
- **Objectif :** Décrire un plat ou une préparation de base (ex. une sauce) avec ses composants et leurs quantités, pour pouvoir ensuite l'utiliser dans d'autres plats et dans les menus, et en calculer le coût.
- **Préconditions :** Les ingrédients utilisés sont déjà enregistrés (UC1). Les préparations de base utilisées comme composant sont déjà enregistrées.

**Scénario nominal**

1. Le chef demande à créer une nouvelle recette.
2. Le système demande le nom de la recette, son type (plat ou préparation de base) et le nombre de portions (ou la quantité produite, ex. 2 litres de sauce).
3. Le chef saisit ces informations.
4. Le chef ajoute un composant : il choisit un ingrédient ou une préparation de base existante, et indique la quantité utilisée avec son unité (g, kg, L, pièce).
5. Le système vérifie que le composant existe et que l'unité est compatible.
6. Le chef répète l'étape 4 pour chaque composant.
7. Le chef valide la recette.
8. Le système enregistre la recette et affiche son coût calculé à partir des prix des composants (UC4).

**Scénarios alternatifs**

- 4a. Le composant est une préparation de base (ex. une sauce qui entre dans trois plats) : le système prend seulement la part utilisée, au prorata de la quantité produite par la préparation.
- 4b. Le chef modifie ou retire un composant déjà ajouté avant de valider.
- 1a. Le chef part d'une recette existante pour en créer une variante (copie puis modification).

**Scénarios d'erreur**

- 3e. Une recette porte déjà ce nom : le système refuse et demande un autre nom.
- 4e. L'ingrédient n'existe pas dans le système : le système le signale et propose de l'enregistrer d'abord (UC1).
- 5e. La quantité est nulle, négative ou l'unité n'est pas compatible avec l'ingrédient (ex. des litres pour des œufs) : le système refuse le composant.
- 5e'. Le chef ajoute comme composant une préparation qui contient déjà la recette en cours (boucle : la sauce A contient la sauce B qui contient la sauce A) : le système refuse.
- 7e. La recette n'a aucun composant : le système refuse de la valider.

**Règles métier appliquées :**
- Une recette a au moins un composant.
- Chaque quantité est strictement positive et exprimée dans une unité compatible avec le composant.
- Une préparation de base ne peut pas se contenir elle-même, directement ou indirectement.
- Le nom d'une recette est unique.
- Le coût d'une préparation utilisée dans un plat est compté au prorata de la quantité utilisée.

---

### UC3 — Mettre à jour le prix d'un ingrédient

- **Auteur :** Abir Guebbache
- **Acteur principal :** Chef de production (Karim T.)
- **Acteurs secondaires :** Fournisseur (non humain : tarif ou facture fournisseur reçu)
- **Objectif :** Garder le prix d'achat des ingrédients à jour pour que le coût des recettes et des menus reste juste.
- **Préconditions :** L'ingrédient est déjà enregistré (UC1).

**Scénario nominal**

1. Le chef reçoit un nouveau prix du fournisseur et demande à modifier le prix d'un ingrédient.
2. Le système affiche l'ingrédient avec son prix actuel et son unité d'achat (ex. 4,20 € le kg).
3. Le chef saisit le nouveau prix et la date à partir de laquelle il s'applique.
4. Le système vérifie le prix.
5. Le système enregistre le nouveau prix en gardant l'ancien dans l'historique.
6. Le système recalcule le coût des recettes et des menus qui utilisent cet ingrédient et affiche la liste de ceux qui sont touchés.

**Scénarios alternatifs**

- 1a. Le nouveau prix arrive directement par le tarif du fournisseur : le système propose la mise à jour, le chef la confirme.
- 3a. Le fournisseur change aussi l'unité ou le conditionnement (ex. carton de 6 au lieu du kg) : le chef saisit la nouvelle unité et le système convertit.
- 6a. Les menus déjà proposés à un client dans un devis gardent le prix du jour du devis (voir Q3).

**Scénarios d'erreur**

- 2e. L'ingrédient recherché n'existe pas : le système le signale et propose de l'enregistrer (UC1).
- 4e. Le prix est vide, nul ou négatif : le système refuse.
- 4e'. Le prix est très différent de l'ancien (ex. multiplié par 10, erreur de virgule) : le système demande une confirmation avant d'enregistrer.
- 3e. La date d'application est dans le passé avant le dernier prix enregistré : le système refuse.

**Règles métier appliquées :**
- Un prix d'ingrédient est strictement positif.
- Un ingrédient a un seul prix en vigueur à une date donnée.
- L'ancien prix est conservé (historique), il n'est jamais effacé.
- Un changement de prix met à jour le coût de toutes les recettes qui contiennent l'ingrédient, directement ou via une préparation.

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
