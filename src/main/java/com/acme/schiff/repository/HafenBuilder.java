package com.acme.schiff.repository;

import com.acme.schiff.entity.Hafen;
import java.util.UUID;

/// Builder-Klasse für die Klasse [Hafen].
/// Wird verwendet, um testweise Hafen-Objekte z. B. in [MockDB] zu erzeugen.
///
/// @author Murat Yahsi
@SuppressWarnings(
    "NullAway.Init"
)
public class HafenBuilder {

    private UUID id;
    private String name;
    private String land;

    /// Standardkonstruktor für den Builder.
    public HafenBuilder() {
        // Default constructor for PMD compliance
    }

    /// Ein Builder-Objekt für die Klasse [Hafen] erzeugen.
    ///
    /// @return Das neue Builder-Objekt.
    public static HafenBuilder getBuilder() {
        return new HafenBuilder();
    }

    /// Die ID des Hafens setzen.
    ///
    /// @param id Die UUID des Hafens.
    /// @return Das Builder-Objekt.
    public HafenBuilder setId(final UUID id) {
        this.id = id;
        return this;
    }

    /// Den Namen des Hafens setzen.
    ///
    /// @param name Der Name des Hafens (z. B. „Hamburg").
    /// @return Das Builder-Objekt.
    public HafenBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    /// Das Land des Hafens setzen.
    ///
    /// @param land Das Land (z. B. „Deutschland").
    /// @return Das Builder-Objekt.
    public HafenBuilder setLand(final String land) {
        this.land = land;
        return this;
    }

    /// Das finale [Hafen]-Objekt bauen.
    ///
    /// @return Das gebaute Hafen-Objekt.
    public Hafen build() {
        return new Hafen(id, name, land);
    }
}
