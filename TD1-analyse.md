# TD 1 · Analyse — Sujet C : La composition des menus

> **Livrable 1.** Un seul fichier, dans le dépôt GitLab de l'équipe.
> **Parties 1 à 3 :** poussées pendant la séance du mardi 3 novembre.
> **Parties 4 à 11 :** poussées avant le vendredi 6 novembre à 12h00.
> En cas de problème GitLab : ce fichier par mail, avant la même heure.
> Si ce n'est pas demandé dans ce modèle, ce n'est pas demandé.

---## 1. Équipe et sujet

**Sujet :** C — La composition des menus (client : Karim T., chef de production, traiteur)

**Équipe jumelle :** _à compléter_

| Membre | Use case riche | Use case simple |
|---|---|---|
| Mohamed Amine BARCHAH | Composer un plat | Composer un menu |
| Abir GUEBBACHE | Calculer le coût de revient d'un menu | Mettre à jour le prix d'un ingrédient |
| Manele HSSINI | Agréger les allergènes d'un menu | Renseigner les allergènes d'un ingrédient |
| Tania HUSSAIN | Fixer le prix de vente d'un menu selon le type de client | Consulter le prix de vente d'un menu |

---

## 2. Nos questions à la cliente

| # | Question | Type de flou | Ce que la réponse débloque |
|---|---|---|---|
| Q1 | Une préparation (une sauce, un fond…) peut-elle contenir d'autres préparations, et sur combien de niveaux ? Les quantités sont-elles données par portion, et faut-il compter les pertes (épluchage, cuisson) ? | ambiguïté | Profondeur de la récursion, unité des quantités, formule du coût |
| Q2 | Comment est structuré un menu : entrée / plat / dessert, plusieurs choix pour un même service, un nombre de convives ? | ambiguïté | Structure du menu, calcul par convive ou par événement |
| Q3 | Le coût de revient comprend-il seulement les ingrédients, ou aussi la main-d'œuvre, l'énergie, les emballages ? | non-dit | Formule du coût et données à saisir |
| Q4 | Si le prix d'un ingrédient change, le coût d'un menu déjà vendu doit-il rester figé ? | non-dit | Historique des prix, notion de devis figé |
| Q5 | Les 14 allergènes réglementaires suffisent-ils, ou faut-il aussi les traces et les contaminations croisées en cuisine ? | contrainte cachée | Périmètre légal, données à saisir par ingrédient |
| Q6 | Si l'information allergènes d'un ingrédient manque, l'outil doit-il bloquer le menu ou l'afficher avec un avertissement ? | contrainte cachée | Blocage ou avertissement au moment d'éditer la fiche d'allergènes |
| Q7 | Qu'est-ce qui change entre un client entreprise et un particulier (coefficient, remise selon le nombre de convives, HT ou TTC) ? D'autres types de clients sont-ils prévus ? | contradiction | Politique de prix, extensibilité à d'autres types de client, prix montrés au commercial |
| Q8 | Le suivi des stocks est un autre besoin : peut-on le traiter dans une phase 2 ? | ambiguïté | Périmètre : stocks dehors |

---

## 3. Nos hypothèses


| # | Nous supposons que… | parce que… | Si c'est faux, cela change… |
|---|---|---|---|
| H1 | Une préparation (sauce, fond…) est une référence partagée : modifier la préparation modifie tous les plats qui l'utilisent | la cliente dit « une sauce entre dans trois plats », donc une seule sauce, pas trois copies | Il faut des versions des préparations et des plats ; le calcul du coût devient daté |
| H2 | Le coût de revient = ingrédients uniquement (quantité × prix unitaire) | c'est la seule donnée citée ; main-d'œuvre et pertes ne sont pas mentionnées | Il faut ajouter des coefficients de perte et un coût de main-d'œuvre |
| H3 | Chaque ingrédient porte sa liste d'allergènes parmi les 14 allergènes à déclaration obligatoire ; la saisie est faite par le chef de production | « c'est la loi, on n'a pas droit à l'erreur » ; la liste réglementaire est fermée | Si les traces comptent aussi, il faut une seconde liste par ingrédient et un affichage distinct |
| H4 | Un ingrédient dont les allergènes ne sont pas renseignés est « inconnu » et bloque la fiche d'allergènes | une erreur d'omission est plus grave qu'un refus de produire la fiche | Si « vide » veut dire « aucun », le calcul passe, mais le risque d'erreur augmente |
| H5 | Coût et prix sont exprimés par convive ; le coût total s'obtient en multipliant par le nombre de convives | un traiteur vend à la personne ; les menus sont composés pour des événements | Si les quantités sont fixes par événement, la composition d'un menu porte directement des quantités |
| H6 | Le commercial est un acteur du système : il enregistre un prix par menu et par type de client (entreprise / particulier) | l'extension demandée est « une deuxième politique de prix selon le type de client » | S'il ne fait que consulter le coût, la saisie des prix de vente disparaît du périmètre |
| H7 | Le calcul d'un coût utilise les prix d'ingrédients en vigueur le jour du calcul ; l'historique des prix est conservé | il faut pouvoir expliquer un coût passé | Si le devis doit rester figé, il faut un « devis » avec un coût enregistré |
| H8 | Les prix d'achat des ingrédients arrivent par un import (fichier ou service du fournisseur) en plus de la saisie manuelle | la cliente travaille avec des fournisseurs ; saisir à la main chaque prix est irréaliste | Saisie 100 % manuelle : l'acteur non humain disparaît |
| H9 | Le suivi des stocks est hors périmètre de cette version | « si l'outil pouvait aussi… » signale un souhait, pas un besoin du produit minimum | Il faut des mouvements de stock, des quantités par ingrédient, des alertes |
| H10 | Un menu est une liste fixe de plats rangés par service (entrée, plat, dessert), sans choix proposé au client | la cliente parle de « composer des menus à partir de plats », pas de choix | Il faut modéliser des groupes de plats à choix et calculer un coût par combinaison |
| H11 | Il n'y a que deux types de client (entreprise, particulier) ; le commercial saisit le prix de vente à la main, sans formule (coefficient ou remise) imposée | « enfin ça, c'est le commercial qui voit » : la cliente ne décrit aucune règle de calcul | Si le prix découle d'un coefficient ou d'une remise au volume, il faut un moteur de règles de prix |


---
## 4. Les acteurs

| Acteur | Humain ? | Ce qu'il veut obtenir |
|---|---|---|
| Chef de production (Karim T.) | oui | Composer plats et menus, connaître leur coût de revient et leurs allergènes sans erreur |
| Commercial | oui | Fixer et consulter les prix de vente d'un menu selon le type de client, en connaissant le coût |
| Service d'import des tarifs fournisseurs | non | Transmettre au système les nouveaux prix d'achat des ingrédients |

---

## 5. Les use cases

| # | Use case | Acteur principal | Porteur |
|---|---|---|---|
| UC1 | Composer un plat | Chef de production | Mohamed Amine BARCHAH |
| UC2 | Composer un menu | Chef de production | Mohamed Amine BARCHAH |
| UC3 | Mettre à jour le prix d'un ingrédient | Service d'import des tarifs | Abir GUEBBACHE |
| UC4 | Calculer le coût de revient d'un menu | Chef de production | Abir GUEBBACHE |
| UC5 | Agréger les allergènes d'un menu | Chef de production | Manele HSSINI |
| UC6 | Fixer le prix de vente d'un menu selon le type de client | Commercial | Tania HUSSAIN |
| UC7 | Renseigner les allergènes d'un ingrédient | Chef de production | Manele HSSINI |
| UC8 | Consulter le prix de vente d'un menu | Commercial | Tania HUSSAIN |


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
