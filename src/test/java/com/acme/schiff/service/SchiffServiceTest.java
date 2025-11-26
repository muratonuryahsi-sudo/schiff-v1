package com.acme.schiff.service;

import com.acme.schiff.entity.Schiff;
import com.acme.schiff.entity.SchiffTyp;
import com.acme.schiff.repository.SchiffRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Tag("unit")
@Tag("service-read")
@DisplayName("Geschäftslogik für Schiffe testen")
@Execution(CONCURRENT)
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@ExtendWith(SoftAssertionsExtension.class)
@SuppressWarnings({"WriteTag", "PMD.AtLeastOneConstructor"})
class SchiffServiceTest {
    private static final String ID_VORHANDEN = "00000000-0000-0000-0000-000000000001";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    private static final String NAME = "Titanic";

    private final SchiffService service;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    SchiffServiceTest() {
        final var constructor = SchiffRepository.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        final SchiffRepository repo;
        try {
            repo = (SchiffRepository) constructor.newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }

        service = new SchiffService(repo);
    }

    @Test
    @DisplayName("Suche nach allen Schiffen")
    void findAll() {
        final var schiffe = service.find(Collections.emptyMap());
        assertThat(schiffe).isNotEmpty();
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem Namen: name={0}")
    @ValueSource(strings = NAME)
    @DisplayName("Suche mit vorhandenem Namen")
    void findByName(final String name) {
        final var params = Map.of("name", List.of(name));
        final var schiffe = service.find(params);

        softly.assertThat(schiffe).isNotEmpty();
        schiffe.stream()
            .map(Schiff::getName)
            .forEach(n -> softly.assertThat(n).isEqualTo(name));
    }

    @Nested
    @DisplayName("Suche anhand der ID")
    class FindById {
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID")
        void findById(final String id) {
            final var schiffId = UUID.fromString(id);
            final var schiff = service.findById(schiffId);

            assertThat(schiff)
                .isNotNull()
                .extracting(Schiff::getId)
                .isEqualTo(schiffId);
        }

        @ParameterizedTest(name = "[{index}] Suche mit nicht-vorhandener ID: id={0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit nicht-vorhandener ID")
        void findByIdNichtVorhanden(final String id) {
            final var schiffId = UUID.fromString(id);

            final var notFoundException = catchThrowableOfType(
                NotFoundException.class,
                () -> service.findById(schiffId)
            );

            assertThat(notFoundException)
                .isNotNull()
                .extracting(NotFoundException::getId)
                .isEqualTo(schiffId);
        }
    }
    @Test
    @DisplayName("Suche nach Schiffstyp FAEHRE")
    void findByTyp() {
        final var params = Map.of("typ", List.of("F"));
        final var schiffe = service.find(params);

        softly.assertThat(schiffe)
            .isNotEmpty()
            .allSatisfy(s -> softly.assertThat(s.getTyp()).isEqualTo(SchiffTyp.FAEHRE));
    }

}
