package com.acme.schiff.controller;

import com.acme.schiff.entity.Crew;
import com.acme.schiff.entity.Hafen;
import com.acme.schiff.entity.Schiff;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/// Mapper zwischen Entity-Klassen.
/// Siehe `build\generated\sources\annotationProcessor\java\main\...\SchiffMapperImpl.java`.
///
/// @author Murat Yahsi
@Mapper(nullValueIterableMappingStrategy = RETURN_DEFAULT, componentModel = "spring")
@AnnotateWith(ExcludeFromJacocoGeneratedReport.class)
interface SchiffMapper {
    /// Ein DTO-Objekt von [SchiffDTO] in ein Objekt für [Schiff] konvertieren.
    /// Die ID wird ignoriert, da sie beim Neuanlegen null sein soll.
    ///
    /// @param dto DTO-Objekt für `SchiffDTO`
    /// @return Konvertiertes `Schiff`-Objekt mit null als ID
    @Mapping(target = "id", ignore = true)
    Schiff toSchiff(SchiffDTO dto);

    /// Ein DTO-Objekt von [HafenDTO] in ein Objekt für [Hafen] konvertieren.
    ///
    /// @param dto DTO-Objekt für `HafenDTO`
    /// @return Konvertiertes `Hafen`-Objekt
    Hafen toHafen(HafenDTO dto);

    /// Ein DTO-Objekt von [CrewDTO] in ein Objekt für [Crew] konvertieren.
    ///
    /// @param dto DTO-Objekt für `CrewDTO`
    /// @return Konvertiertes `Crew`-Objekt
    Crew toCrew(CrewDTO dto);
}
