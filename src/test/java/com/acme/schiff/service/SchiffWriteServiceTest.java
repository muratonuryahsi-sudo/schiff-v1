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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.schiff.service;

import com.acme.schiff.repository.SchiffRepository;
import com.acme.schiff.repository.SchiffBuilder;
import com.acme.schiff.repository.HafenBuilder;
import com.acme.schiff.repository.CrewBuilder;
import com.acme.schiff.entity.SchiffTyp;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.gaul.modernizer_maven_annotations.SuppressModernizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Tag("unit")
@Tag("service-write")
@DisplayName("SchiffWriteService testen")
@Execution(CONCURRENT)
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@ExtendWith(SoftAssertionsExtension.class)
@SuppressWarnings("WriteTag")
class SchiffWriteServiceTest {

    private static final String NEUER_NAME_CREATE = "Spring";
    private static final String NEUER_NAME_UPDATE = "Hibernate";
    private static final int NEUE_KAPAZITAET = 1500;
    private static final String NEUER_HAFEN = "Hamburg";
    private static final String NEUER_LAND = "Deutschland";
    private static final String NEUE_BAUJAHR = "2004-03-23";
    private static final String NEUE_VORNAME = "Max";
    private static final String NEUE_NACHNAME = "Mustermann";
    private static final String NEUE_ROLLE = "Kapitän";
    private static final String ID_UPDATE = "00000000-0000-0000-0000-000000000002";
    private static final String ID_DELETE = "00000000-0000-0000-0000-000000000003";

    private final SchiffWriteService service;
    private final SchiffRepository repo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    SchiffWriteServiceTest() {
        final var constructor = SchiffRepository.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            repo = (SchiffRepository) constructor.newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }

        service = new SchiffWriteService(repo);
    }

    @ParameterizedTest(name = "[{index}] Neues Schiff anlegen: name={0}, hafen={1}")
    @CsvSource(NEUER_NAME_CREATE + "," + NEUE_BAUJAHR + "," + NEUE_KAPAZITAET + ","
        + NEUER_HAFEN + "," + NEUER_LAND + "," + NEUE_VORNAME + ","
        + NEUE_NACHNAME + "," + NEUE_ROLLE)
    @DisplayName("Neues Schiff anlegen")
    @SuppressWarnings({"MagicNumber", "BooleanExpressionComplexity"})
    void create(final ArgumentsAccessor args) {
        // given
        final var name = args.getString(0);
        final var baujahr = args.get(1, LocalDate.class);
        final var kapazitaet = args.getInteger(2);
        final var hafenname = args.getString(3);
        final var land = args.getString(4);
        final var vorname = args.getString(5);
        final var nachname = args.getString(6);
        final var rolle = args.getString(7);

        if (name == null || baujahr == null || kapazitaet == null ||
            hafenname == null || land == null || vorname == null || nachname == null || rolle == null){
            throw new IllegalStateException("Argumente dürfen nicht null sein");
        }

        final var hafen = HafenBuilder
            .getBuilder()
            .setName(hafenname)
            .setLand(NEUER_LAND)
            .build();

        final var crew = List.of(
            CrewBuilder
                .getBuilder()
                .setVorname("Max")
                .setNachname("Mustermann")
                .setRolle("Kapitän")
                .build()
        );
        final var schiff = SchiffBuilder
            .getBuilder()
            .setName(name)
            .setBaujahr(LocalDate.of(2015, 5, 5))
            .setKapazitaet(NEUE_KAPAZITAET)
            .setTyp(SchiffTyp.FAEHRE)
            .setHafen(hafen)
            .setCrewList(crew)
            .build();

        // when
        final var created = service.create(schiff);

        // then
        softly.assertThat(created.getId()).isNotNull();
        softly.assertThat(created.getName()).isEqualTo(NEUER_NAME_CREATE);
        softly.assertThat(created.getHafen().getName()).isEqualTo(NEUER_HAFEN);
    }

    @ParameterizedTest(name = "[{index}] Aendern eines vorhandenen Schiffs: id={0}")
    @ValueSource(strings = ID_UPDATE)
    @DisplayName("Aendern eines vorhandenen Schiffs")
    @SuppressModernizer
    void update(final String id) {
        // given
        final var schiffId = UUID.fromString(id);
        final var schiff = repo.findById(schiffId);
        assertThat(schiff).isNotNull();
        schiff.setName(NEUER_NAME_UPDATE);

        // when
        service.update(schiff, schiffId);

        // then
        final var result = repo.findById(schiffId);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(NEUER_NAME_UPDATE);
    }

    @ParameterizedTest(name = "[{index}] Loeschen eines vorhandenen Schiffs: id={0}")
    @ValueSource(strings = ID_DELETE)
    @DisplayName("Loeschen eines vorhandenen Schiffs")
    void deleteById(final String id) {
        // given
        final var schiffId = UUID.fromString(id);

        // when
        service.deleteById(schiffId);

        // then
        final var result = repo.findById(schiffId);
        assertThat(result).isNull();
    }
}
