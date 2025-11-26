/*
 * Copyright (C) 2025 - present Murat Yahsi, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.schiff.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

/// Enum für Schiffstypen.
/// Dazu können auf der Clientseite z.B. Auswahlfelder oder Icons für Schiffskategorien realisiert werden.
///
/// @author Murat Yahsi
public enum SchiffTyp {
    /// _Kreuzfahrtschiff_ mit dem internen Wert `X` für z.B. das Mapping in einem JSON-Datensatz.
    KREUZFAHRT("X"),

    /// _Containerschiff_ mit dem internen Wert `C`.
    CONTAINER("C"),

    /// _Militärisches Schiff_ mit dem internen Wert `M`.
    MILITAER("M"),

    /// _Fähre_ mit dem internen Wert `F`.
    FAEHRE("F"),

    /// _Segelboot_ mit dem internen Wert `S`.
    SEGELBOOT("S");

    /// Der interne Wert zur Serialisierung, z.B. bei JSON.
    private final String value;

    /// Konstruktor mit dem internen Wert für die Enum-Konstanten.
    ///
    /// @param value Interner Wert für die Enum-Konstante.
    SchiffTyp(final String value) {
        this.value = value;
    }

    /// Einen Enum-Wert als String mit dem internen Wert ausgeben.
    /// Dieser Wert wird z.B. durch Jackson für einen JSON-Datensatz verwendet.
    ///
    /// @return Der interne Wert.
    @JsonValue
    public String getValue() {
        return value;
    }

    /// Konvertierung eines Strings in einen passenden Enum-Wert.
    /// Wird z.B. beim Deserialisieren von JSON aufgerufen.
    ///
    /// @param value Der String-Wert.
    /// @return Passender Enum-Wert oder `null`, falls keiner gefunden wurde.
    @JsonCreator
    public static SchiffTyp of(final String value) {
        return Stream.of(values())
            .filter(schiff -> schiff.value.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
