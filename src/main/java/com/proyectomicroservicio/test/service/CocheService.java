package com.proyectomicroservicio.test.service;

import com.proyectomicroservicio.test.service.dto.CocheDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Coche.
 */
public interface CocheService {

    /**
     * Save a coche.
     *
     * @param cocheDTO the entity to save
     * @return the persisted entity
     */
    CocheDTO save(CocheDTO cocheDTO);

    /**
     * Get all the coches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CocheDTO> findAll(Pageable pageable);


    /**
     * Get the "id" coche.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CocheDTO> findOne(Long id);

    /**
     * Delete the "id" coche.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the coche corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CocheDTO> search(String query, Pageable pageable);
}
