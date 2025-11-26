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
package com.acme.schiff.controller;

import com.acme.schiff.entity.SchiffTyp;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import static com.acme.schiff.config.DevConfig.DEV;
import static com.acme.schiff.controller.Constants.API_PATH;
import static com.acme.schiff.controller.TestConstants.API_VERSION_INSERTER;
import static com.acme.schiff.controller.TestConstants.HOST;
import static com.acme.schiff.controller.TestConstants.REQUEST_FACTORY;
import static com.acme.schiff.controller.TestConstants.SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@Tag("integration")
@Tag("rest")
@Tag("rest-write")
@DisplayName("REST-Schnittstelle für Schreiben testen")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@SuppressWarnings({"WriteTag", "PMD.AtLeastOneConstructor"})
class SchiffWriteControllerTest {

    private static final String ID_UPDATE_PUT = "00000000-0000-0000-0000-000000000002";
    private static final String ID_DELETE = "00000000-0000-0000-0000-000000000003";
    private static final String UNGUELTIGES_BAUJAHR = "2100-01-01";
    private static final String NEUER_NAME = "Karlsruher";
    private static final String NEUER_TYP = "F";
    private static final int NEUE_KAPAZITAET = 900;
    private static final String NEUER_HAFEN = "Hamburg";
    private static final String NEUES_BAUJAHR = "2015-05-05";
    private static final String NEUES_LAND = "Deutschland";
    private static final String NEUE_VORNAME = "Max";
    private static final String NEUE_NACHNAME = "Mustermann";
    private static final String NEUE_ROLLE = "Kapitän";
    private static final String NAME_INVALID = "?!$";
    private static final int KAPAZITAET_INVALID = 0;

    private final SchiffRepository schiffRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressFBWarnings("CT")
    SchiffWriteControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        assertThat(ctx).isNotNull();
        final var controller = ctx.getBean(SchiffWriteController.class);
        assertThat(controller).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA)
            .host(HOST)
            .port(port)
            .path(API_PATH)
            .build();
        final var baseUrl = uriComponents.toUriString();
        final var restClient = RestClient
            .builder()
            .baseUrl(baseUrl)
            .requestFactory(REQUEST_FACTORY)
            .apiVersionInserter(API_VERSION_INSERTER)
            .build();
        final var clientAdapter = RestClientAdapter.create(restClient);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
        schiffRepo = proxyFactory.createClient(SchiffRepository.class);
    }

    @Nested
    @DisplayName("Erzeugen")
    class Erzeugen {
        @ParameterizedTest(name = "[{index}] Neues Schiff anlegen: name={0}, typ={1}")
        @CsvSource(NEUER_NAME + "," + NEUER_TYP + "," + NEUES_BAUJAHR + "," +
            NEUE_KAPAZITAET + "," + NEUER_HAFEN + "," + NEUES_LAND + "," +
            NEUE_VORNAME + "," + NEUE_NACHNAME + "," + NEUE_ROLLE)
        @DisplayName("Neues Schiff anlegen")
        void post(final ArgumentsAccessor args) {
            // given
            final var name = args.getString(0);
            final var typStr = args.getString(1);
            final var baujahr = args.get(2, LocalDate.class);
            final var kapazitaet = args.getInteger(3);
            final var hafenname = args.getString(4);
            final var land = args.getString(5);
            final var vorname = args.getString(6);
            final var nachname = args.getString(7);
            final var rolle = args.getString(8);

            if (name == null || typStr == null || baujahr == null || kapazitaet == null ||
            hafenname == null || land == null || vorname == null || nachname == null || rolle == null){
                throw new IllegalStateException("Testdaten sind null");
            }

            final var schiffDTO = new SchiffDTO(
                name,
                baujahr,
                kapazitaet,
                SchiffTyp.of(typStr),
                new HafenDTO(hafenname, land),
                List.of(new CrewDTO(vorname, nachname, rolle))
            );

            // when
            final var response = schiffRepo.post(schiffDTO);

            // then
            assertThat(response).isNotNull();
            softly.assertThat(response.getStatusCode()).isEqualTo(CREATED);
            final var location = response.getHeaders().getLocation();
            assertThat(location)
                .isNotNull()
                .isInstanceOf(URI.class);
        }

        @ParameterizedTest(name = "[{index}] Neues Schiff mit ungueltigen Daten: name={0}")
        @CsvSource(NAME_INVALID + "," + NEUER_TYP + "," + UNGUELTIGES_BAUJAHR + "," +
            KAPAZITAET_INVALID + "," + NEUER_HAFEN + "," + NEUES_LAND)
        @DisplayName("Neues Schiff mit ungueltigen Daten")
        void postInvalid(final ArgumentsAccessor args) {
            // given
            final var name = args.getString(0);
            final var typStr = args.getString(1);
            final var baujahr = args.get(2, LocalDate.class);
            final var kapazitaet = args.getInteger(3);
            final var hafenname = args.getString(4);
            final var land = args.getString(5);

            if (name == null || typStr == null || baujahr == null || kapazitaet == null ||
                hafenname == null || land == null ) {
                throw new IllegalStateException("Testdaten sind null");
            }
            final var schiffDTO = new SchiffDTO(
                name,
                baujahr,
                kapazitaet,
                SchiffTyp.of(typStr),
                new HafenDTO(hafenname, land),
                null
            );
            final var violationKeys = List.of("name", "typStr", "baujahr", "kapazitaet", "hafennam", "land");

            // when
            final var exc = catchThrowableOfType(
                HttpClientErrorException.UnprocessableContent.class,
                () -> schiffRepo.post(schiffDTO)
            );

            // then
            assertThat(exc.getStatusCode()).isEqualTo(UNPROCESSABLE_CONTENT);
            final var body = exc.getResponseBodyAs(ProblemDetail.class);
            assertThat(body).isNotNull();
            final var detail = body.getDetail();
            assertThat(detail).isNotNull();
            final var violations = detail.split(", ");
            final var actualViolationKeys = Arrays.stream(violations)
                .map(v -> v.substring(0, v.indexOf(": ")))
                .toList();
            assertThat(actualViolationKeys).containsAnyElementsOf(violationKeys);
        }
    }

    @Nested
    @DisplayName("Aendern")
    class Aendern {
        @ParameterizedTest(name = "[{index}] Aendern eines vorhandenen Schiffs durch PUT: id={0}")
        @ValueSource(strings = ID_UPDATE_PUT)
        @DisplayName("Aendern eines vorhandenen Schiffs durch PUT")
        void put(final String id) {
            // given
            final var schiffOrig = schiffRepo.getById(id).getBody();
            assertThat(schiffOrig).isNotNull();
            final var crewOrig = schiffOrig.getCrewList();
            final List<CrewDTO> crew;
            if (crewOrig == null) {
                crew = null;
            } else {
                crew = crewOrig.stream()
                    .map(c -> new CrewDTO(c.getVorname(), c.getNachname(), c.getRolle()))
                    .toList();
            }

            final var hafenOrig = schiffOrig.getHafen();
            final var hafen = new HafenDTO(hafenOrig.getName(), hafenOrig.getLand());

            final var schiff = new SchiffDTO(
                schiffOrig.getName() + "put",
                schiffOrig.getBaujahr(),
                schiffOrig.getKapazitaet(),
                schiffOrig.getTyp(),
                hafen,
                crew
            );

            // when
            final var response = schiffRepo.put(id, schiff);

            // then
            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        }
    }

    @Nested
    @DisplayName("Loeschen")
    class Loeschen {
        @ParameterizedTest(name = "[{index}] Loeschen eines vorhandenen Schiffs: id={0}")
        @ValueSource(strings = ID_DELETE)
        @DisplayName("Loeschen eines vorhandenen Schiffs")
        void deleteById(final String id) {
            // when
            final var response = schiffRepo.deleteById(id);

            // then
            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        }
    }
}
