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
package com.acme.schiff.controller;

import com.acme.schiff.controller.SchiffDTO.OnCreate;
import com.acme.schiff.service.SchiffWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.acme.schiff.controller.Constants.API_PATH;
import static com.acme.schiff.controller.Constants.ID_PATTERN;
import static com.acme.schiff.controller.Constants.VERSION_1;
import static com.acme.schiff.controller.Constants.VERSION_1_EXAMPLE;
import static com.acme.schiff.controller.Constants.X_VERSION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;
import static org.springframework.http.ResponseEntity.created;

/// Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
/// Methoden der Klasse abgebildet werden.
/// ![Klassendiagramm](../../../../../asciidoc/SchiffWriteController.svg)
///
/// @author Murat Yahsi
@Controller
@RequestMapping(API_PATH)
@Validated
@SuppressWarnings({"ClassFanOutComplexity", "MethodCount", "java:S1075"})
class SchiffWriteController {
    private final SchiffWriteService service;
    private final SchiffMapper mapper;
    private final UriHelper uriHelper;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor für Constructor Injection durch Spring.
    ///
    /// @param service   Schreibservice für Schiffe
    /// @param mapper    Mapper zum Umwandeln von DTO zu Entity
    /// @param uriHelper Hilfsklasse zum Erzeugen von URIs
    SchiffWriteController(final SchiffWriteService service, final SchiffMapper mapper, final UriHelper uriHelper) {
        this.service = service;
        this.mapper = mapper;
        this.uriHelper = uriHelper;
    }

    /// Ein neues Schiff anlegen.
    ///
    /// @param schiffDTO     Eingabedaten als DTO
    /// @param request HTTP-Request für URI-Ermittlung
    /// @return HTTP 201 mit Location-Header oder 400/422 im Fehlerfall
    @PostMapping(version = VERSION_1)
    @Operation(summary = "Ein neues Schiff anlegen", tags = "Neuanlegen")
    @Parameter(name = X_VERSION, in = ParameterIn.HEADER, example = VERSION_1_EXAMPLE,
        schema = @Schema(implementation = String.class))
    @ApiResponse(responseCode = "201", description = "Schiff neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte vorhanden")
    ResponseEntity<Void> post(
        @RequestBody @Validated({Default.class, OnCreate.class}) final SchiffDTO schiffDTO,
                              final HttpServletRequest request) {
        getLogger().debug("post: {}", schiffDTO);
        final var schiffInput = mapper.toSchiff(schiffDTO);
        final var result = service.create(schiffInput);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var location = URI.create(baseUri + "/" + result.getId());
        return created(location).build();
    }

    /// Ein vorhandenes Schiff aktualisieren.
    ///
    /// @param id  ID des zu ändernden Schiffs
    /// @param schiffDTO Neue Werte als DTO
    @PutMapping(path = "{id:" + ID_PATTERN + "}", version = VERSION_1)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Ein Schiff mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @Parameter(name = X_VERSION, in = ParameterIn.HEADER, example = VERSION_1_EXAMPLE,
        schema = @Schema(implementation = String.class))
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Schiff nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte vorhanden")
    void put(
        @PathVariable final UUID id,
        @RequestBody @Validated final SchiffDTO schiffDTO
    ) {
        getLogger().debug("put: id={}, {}", id, schiffDTO);
        final var schiff = mapper.toSchiff(schiffDTO);
        service.update(schiff, id);
    }

    /// Ein Schiff anhand seiner ID löschen.
    ///
    /// @param id ID des zu löschenden Schiffs
    @DeleteMapping(path = "{id:" + ID_PATTERN + "}", version = VERSION_1)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Eine Schiff anhand der ID loeschen", tags = "Loeschen")
    @Parameter(name = X_VERSION, in = ParameterIn.HEADER, example = VERSION_1_EXAMPLE,
        schema = @Schema(implementation = String.class))
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    @ApiResponse(responseCode = "404", description = "Schiff nicht vorhanden")
    void deleteById(@PathVariable final UUID id) {
        getLogger().debug("deleteById: id={}", id);
        service.deleteById(id);
    }

    /// [ExceptionHandler] für [MethodArgumentNotValidException]
    ///
    /// @param ex Exception für Fehler im Request-Body bei `POST` oder `PUT` gemäß _Jakarta Validation_.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onConstraintViolations(final MethodArgumentNotValidException ex) {
        getLogger().debug("onConstraintViolations: {}", ex.getMessage());

        final var detailMessages = ex.getDetailMessageArguments();
        final var detail = detailMessages.length == 0 || detailMessages[1] == null
            ? "Constraint Violation"
            : ((String) detailMessages[1]).replace(", and ", ", ");
        return ErrorResponse.create(ex, UNPROCESSABLE_CONTENT, detail);
    }

    /// [ExceptionHandler] für [HttpMessageNotReadableException]
    ///
    /// @param ex Exception für den syntaktisch falschen Request-Body bei `POST` oder `PUT`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onMessageNotReadable(final HttpMessageNotReadableException ex) {
        final var msg = ex.getMessage() == null ? "N/A" : ex.getMessage();
        getLogger().debug("onMessageNotReadable: {}", msg);
        return ErrorResponse.create(ex, BAD_REQUEST, msg);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(SchiffWriteController.class));
    }
}
