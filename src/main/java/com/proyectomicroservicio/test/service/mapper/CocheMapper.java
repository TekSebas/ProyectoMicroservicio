package com.proyectomicroservicio.test.service.mapper;

import com.proyectomicroservicio.test.domain.*;
import com.proyectomicroservicio.test.service.dto.CocheDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Coche and its DTO CocheDTO.
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface CocheMapper extends EntityMapper<CocheDTO, Coche> {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    CocheDTO toDto(Coche coche);

    @Mapping(source = "usuarioId", target = "usuario")
    Coche toEntity(CocheDTO cocheDTO);

    default Coche fromId(Long id) {
        if (id == null) {
            return null;
        }
        Coche coche = new Coche();
        coche.setId(id);
        return coche;
    }
}
