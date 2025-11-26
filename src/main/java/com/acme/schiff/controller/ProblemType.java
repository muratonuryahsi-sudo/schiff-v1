package com.acme.schiff.controller;

/// Problemtypen für Fehlerbehandlung.
/// Wird z.B. für Validierungsfehler und JSON-Verarbeitungsfehler verwendet.
///
/// @author Murat Yahsi
enum ProblemType {
    /// Validierungsfehler bei Constraints.
    CONSTRAINTS("constraints"),

    /// Fehler bei der Verarbeitung von Daten.
    UNPROCESSABLE("unprocessable"),

    /// Fehler bei Vorbedingungen.
    PRECONDITION("precondition"),

    /// Fehler bei der Anfrage.
    BAD_REQUEST("badRequest");

    private final String value;

    /// Konstruktor mit dem Wert für den Problemtyp.
    ///
    /// @param value Der Wert für den Problemtyp.
    ProblemType(final String value) {
        this.value = value;
    }

    /// Den Wert des Problemtyps ermitteln.
    ///
    /// @return Der Wert des Problemtyps.
    String getValue() {
        return this.value;
    }
}
