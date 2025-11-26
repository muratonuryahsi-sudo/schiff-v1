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

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/// [RuntimeException], falls kein Schiff gefunden wurde.
public final class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1101909572340666200L;

    /// Fehlerhafte ID
    @Nullable
    private final UUID id;

    ///  Fehlerhafte Suchparameter
    @Nullable
    private final Map<String, List<String>> suchparameter;

    /// Standardkonstruktor für den [SchiffService], wenn alle Schiffn gesucht werden, aber keine existieren.
    NotFoundException() {
        super("Keine Schiffn gefunden.");
        id = null;
        suchparameter = null;
    }

    /// Konstruktor für den [SchiffService] bei fehlerhafter ID.
    ///
    /// @param id Die fehlerhafte ID
    NotFoundException(final UUID id) {
        super("Kein Schiff mit der ID " + id + " gefunden.");
        this.id = id;
        suchparameter = null;
    }

    /// Konstruktor für den [SchiffService] bei fehlerhaften Suchparameter.
    ///
    /// @param suchparameter Die fehlerhaften Suchparameter
    NotFoundException(final Map<String, List<String>> suchparameter) {
        super("Keine Schiffen gefunden.");
        id = null;
        this.suchparameter = suchparameter;
    }

    /// id ermitteln.
    ///
    /// @return Die fehlerhafte id.
    public @Nullable UUID getId() {
        return id;
    }

    /// Suchparameter ermitteln.
    ///
    /// @return Die fehlerhaften Suchparameter.
    public @Nullable Map<String, List<String>> getSuchparameter() {
        return suchparameter;
    }
}
