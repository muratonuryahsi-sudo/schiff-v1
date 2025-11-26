/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/// Geschäftslogik für Schiffn.
/// ![Klassendiagramm](../../../../../asciidoc/SchiffService.svg)
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
// Maven: ![Klassendiagramm](../../../../../../generated-docs/SchiffService.svg)
@Service
public class SchiffService {
    private final SchiffRepository repo;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit _package private_ für _Spring_.
    ///
    /// @param repo Injiziertes Repository-Objekt.
    SchiffService(final SchiffRepository repo) {
        this.repo = repo;
    }

    /// Einen Schiffn anhand seiner ID suchen.
    ///
    /// @param id Die Id des gesuchten Schiffen
    /// @return Das gefundene Schiff
    /// @throws NotFoundException Falls kein Schiff gefunden wurde
    public Schiff findById(final UUID id) {
        getLogger().debug("findById: id={}", id);
        final var schiff = repo.findById(id);
        if (schiff == null) {
            throw new NotFoundException(id);
        }
        getLogger().debug("findById: schiff={}", schiff);
        return schiff;
    }

    /// Schiffn anhand von SuchParametern als Collection suchen.
    ///
    /// @param suchparameter Die SuchParametern
    /// @return Die gefundenen Schiffn
    /// @throws NotFoundException Falls keine Schiffn gefunden wurden
    @SuppressWarnings({"ReturnCount", "NestedIfDepth"})
    public Collection<Schiff> find(final Map<String, List<String>> suchparameter) {
        getLogger().debug("find: suchparameter={}", suchparameter);

        final var schiffe = repo.find(suchparameter);
        if (schiffe.isEmpty()) {
            throw new NotFoundException(suchparameter);
        }

        getLogger().debug("find: schiffe={}", schiffe);
        return schiffe;
    }
    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(SchiffService.class));
    }
}
