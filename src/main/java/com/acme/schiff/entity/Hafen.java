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

import java.util.Objects;
import java.util.UUID;

/// Daten eines Hafens.
///
/// @author Murat Yahsi
public class Hafen {
    private UUID id;
    private String name;
    private String land;

    /// Konstruktor für Hafen.
    ///
    /// @param id   Eindeutige ID des Hafens
    /// @param name Name des Hafens (z. B. „Hamburg“)
    /// @param land Land, in dem der Hafen liegt (z. B. „Deutschland“)
    public Hafen(final UUID id, final String name, final String land) {
        this.id = id;
        this.name = name;
        this.land = land;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Hafen hafen && Objects.equals(id, hafen.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /// Gibt die ID des Hafens zurück.
    ///
    /// @return UUID des Hafens
    public UUID getId() {
        return id;
    }

    /// Setzt die ID des Hafens.
    ///
    /// @param id UUID des Hafens
    public void setId(final UUID id) {
        this.id = id;
    }

    /// Gibt den Namen des Hafens zurück.
    ///
    /// @return Name des Hafens
    public String getName() {
        return name;
    }

    /// Setzt den Namen des Hafens.
    ///
    /// @param name z. B. „Rotterdam“, „Istanbul“
    public void setName(final String name) {
        this.name = name;
    }

    /// Gibt das Land des Hafens zurück.
    ///
    /// @return Land, in dem sich der Hafen befindet
    public String getLand() {
        return land;
    }

    /// Setzt das Land des Hafens.
    ///
    /// @param land z. B. „Deutschland“, „China“
    public void setLand(final String land) {
        this.land = land;
    }

    @Override
    public String toString() {
        return "Hafen{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", land='" + land + '\'' +
            '}';
    }
}
