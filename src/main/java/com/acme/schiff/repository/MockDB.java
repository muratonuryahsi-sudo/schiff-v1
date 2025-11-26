package com.acme.schiff.repository;

import com.acme.schiff.entity.Schiff;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import static com.acme.schiff.entity.SchiffTyp.CONTAINER;
import static com.acme.schiff.entity.SchiffTyp.FAEHRE;
import static com.acme.schiff.entity.SchiffTyp.KREUZFAHRT;
import static com.acme.schiff.entity.SchiffTyp.MILITAER;
import static com.acme.schiff.entity.SchiffTyp.SEGELBOOT;

/// Emulation der Datenbasis für persistente Schiffe.
/// Wird z. B. vom Repository [SchiffRepository] verwendet.
///
/// @author Murat Yahsi
@SuppressWarnings({
    "UtilityClassCanBeEnum",
    "UtilityClass",
    "MagicNumber",
    "RedundantSuppression",
    "java:S1192"
})
final class MockDB {
    /// Liste der Schiffe zur Emulation der Datenbank.
    @SuppressWarnings("StaticCollection")
    static final List<Schiff> SCHIFFE = new ArrayList<>();

    static {
        SCHIFFE.addAll(Stream.of(
            // admin
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000010"))
                .setName("Titanic")
                .setBaujahr(LocalDate.of(1912, 4, 2))
                .setKapazitaet(3300)
                .setTyp(KREUZFAHRT)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Southampton")
                    .setLand("England")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Edward John")
                        .setNachname("Smith")
                        .setRolle("Kapitän")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("Thomas")
                        .setNachname("Andrews Jr.")
                        .setRolle("Ingenieur")
                        .build()
                ))
                .build(),

            // HTTP GET
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setName("Test-Schiff")
                .setBaujahr(LocalDate.of(2010, 8, 15))
                .setKapazitaet(1200)
                .setTyp(FAEHRE)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Istanbul")
                    .setLand("Türkei")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Mustafa")
                        .setNachname("")
                        .setRolle("Maschinist")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("Kemal")
                        .setNachname("")
                        .setRolle("Navigator")
                        .build()
                ))
                .build(),

            // HTTP PUT
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .setName("Queen-Mary")
                .setBaujahr(LocalDate.of(1936, 5, 27))
                .setKapazitaet(2800)
                .setTyp(KREUZFAHRT)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Liverpool")
                    .setLand("England")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Anne")
                        .setNachname("Smith")
                        .setRolle("Kapitänin")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("James")
                        .setNachname("Brown")
                        .setRolle("Erster Offizier")
                        .build()
                ))
                .build(),

            // HTTP PATCH
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000003"))
                .setName("Ever-Given")
                .setBaujahr(LocalDate.of(2018, 5, 25))
                .setKapazitaet(20_000)
                .setTyp(CONTAINER)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Shanghai")
                    .setLand("China")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Li")
                        .setNachname("Wei")
                        .setRolle("Kapitänin")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("Zhang")
                        .setNachname("Wei")
                        .setRolle("Navigator")
                        .build()
                ))
                .build(),

            // HTTP DELETE
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000004"))
                .setName("Bismarck")
                .setBaujahr(LocalDate.of(1939, 2, 14))
                .setKapazitaet(2200)
                .setTyp(MILITAER)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Hamburg")
                    .setLand("Deutschland")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Hannah")
                        .setNachname("Lindemann")
                        .setRolle("Kapitänin")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("Hans")
                        .setNachname("Oels")
                        .setRolle("Erster Offizier")
                        .build()
                ))
                .build(),

            // zur freien Verfügung
            SchiffBuilder.getBuilder()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000005"))
                .setName("Black-Pearl")
                .setBaujahr(LocalDate.of(1700, 1, 1))
                .setKapazitaet(100)
                .setTyp(SEGELBOOT)
                .setHafen(HafenBuilder.getBuilder()
                    .setName("Tortuga")
                    .setLand("Karibik")
                    .build())
                .setCrewList(List.of(
                    CrewBuilder.getBuilder()
                        .setVorname("Jack")
                        .setNachname("Sparrow")
                        .setRolle("Kapitän")
                        .build(),
                    CrewBuilder.getBuilder()
                        .setVorname("Hector")
                        .setNachname("Barbossa")
                        .setRolle("Erster Offizier")
                        .build()
                ))
                .build()
        ).toList());
    }

    /// Privater Konstruktor, da die Klasse nicht instanziiert werden soll.
    private MockDB() {
    }
}
