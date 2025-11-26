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
package com.acme.schiff.service;

import com.acme.schiff.entity.Schiff;
import com.acme.schiff.repository.SchiffRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/// Anwendungslogik für Schiffe.
/// [Klassendiagramm](../../../../../asciidoc/SchiffWriteService.svg)
///
/// @author Murat Yahsi
@Service
public class SchiffWriteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchiffWriteService.class);

    private final SchiffRepository repo;

    /// Konstruktor für _Constructor Injection_ durch _Spring_.
    ///
    /// @param repo Injiziertes Repository für Zugriff auf Schiff-Entitäten.
    SchiffWriteService(final SchiffRepository repo) {
        this.repo = repo;
    }

    /// Ein neues Schiff anlegen.
    ///
    /// @param schiff Zu speicherndes Schiff-Objekt.
    /// @return Gespeichertes Schiff mit generierter ID.
    /// @throws ResponseStatusException wenn ein Schiff mit gleichem Namen existiert.
    public Schiff create(final Schiff schiff) {
        LOGGER.debug("create: {}", schiff);

        final var existingShip = repo.find(Map.of("name", List.of(schiff.getName())));
        if (!existingShip.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ein Schiff mit dem Namen '" + schiff.getName() + "' existiert bereits");
        }

        final var schiffDB = repo.create(schiff);
        LOGGER.debug("create: {}", schiffDB);
        return schiffDB;
    }

    /// Ein bestehendes Schiff aktualisieren.
    ///
    /// @param schiff Objekt mit aktualisierten Werten.
    /// @param id ID des zu aktualisierenden Schiffs.
    /// @throws NotFoundException wenn kein Schiff zur gegebenen ID existiert
    /// @throws ResponseStatusException bei Namenskonflikt.
    public void update(@Valid final Schiff schiff, final UUID id) {
        LOGGER.debug("update: {}", schiff);
        LOGGER.debug("update: id={}", id);

        final var schiffDB = repo.findById(id);
        if (schiffDB == null) {
            throw new NotFoundException(id);
        }

        final var existingShips = repo.find(Map.of("name", List.of(schiff.getName())));
        final var shipWithSameName = existingShips.stream()
            .filter(s -> !s.getId().equals(id))
            .findFirst();
        if (shipWithSameName.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ein anderes Schiff mit dem Namen '" + schiff.getName() + "' existiert bereits");
        }

        schiff.setId(id);
        repo.update(schiff);
    }

    /// Ein Schiff anhand seiner ID löschen.
    ///
    /// @param id ID des zu löschenden Schiffs.
    public void deleteById(final UUID id) {
        LOGGER.debug("deleteById: id={}", id);
        repo.deleteById(id);
    }
}
