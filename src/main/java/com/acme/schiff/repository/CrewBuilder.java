package com.acme.schiff.repository;

import com.acme.schiff.entity.Crew;
import java.util.UUID;

/// Builder für die Entity-Klasse [Crew].
///
/// @author Murat Yahsi
@SuppressWarnings({
    "NullAway.Init",
    "NotNullFieldNotInitialized"
})
public class CrewBuilder {

    private UUID id;
    private String vorname;
    private String nachname;
    private String rolle;

    /**
     * Standardkonstruktor für den Builder.
     * Erforderlich für PMD-Konformität.
     */
    public CrewBuilder() {
        // Default constructor for PMD compliance
    }

    /**
     * Gibt eine neue Instanz des CrewBuilder zurück.
     *
     * @return Neue Instanz von CrewBuilder
     */
    public static CrewBuilder getBuilder() {
        return new CrewBuilder();
    }

    /**
     * Setzt die ID des Crew-Mitglieds.
     *
     * @param id Eindeutige UUID
     * @return Aktueller Builder
     */
    public CrewBuilder setId(final UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Setzt den Vornamen des Crew-Mitglieds.
     *
     * @param vorname z. B. „James“
     * @return Aktueller Builder
     */
    public CrewBuilder setVorname(final String vorname) {
        this.vorname = vorname;
        return this;
    }

    /**
     * Setzt den Nachnamen des Crew-Mitglieds.
     *
     * @param nachname z. B. „Smith“
     * @return Aktueller Builder
     */
    public CrewBuilder setNachname(final String nachname) {
        this.nachname = nachname;
        return this;
    }

    /**
     * Setzt die Rolle des Crew-Mitglieds.
     *
     * @param rolle z. B. „Kapitän“, „Techniker“
     * @return Aktueller Builder
     */
    public CrewBuilder setRolle(final String rolle) {
        this.rolle = rolle;
        return this;
    }

    /**
     * Baut das Crew-Objekt mit den gesetzten Attributen.
     *
     * @return Instanz der Klasse {@link Crew}
     */
    public Crew build() {
        return new Crew(id, vorname, nachname, rolle);
    }
}
