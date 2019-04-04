package com.proyectomicroservicio.test.service.impl;

import com.proyectomicroservicio.test.service.CocheService;
import com.proyectomicroservicio.test.domain.Coche;
import com.proyectomicroservicio.test.repository.CocheRepository;
import com.proyectomicroservicio.test.repository.search.CocheSearchRepository;
import com.proyectomicroservicio.test.service.dto.CocheDTO;
import com.proyectomicroservicio.test.service.mapper.CocheMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Coche.
 */
@Service
@Transactional
public class CocheServiceImpl implements CocheService {

    private final Logger log = LoggerFactory.getLogger(CocheServiceImpl.class);

    private final CocheRepository cocheRepository;

    private final CocheMapper cocheMapper;

    private final CocheSearchRepository cocheSearchRepository;

    public CocheServiceImpl(CocheRepository cocheRepository, CocheMapper cocheMapper, CocheSearchRepository cocheSearchRepository) {
        this.cocheRepository = cocheRepository;
        this.cocheMapper = cocheMapper;
        this.cocheSearchRepository = cocheSearchRepository;
    }

    /**
     * Save a coche.
     *
     * @param cocheDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CocheDTO save(CocheDTO cocheDTO) {
        log.debug("Request to save Coche : {}", cocheDTO);

        Coche coche = cocheMapper.toEntity(cocheDTO);
        coche = cocheRepository.save(coche);
        CocheDTO result = cocheMapper.toDto(coche);
        cocheSearchRepository.save(coche);
        return result;
    }

    /**
     * Get all the coches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CocheDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Coches");
        return cocheRepository.findAll(pageable)
            .map(cocheMapper::toDto);
    }


    /**
     * Get one coche by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CocheDTO> findOne(Long id) {
        log.debug("Request to get Coche : {}", id);
        return cocheRepository.findById(id)
            .map(cocheMapper::toDto);
    }

    /**
     * Delete the coche by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Coche : {}", id);
        cocheRepository.deleteById(id);
        cocheSearchRepository.deleteById(id);
    }

    /**
     * Search for the coche corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CocheDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Coches for query {}", query);
        return cocheSearchRepository.search(queryStringQuery(query), pageable)
            .map(cocheMapper::toDto);
    }
}
