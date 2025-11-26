package com.acme.schiff.controller;

import com.acme.schiff.entity.SchiffTyp;
import com.acme.schiff.entity.Schiff;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.gaul.modernizer_maven_annotations.SuppressModernizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

import static com.acme.schiff.controller.Constants.API_PATH;
import static com.acme.schiff.controller.TestConstants.*;
import static com.acme.schiff.entity.Schiff.NAME_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Tag("rest")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("dev")
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@ExtendWith(SoftAssertionsExtension.class)
@DisplayName("REST-Schnitttelle für GET-Requests testen")
class SchiffControllerTest {
    private static final String ID_VORHANDEN = "00000000-0000-0000-0000-000000000001";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    private static final String NAME = "Titanic";

    private static final String NAME_PARAM = "name";
    private static final String TYP_PARAM = "typ";
    private static final String KREUZFAHRT = "X";
    private static final String FAEHRE = "F";

    private final SchiffRepository schiffRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")

    private SoftAssertions softly;
    SchiffControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        assertThat(ctx).isNotNull();
        final var controller = ctx.getBean(SchiffController.class);
        assertThat(controller).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA)
            .host(HOST)
            .port(port)
            .path(API_PATH)
            .build();
        final var baseUrl = uriComponents.toUriString();

        final var client = RestClient.builder()
            .requestFactory(REQUEST_FACTORY)
            .baseUrl(baseUrl)
            .build();
        final var clientAdapter = RestClientAdapter.create(client);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
        schiffRepo = proxyFactory.createClient(SchiffRepository.class);
    }
    @Test
    @DisplayName("Suche nach allen Schiffe")
    void findAll() {
        // given
        final MultiValueMap<String, String> suchparameter = MultiValueMap.fromSingleValue(Map.of());

        // when
        final var schiffe = schiffRepo.get(suchparameter);

        // then
        softly.assertThat(schiffe)
            .isNotNull()
            .isNotEmpty();
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem Namen: name={0}")
    @ValueSource(strings = NAME)
    @DisplayName("Suche mit vorhandenem Namen")
    void getByName(final String name) {
        // given
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of(NAME_PARAM, name));

        // when
        final var schiffe = schiffRepo.get(suchparameter);

        // then
        softly.assertThat(schiffe)
            .isNotNull()
            .isNotEmpty();
        schiffe
            .stream()
            .map(Schiff::getName)
            .forEach(nameTmp -> softly.assertThat(nameTmp).isEqualTo(name));
    }

    @ParameterizedTest(name = "[{index}] Suche mit einem Typ: typ={0}")
    @ValueSource(strings = {KREUZFAHRT, FAEHRE})
    @DisplayName("Suche mit einem Typ")
    void getByTyp(final String typStr) {
        // given
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of(TYP_PARAM, typStr));

        // when
        final var schiffe = schiffRepo.get(suchparameter);

        // then
        softly.assertThat(schiffe)
            .isNotNull()
            .isNotEmpty();
        final var typ = SchiffTyp.of(typStr);
        schiffe.forEach(schiff -> {
            final var typen = schiff.getTyp();
            softly.assertThat(typen)
                .isNotNull()
                .isEqualTo(typ);
        });
    }
    @Nested
    @DisplayName("Suche anhand der ID")
    class GetById{
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID und JsonPath: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID und JsonPath")
        @SuppressModernizer
        void getByIdJson(final String id){
            //given

            //when
            final var response = schiffRepo.getByIdAsString(id);

            //then
            final var body = response.getBody();
            assertThat(body).isNotNull().isNotBlank();
            final var idPath = "$.id";
            final String idTmp = JsonPath.read(body, idPath);
            softly.assertThat(idTmp).isEqualTo(id);

            final var namePath = "$.name";
            final String name = JsonPath.read(body, namePath);
            softly.assertThat(name).matches(NAME_PATTERN);

        }
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID")
        void getById(final String id) {
            // given

            // when
            final var response = schiffRepo.getById(id);

            // then
            final var schiff = response.getBody();
            assertThat(schiff).isNotNull();
            softly.assertThat(schiff.getId().toString()).isEqualTo(id);
            softly.assertThat(schiff.getName()).isNotNull();
        }
        @ParameterizedTest(name = "[{index}] Suche mit syntaktisch ungueltiger oder nicht-vorhandener ID: {0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit syntaktisch ungültiger oder nicht-vorhandener ID")
        void getByIdNichtVorhanden(final String id) {
            // when
            final var exc = catchThrowableOfType(
                HttpClientErrorException.NotFound.class,
                () -> schiffRepo.getById(id)
            );

            // then
            assertThat(exc.getStatusCode()).isEqualTo(NOT_FOUND);
        }
    }
}
