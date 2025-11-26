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

import com.acme.schiff.entity.SchiffTyp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// ValueObject für das Neuanlegen und Ändern eines Schiffs.
///
/// @author Murat Yahsi
/// @param name Name des Schiffs, d.h. mit einem geeigneten Muster.
/// @param baujahr Das Baujahr des Schiffs.
/// @param kapazitaet Die Kapazität des Schiffs.
/// @param typ Der Typ des Schiffs.
/// @param hafen Der Heimathafen des Schiffs.
/// @param crewList Die Crew-Mitglieder des Schiffs.
@SuppressWarnings("RecordComponentNumber")
record SchiffDTO(
    @NotNull
    @Pattern(regexp = NAME_PATTERN)
    @Size(max = NAME_MAX_LENGTH)
    String name,

    @NotNull
    @Past
    LocalDate baujahr,

    @Min(MIN_KAPAZITAET)
    @Max(MAX_KAPAZITAET)
    int kapazitaet,

    @NotNull
    SchiffTyp typ,

    @Valid
    @NotNull(groups = OnCreate.class)
    HafenDTO hafen,

    @Nullable
    List<@Valid CrewDTO> crewList
) {
    public static final String NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
    public static final int MIN_KAPAZITAET = 1;
    public static final int MAX_KAPAZITAET = 10_000;
    public static final int NAME_MAX_LENGTH = 40;

    /**
     * Marker-Interface für Jakarta Validation.
     * Wird verwendet, um Validierungen beim Neuanlegen zu aktivieren.
     */
    interface OnCreate { }
}
