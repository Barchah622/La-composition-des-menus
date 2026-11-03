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
| Tania Hussain |UC5 — Établir la liste des allergènes d’un menu | UC7 — Modifier le prix d’un ingrédient |
| Mohamed Amine BARCHAH | UC8 — Fixer le prix de vente d'un menu selon le type de client | UC6 — Composer un menu


---

## 2. Nos questions à la cliente

*8 questions maximum. Une question = un point flou, dans les mots du client, et une décision qui en dépend.*

| # | Question | Type de flou | Ce que la réponse débloque |
|---|---|---|---|
| Q1 |Le « coût de revient » d'un menu, c'est seulement le coût des ingrédients, ou faut-il aussi compter la main-d'œuvre, l'énergie et les pertes ? |	non-dit  | La formule de calcul du coût (RM sur le coût de revient, UC4).|
| Q2 | Quand « une sauce entre dans trois plats », comment indiquez-vous la quantité utilisée dans chaque plat, et une préparation a-t-elle un rendement (pertes à la cuisson, quantité produite par recette) ?| ambiguïté  | 	Le calcul récursif : on prend une part de la sauce au prorata de la quantité utilisée (UC2, UC4).|
| Q3 | Quand le prix d'un ingrédient change chez le fournisseur, le coût des menus déjà proposés ou vendus à un client doit-il changer aussi, ou garde-t-on le prix du jour du devis ? | contrainte cachée | Faut-il garder l'historique des prix des ingrédients et figer le coût d'un menu au moment du devis (UC3, UC4). |
| Q4 | Une recette est-elle écrite pour un nombre de portions fixe, et faut-il recalculer les quantités automatiquement selon le nombre de couverts d'une commande ? | non-dit | Ce qu'on enregistre dans une recette (quantités pour N portions) et la règle de mise à l'échelle des quantités (UC2, UC5). |
| Q5 | Quand vous « composez des menus pour des événements », un menu est-il organisé en services (entrée, plat, dessert) avec plusieurs choix possibles par service, ou c'est une simple liste de plats ? | ambiguïté | La structure d'un menu et la façon de calculer son coût par couvert (UC5, UC4). |
| Q6 | Le nombre de couverts d'un événement peut-il changer une fois le menu composé (invités en plus, annulations) ? Jusqu'à quand ? | non-dit | Faut-il recalculer quantités et coût, et à partir de quand le menu est figé (UC6, UC4). |
| Q7 | Qu'est-ce qui différencie exactement un client « entreprise » d'un « particulier » (coefficient de marge différent, TVA, remise selon le volume) ? | ambiguïté | La formule de calcul du prix de vente (UC8, RM sur les prix). |
| Q8 | Les nouveaux prix des ingrédients doivent-ils être saisis à la main, ou proviennent-ils d'un fichier/logiciel externe (fournisseur) ? | non-dit | L'identification d'un acteur non humain et le mécanisme d'import (UC3, UC7). | 
---

## 3. Nos hypothèses

*Pour chaque point flou, l'hypothèse que vous prenez pour avancer sans réponse.*

| # | Nous supposons que… | parce que… | Si c'est faux, cela change… |
|---|---|---|---|
| H1 |	le coût de revient ne compte que le coût des ingrédients | c'est le seul coût que le mail permet de relier aux plats|la formule de UC4 : il faut ajouter des coûts fixes ou horaires par recette. |
| H2 | le commercial fixe le prix en appliquant un simple coefficient multiplicateur sur le coût de revient selon le type de client | la cliente demande une politique de prix selon le client mais ne mentionne aucune règle complexe de remise | le scénario nominal de l'UC8 : il faudrait développer un moteur de règles tarifaires beaucoup plus lourd. |
| H3 | la composition d'un menu se fait uniquement en sélectionnant des plats standards, sans modifier les ingrédients pour cet événement | le besoin évoque l'assemblage de plats, ce qui implique de réutiliser les fiches techniques telles quelles | la structure de l'UC6 : le système devrait créer une copie indépendante de la recette pour chaque menu. |
| H4 | le prix de vente fixé pour un menu est définitivement figé une fois validé par le commercial, même si le coût des ingrédients fluctue | un devis communiqué à un client engage commercialement le traiteur, le prix ne peut plus bouger | le comportement de l'UC8 : il faudrait gérer des alertes de marge et des mises à jour dynamiques des devis. |
| H5 | un menu est une liste de plats répartis en services (entrée, plat, dessert), avec un seul choix par service, pour un nombre de couverts donné | c'est le cas le plus simple qui couvre un événement, et le mail ne parle pas de choix | il faudrait gérer des options par service, et le coût deviendrait une fourchette ou une moyenne |
| H6 | le nombre de couverts reste modifiable jusqu'à la validation du devis, et les quantités et le coût sont alors recalculés | un événement change souvent de taille avant la date, mais un devis signé ne doit plus bouger | il faudrait gérer des versions du menu ou des avenants au devis |
---
## 4. Les acteurs

*Qui déclenche une interaction avec le système. Au moins un acteur n'est pas humain.*

| Acteur | Humain ? | Ce qu'il veut obtenir |
|---|---|---|
| Chef de production (Karim T.) | oui | Composer des plats et des menus, calculer leur coût de revient et gérer la liste de leurs allergènes. |
| Commercial | oui | Consulter et fixer les prix de vente des menus pour les devis en appliquant la politique tarifaire par client. |
| Système fournisseur | non | Transmettre et mettre à jour automatiquement les prix d'achat des ingrédients dans le système. |

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
