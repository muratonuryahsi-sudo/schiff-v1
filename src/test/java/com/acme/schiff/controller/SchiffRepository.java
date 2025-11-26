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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.schiff.controller;

import com.acme.schiff.entity.Schiff;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import static com.acme.schiff.controller.TestConstants.VERSION_1;

@HttpExchange
@SuppressWarnings({"WriteTag", "PMD.AvoidDuplicateLiterals"})
interface SchiffRepository {
    @GetExchange(url = "/{id}", version = VERSION_1)
    ResponseEntity<Schiff> getById(@PathVariable String id);

    @GetExchange(url = "/{id}", version = VERSION_1)
    ResponseEntity<String> getByIdAsString(@PathVariable String id);

    @GetExchange(version = VERSION_1)
    List<Schiff> get(@RequestParam MultiValueMap<String, String> suchparameter);

    @PostExchange(version = VERSION_1)
    ResponseEntity<Void> post(@RequestBody SchiffDTO schiff);

    @PutExchange(url = "/{id}", version = VERSION_1)
    ResponseEntity<Void> put(@PathVariable String id, @RequestBody SchiffDTO schiff);

    @DeleteExchange(url = "/{id}", version = VERSION_1)
    ResponseEntity<Void> deleteById(@PathVariable String id);
}
