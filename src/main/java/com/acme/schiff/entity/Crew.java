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

/// Daten eines Crew-Mitglieds.
///
/// @author Murat Yahsi
@SuppressWarnings("PMD.ShortClassName")
public class Crew {
    private UUID id;
    private String vorname;
    private String nachname;
    private String rolle;

    /// Konstruktor für Crew.
    ///
    /// @param id       ID des Crew-Mitglieds
    /// @param vorname  Vorname des Crew-Mitglieds
    /// @param nachname Nachname des Crew-Mitglieds
    /// @param rolle    Rolle an Bord (z. B. Kapitän, Techniker)
    public Crew(final UUID id, final String vorname, final String nachname, final String rolle) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.rolle = rolle;
    }
    @Override
    public boolean equals(final Object other) {
        return other instanceof Crew crew && Objects.equals(id, crew.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /// Gibt die ID zurück.
    ///
    /// @return UUID des Crew-Mitglieds
    public UUID getId() {
        return id;
    }

    /// Setzt die ID des Crew-Mitglieds.
    ///
    /// @param id UUID des Crew-Mitglieds
    public void setId(final UUID id) {
        this.id = id;
    }

    /// Gibt den Vornamen zurück.
    ///
    /// @return Vorname des Crew-Mitglieds
    public String getVorname() {
        return vorname;
    }

    /// Setzt den Vornamen des Crew-Mitglieds.
    ///
    /// @param vorname Vorname
    public void setVorname(final String vorname) {
        this.vorname = vorname;
    }

    /// Gibt den Nachnamen zurück.
    ///
    /// @return Nachname des Crew-Mitglieds
    public String getNachname() {
        return nachname;
    }

    /// Setzt den Nachnamen des Crew-Mitglieds.
    ///
    /// @param nachname Nachname
    public void setNachname(final String nachname) {
        this.nachname = nachname;
    }

    /// Gibt die Rolle zurück.
    ///
    /// @return Rolle (z. B. Kapitän)
    public String getRolle() {
        return rolle;
    }

    /// Setzt die Rolle des Crew-Mitglieds.
    ///
    /// @param rolle z. B. Kapitän, Navigator
    public void setRolle(final String rolle) {
        this.rolle = rolle;
    }

    @Override
    public String toString() {
        return "Crew{" +
            "id=" + id +
            ", vorname='" + vorname + '\'' +
            ", nachname='" + nachname + '\'' +
            ", rolle='" + rolle + '\'' +
            '}';
    }
}
