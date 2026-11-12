package fr.esiea.menus.console;

/** L'entrée standard est fermée : on quitte proprement. */
class FinDeSaisie extends RuntimeException {

    FinDeSaisie() {
        super("fin de la saisie");
    }
}
