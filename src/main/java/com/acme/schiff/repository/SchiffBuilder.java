package com.acme.schiff.repository;

import com.acme.schiff.entity.Crew;
import com.acme.schiff.entity.Hafen;
import com.acme.schiff.entity.Schiff;
import com.acme.schiff.entity.SchiffTyp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/// Builder-Klasse für die Klasse [Schiff].
///
/// @author Murat Yahsi
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
public class SchiffBuilder {

    @Nullable
    private UUID id;
    private String name;
    private LocalDate baujahr;
    private int kapazitaet;
    private SchiffTyp typ;
    private Hafen hafen;
    private List<Crew> crewList;

    /// Standardkonstruktor für den Builder.
    public SchiffBuilder() {
        // Default constructor for PMD compliance
    }

    /// Ein Builder-Objekt für die Klasse [Schiff] erzeugen.
    ///
    /// @return Das neue Builder-Objekt.
    public static SchiffBuilder getBuilder() {
        return new SchiffBuilder();
    }

    /// ID setzen.
    ///
    /// @param id Die ID des Schiffs.
    /// @return Das Builder-Objekt.
    public SchiffBuilder setId(final UUID id) {
        this.id = id;
        return this;
    }

    /// Name setzen.
    ///
    /// @param name Der Name des Schiffs.
    /// @return Das Builder-Objekt.
    public SchiffBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    /// Baujahr setzen.
    ///
    /// @param baujahr Das Baujahr des Schiffs.
    /// @return Das Builder-Objekt.
    public SchiffBuilder setBaujahr(final LocalDate baujahr) {
        this.baujahr = baujahr;
        return this;
    }

    /// Kapazität setzen.
    ///
    /// @param kapazitaet Die maximale Kapazität (z.B. Anzahl Passagiere).
    /// @return Das Builder-Objekt.
    public SchiffBuilder setKapazitaet(final int kapazitaet) {
        this.kapazitaet = kapazitaet;
        return this;
    }

    /// Schiffstyp setzen.
    ///
    /// @param typ Der Typ des Schiffs (z.B. Kreuzfahrt, Fähre).
    /// @return Das Builder-Objekt.
    public SchiffBuilder setTyp(final SchiffTyp typ) {
        this.typ = typ;
        return this;
    }

    /// Hafen setzen.
    ///
    /// @param hafen Der Heimathafen des Schiffs.
    /// @return Das Builder-Objekt.
    public SchiffBuilder setHafen(final Hafen hafen) {
        this.hafen = hafen;
        return this;
    }

    /// Liste der CrewList setzen.
    ///
    /// @param crewList Die CrewList des Schiffs.
    /// @return Das Builder-Objekt.
    public SchiffBuilder setCrewList(final List<Crew> crewList) {
        this.crewList = crewList;
        return this;
    }

    /// Das finale [Schiff]-Objekt bauen.
    ///
    /// @return Das fertig gebaute Schiff-Objekt.
    public Schiff build() {
        return new Schiff(id, name, baujahr, kapazitaet, typ, hafen, crewList);
    }
}
