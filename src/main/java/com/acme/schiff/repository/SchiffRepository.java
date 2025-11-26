package com.acme.schiff.repository;

import com.acme.schiff.entity.Schiff;
import com.acme.schiff.entity.SchiffTyp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import static com.acme.schiff.repository.MockDB.SCHIFFE;
import static java.util.Collections.emptyList;

/// Repository für die Entity-Klasse [Schiff].
/// Implementiert die Persistenz in einer Map.
///
/// @author Murat Yahsi
@Repository
//@SuppressWarnings("PMD.UseEnumCollections")
public class SchiffRepository {
    private final StableValue<Logger> logger = StableValue.of();

    /// Standardkonstruktor für das Repository.
    SchiffRepository() {
        // Default constructor for PMD compliance
    }

    /// Eines Schiff anhand seiner ID suchen.
    /// ```
    /// SELECT *
    /// FROM   schiff
    /// WHERE  id = ...
    /// ```
    ///
    /// @param id Die Id des gesuchten Schiffs
    /// @return Gefundenes Schiff oder null
    @Nullable
    public Schiff findById(final UUID id) {
        getLogger().debug("findById: id={}", id);
        final var result = SCHIFFE.stream()
            .filter(schiff -> Objects.equals(schiff.getId(), id))
            .findFirst()
            .orElse(null);
        getLogger().debug("findById: result={}", result);
        return result;
    }

    /// Schiffe anhand von Suchparameter ermitteln.
    /// Z.B. mit `GET https://localhost:8080/api?name=Titanic`
    ///
    /// @param suchparameter Suchparameter.
    /// @return Gefundenes Schiff oder leere Collection.
    @SuppressWarnings({"ReturnCount", "JavadocLinkAsPlainText", "PMD.AvoidLiteralsInIfCondition"})
    public Collection<Schiff> find(final Map<String, ? extends List<String>> suchparameter) {
        getLogger().debug("find: suchparameter={}", suchparameter);

        if (suchparameter.isEmpty()) {
            return findAll();
        }

        if (suchparameter.size() == 1) {
            final var namen = suchparameter.get("name");
            if (namen != null && namen.size() == 1) {
                final var schiffe = findByName(namen.getFirst());
                getLogger().debug("find (name): schiffe={}", schiffe);
                return schiffe;
            }
        }
        final var typen = suchparameter.get("typ");
        if (typen != null) {
            final var schiffe = findByTyp(typen);
            getLogger().debug("find (typen): schiffe={}", schiffe);
            return schiffe;
        }
        getLogger().debug("find: ungueltige Suchparameter={}", suchparameter);
        return emptyList();
    }

    /// Alle Schiffe als Collection ermitteln, wie sie später auch von der DB kommen.
    /// ```
    /// SELECT *
    /// FROM schiff
    /// ```
    ///
    /// @return Alle Schiffe
    private Collection<Schiff> findAll() {
        return SCHIFFE;
    }

    /// Schiffe anhand des Namens suchen.
    /// ```
    /// SELECT *
    /// FROM   schiff
    /// WHERE  name LIKE ...
    /// ```
    ///
    /// @param name Der (Teil-) Name dem gesuchten Schiff
    /// @return Die gefundenen Schiffe oder eine leere Collection
    private Collection<Schiff> findByName(final CharSequence name) {
        getLogger().debug("findByName: name={}", name);
        final var schiffs = SCHIFFE.stream()
            .filter(schiff -> schiff.getName().contains(name))
            .toList();
        getLogger().debug("findByName: schiffs={}", schiffs);
        return schiffs;
    }

    /// Schiffe anhand von Typen suchen.
    /// ```
    /// SELECT *
    /// FROM   schiff
    /// WHERE  typ = ...
    /// ```
    ///
    /// @param typStr Die Typen der gesuchten Schiffe
    /// @return Die gefundenen Schiffe oder eine leere Collection
    private Collection<Schiff> findByTyp(final Collection<String> typStr) {
        getLogger().debug("findByTyp: typStr{}", typStr);
        final var typen = typStr
            .stream()
            .map(SchiffTyp::of)
            .toList();
        if (typen.contains(null)) {
            getLogger().debug("findByTyp: keine SchiffTyp");
            return emptyList();
        }
        getLogger().trace("findByTyp: typen={}", typen);
        final var schiffe = SCHIFFE.stream()
            .filter(schiff -> {
                final Collection<SchiffTyp> schiffTypen = Set.of(schiff.getTyp());
                return schiffTypen.containsAll(typen);
            })

            .toList();
        getLogger().debug("findByTyp: schiffe={}", schiffe);
        return schiffe;
    }

    /// Ein neues Schiff anlegen.
    /// ```sql
    /// INSERT INTO schiff VALUES ...
    /// ```
    /// @param schiff Das Schiff-Objekt
    /// @return Das neu angelegte Schiff mit generierter ID
    public Schiff create(final Schiff schiff) {
        getLogger().debug("create: {}", schiff);
        schiff.setId(UUID.randomUUID());
        SCHIFFE.add(schiff);
        getLogger().debug("create: schiff={}", schiff);
        return schiff;
    }

    /// Ein bestehendes Schiff aktualisieren.
    /// ```sql
    /// UPDATE schiff SET ...
    /// ```
    /// @param schiff Das Objekt mit neuen Daten
    public void update(final Schiff schiff) {
        getLogger().debug("update: {}", schiff);
        final var index = IntStream
            .range(0, SCHIFFE.size())
            .filter(i -> Objects.equals(SCHIFFE.get(i).getId(), schiff.getId()))
            .findFirst();

        if (index.isEmpty()) {
            return;
        }
        SCHIFFE.set(index.getAsInt(), schiff);
        getLogger().debug("update: schiff={}", schiff);
    }

    /// Ein Schiff anhand der ID löschen.
    /// ```sql
    /// DELETE FROM schiff WHERE id = ...
    /// ```
    /// @param id Die ID des zu löschenden Schiffs
    public void deleteById(final UUID id) {
        getLogger().debug("deleteById: id={}", id);
        final var index = IntStream
            .range(0, SCHIFFE.size())
            .filter(i -> Objects.equals(SCHIFFE.get(i).getId(), id))
            .findFirst();

        index.ifPresent(SCHIFFE::remove);
        getLogger().debug("deleteById: #SCHIFFE={}", SCHIFFE.size());
    }
    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(SchiffRepository.class));
    }
}
