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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/// Daten ein Schiff. In DDD ist Schiff ist ein Aggregate Root.
/// ![Klassendiagramm](../../../../../asciidoc/Schiff.svg)
///
/// @author [Murat Yahsi](mailto:yamu1012@h-ka.de)
// Maven: [Klassendiagramm](../../../../../../generated-docs/Schiff.svg)
// https://thorben-janssen.com/java-records-hibernate-jpa
public class Schiff {
    public static final String NAME_PATTERN =
        "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
    private UUID id;
    private String name;
    private LocalDate baujahr;
    private int kapazitaet;
    private SchiffTyp typ;
    private Hafen hafen;
    private List<Crew> crewList;

    /// Konstruktor für Schiff.
    ///
    /// @param id          Eindeutige ID des Schiffs
    /// @param name        Name des Schiffs (z. B. „Titanic“)
    /// @param baujahr     Baujahr des Schiffs
    /// @param kapazitaet  Maximale Kapazität (z. B. Anzahl Passagiere)
    /// @param typ         Schiffstyp (z. B. Kreuzfahrt, Container)
    /// @param hafen       Heimathafen des Schiffs
    /// @param crewList    Liste der zugehörigen Crew-Mitglieder
    public Schiff(final UUID id, final String name, final LocalDate baujahr, final int kapazitaet,
                  final SchiffTyp typ, final Hafen hafen, final List<Crew> crewList) {
        this.id = id;
        this.name = name;
        this.baujahr = baujahr;
        this.kapazitaet = kapazitaet;
        this.typ = typ;
        this.hafen = hafen;
        this.crewList = crewList;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Schiff schiff && Objects.equals(id, schiff.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /// Gibt die ID zurück.
    ///
    /// @return UUID des Schiffs
    public UUID getId() {
        return id;
    }

    /// Setzt die ID des Schiffs.
    ///
    /// @param id UUID
    public void setId(final UUID id) {
        this.id = id;
    }

    /// Gibt den Namen des Schiffs zurück.
    ///
    /// @return Name des Schiffs
    public String getName() {
        return name;
    }

    /// Setzt den Namen des Schiffs.
    ///
    /// @param name z. B. „Ever-Given“
    public void setName(final String name) {
        this.name = name;
    }

    /// Gibt das Baujahr zurück.
    ///
    /// @return Baujahr als LocalDate
    public LocalDate getBaujahr() {
        return baujahr;
    }

    /// Setzt das Baujahr des Schiffs.
    ///
    /// @param baujahr z. B. 2005-04-01
    public void setBaujahr(final LocalDate baujahr) {
        this.baujahr = baujahr;
    }

    /// Gibt die Kapazität des Schiffs zurück.
    ///
    /// @return Anzahl der maximalen Personen oder Einheiten
    public int getKapazitaet() {
        return kapazitaet;
    }

    /// Setzt die Kapazität des Schiffs.
    ///
    /// @param kapazitaet z. B. 5000
    public void setKapazitaet(final int kapazitaet) {
        this.kapazitaet = kapazitaet;
    }

    /// Gibt den Schiffstyp zurück.
    ///
    /// @return Typ des Schiffs (Enum)
    public SchiffTyp getTyp() {
        return typ;
    }

    /// Setzt den Typ des Schiffs.
    ///
    /// @param typ z. B. KREUZFAHRT, CONTAINER
    public void setTyp(final SchiffTyp typ) {
        this.typ = typ;
    }

    /// Gibt den Heimathafen zurück.
    ///
    /// @return Heimathafen des Schiffs
    public Hafen getHafen() {
        return hafen;
    }

    /// Setzt den Heimathafen.
    ///
    /// @param hafen z. B. „Hamburg“
    public void setHafen(final Hafen hafen) {
        this.hafen = hafen;
    }

    /// Gibt die Liste der Crew-Mitglieder zurück.
    ///
    /// @return Liste von Crew-Objekten
    public List<Crew> getCrewList() {
        return crewList;
    }

    /// Setzt die Liste der Crew-Mitglieder.
    ///
    /// @param crewList Liste von Crew-Objekten
    public void setCrewList(final List<Crew> crewList) {
        this.crewList = crewList;
    }

    @Override
    public String toString() {
        return "Schiff{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", baujahr=" + baujahr +
            ", kapazitaet=" + kapazitaet +
            ", typ=" + typ +
            '}';
    }
}
