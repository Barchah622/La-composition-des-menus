# TD 2 · Modèle — Sujet C : La composition des menus

> **Livrable 2.** Un seul fichier, dans le dépôt GitLab de l'équipe, avec les images des diagrammes dans le dossier `diagrammes/`.
> **À pousser avant le dimanche 8 novembre à 23h59.**
> En cas de problème GitLab : ce fichier et ses images par mail, avant la même heure.
> Si ce n'est pas demandé dans ce modèle, ce n'est pas demandé.

---

## 1. Les cartes CRC

*Une carte par classe, sans doublon. La responsabilité tient en une phrase, sans « et ». Chaque règle métier (RM) de votre livrable 1 est portée par une classe. Copiez le modèle de carte autant de fois que nécessaire.*

### [Nom de la classe]

| Responsabilité | Collaborateurs |
|---|---|
| | |

**Règles portées :** RM…

---

### Ingrédient

| Responsabilité | Collaborateurs |
|---|---|
| Décrire un produit acheté chez un fournisseur, tel qu'on l'utilise en cuisine. | PrixIngrédient, Allergène, CatalogueIngrédients |

**Règles portées :** RM14, RM18

---

### PrixIngrédient

| Responsabilité | Collaborateurs |
|---|---|
| Garder un prix d'achat valable à partir d'une date donnée. | Ingrédient |

**Règles portées :** RM13, RM15

---

### Allergène

| Responsabilité | Collaborateurs |
|---|---|
| Représenter une des 14 substances réglementaires à signaler au client. | Ingrédient, ListeAllergènesMenu |

**Règles portées :** RM23

---

### CatalogueIngrédients

| Responsabilité | Collaborateurs |
|---|---|
| Refuser un nouvel ingrédient dont le nom est déjà pris. | Ingrédient |

**Règles portées :** RM17

---

### ConvertisseurUnités

| Responsabilité | Collaborateurs |
|---|---|
| Ramener une quantité dans l'unité d'achat de l'ingrédient. | Ingrédient, Composant |

**Règles portées :** RM20

---

### CalculateurCoûtRevient *(classe inventée, c'est elle qui décide)*

| Responsabilité | Collaborateurs |
|---|---|
| Décider du coût de revient d'un menu pour un nombre de couverts. | Menu, Recette, Composant, PrixIngrédient, ConvertisseurUnités, CoûtCalculé |

**Règles portées :** RM2, RM3, RM11, RM19

---

### CoûtCalculé

| Responsabilité | Collaborateurs |
|---|---|
| Mémoriser le résultat d'un calcul de coût à une date donnée. | Menu, CalculateurCoûtRevient |

**Règles portées :** —

---

### Recette

| Responsabilité | Collaborateurs |
|---|---|
| Décrire un plat (pour une portion) ou une préparation (pour une quantité produite) par la liste de ses composants. | Composant, CatalogueRecettes, GardienComposition |

**Règles portées :** RM6, RM12

---

### Composant

| Responsabilité | Collaborateurs |
|---|---|
| Indiquer quelle quantité d'un ingrédient ou d'une préparation entre dans une recette. | Recette, Ingrédient, ConvertisseurUnités |

**Règles portées :** RM8, RM10

---

### CatalogueRecettes

| Responsabilité | Collaborateurs |
|---|---|
| Refuser une nouvelle recette dont le nom est déjà pris. | Recette |

**Règles portées :** RM9

---

### GardienComposition *(classe inventée, c'est elle qui décide)*

| Responsabilité | Collaborateurs |
|---|---|
| Décider si un composant peut entrer dans une recette sans que la recette finisse par se contenir elle-même. | Recette, Composant |

**Règles portées :** RM7

---

### TarifFournisseur

| Responsabilité | Collaborateurs |
|---|---|
| Porter un nouveau prix transmis par le fournisseur pour un ingrédient, avec sa date d'application. | Ingrédient, PrixIngrédient, CatalogueIngrédients |

**Règles portées :** —

---

### ImpactChangementPrix *(classe inventée, c'est elle qui décide)*

| Responsabilité | Collaborateurs |
|---|---|
| Décider quelles préparations, quels plats et quels menus doivent être recalculés après un changement de prix. | Ingrédient, Composant, Recette, Menu, CalculateurCoûtRevient |

**Règles portées :** RM16

---

### ListeAllergènesMenu

| Responsabilité | Collaborateurs |
|---|---|
| Garder la liste datée des allergènes d'un menu, telle qu'elle est remise au client. | Menu, Allergène, VérificateurAllergènes |

**Règles portées :** RM24

---

### VérificateurAllergènes *(classe inventée, c'est elle qui décide)*

| Responsabilité | Collaborateurs |
|---|---|
| Décider si la liste des allergènes d'un menu est complète pour être remise au client. | Menu, Recette, Composant, Ingrédient, ListeAllergènesMenu |

**Règles portées :** RM21, RM22

---

### ModificationPrix

| Responsabilité | Collaborateurs |
|---|---|
| Porter un nouveau prix saisi à la main par le Chef, avec la référence de son justificatif. | Ingrédient, PrixIngrédient, ContrôleurVariationPrix, ImpactChangementPrix, TarifFournisseur |

**Règles portées :** RM25

---

### ContrôleurVariationPrix *(classe inventée, c'est elle qui décide)*

| Responsabilité | Collaborateurs |
|---|---|
| Décider si un nouveau prix s'écarte trop de l'ancien pour être appliqué sans vérification. | PrixIngrédient, ModificationPrix, TarifFournisseur |

**Règles portées :** RM26

---

## 2. Le diagramme de classes

*Chaque carte devient une classe : attributs, méthodes, relations, cardinalités. Ce qui n'est sur aucune carte n'existe pas dans le diagramme. Dans le doute, une association plutôt qu'un héritage.*

![Diagramme de classes](diagrammes/classes.png)

**La classe qui décide :** *mettez-la en évidence sur le diagramme (une couleur), puis répondez en deux lignes.*

- **Laquelle :**
- **Quelle règle elle porte, et pourquoi elle n'existe pas dans le monde réel :**

---

## 3. Le diagramme d'états

*L'objet qui change d'état au fil du temps dans votre sujet. 3 à 6 états, avec des noms du métier. Chaque transition porte l'événement qui la déclenche. Au moins une transition d'erreur.*

**Objet choisi :**

![Diagramme d'états](diagrammes/etats.png)

---

## 4. La séquence, version classes

*Le diagramme de séquence de votre livrable 1, refait avec les classes de votre modèle. Une classe qui envoie huit messages d'affilée porte trop de logique.*

**Use case :**

![Diagramme de séquence](diagrammes/sequence-classes.png)

---

## 5. Use case → classes

*Chacun, pour ses 2 use cases : les classes qui y participent, et ce que fait chacune.*

| Use case | Porteur | Classes qui participent | Ce que fait chaque classe |
|---|---|---|---|
| UC1 | Manele Hssini | CatalogueIngrédients, Ingrédient, PrixIngrédient, Allergène | CatalogueIngrédients vérifie que le nom n'est pas déjà pris. Ingrédient garde l'unité d'achat, le caractère frais et les allergènes. PrixIngrédient enregistre le premier prix avec sa date. Allergène fournit la liste des 14 à cocher. |
| UC4 | Manele Hssini | CalculateurCoûtRevient, Menu, Recette, Composant, ConvertisseurUnités, PrixIngrédient, CoûtCalculé | CalculateurCoûtRevient vérifie le nombre de couverts, applique les 10 % de pertes et fait le total. Menu donne ses plats. Recette et Composant donnent les quantités. ConvertisseurUnités ramène chaque quantité en kilo, litre ou pièce. PrixIngrédient donne le prix du jour. CoûtCalculé garde le résultat pour le Commercial. |
| UC5 | Tania Hussain | VérificateurAllergènes, Menu, Recette, Composant, Ingrédient, Allergène, ListeAllergènesMenu | VérificateurAllergènes descend dans chaque plat et refuse la liste si un ingrédient n'a pas ses allergènes. Menu donne ses plats. Recette et Composant donnent ce qui entre dans chaque plat. Ingrédient donne ses allergènes. Allergène limite aux 14 réglementaires. ListeAllergènesMenu garde la liste validée avec sa date. |
| UC7 | Tania Hussain | ModificationPrix, Ingrédient, PrixIngrédient, ContrôleurVariationPrix, ImpactChangementPrix | ModificationPrix porte le nouveau prix et la référence de la facture. PrixIngrédient vérifie la date et garde l'ancien prix. ContrôleurVariationPrix demande une confirmation si l'écart dépasse 50 %. ImpactChangementPrix donne la liste des plats et menus à recalculer. |

---

## 6. Nos décisions de conception

*2 ou 3 décisions. Format : « Nous avons choisi… plutôt que… parce que… ».*

| # | Nous avons choisi… | plutôt que… | parce que… |
|---|---|---|---|
| D1 | | | |
| D2 | | | |

---

## 7. La carte des changements

*3 à 5 changements probables. Chacun cite son indice, dans le sujet ou dans les réponses de la cliente. Sans indice, ce n'est pas un changement probable.*

| # | Ce qui peut changer | L'indice | Où dans le modèle |
|---|---|---|---|
| C1 | | | |
| C2 | | | |
| C3 | | | |

---

*© 2026 Kawtar LAHMINI · Conception d'application · 4A ESIEA · Tous droits réservés*
