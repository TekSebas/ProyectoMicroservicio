package com.proyectomicroservicio.test.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.proyectomicroservicio.test.service.CocheService;
import com.proyectomicroservicio.test.web.rest.errors.BadRequestAlertException;
import com.proyectomicroservicio.test.web.rest.util.HeaderUtil;
import com.proyectomicroservicio.test.web.rest.util.PaginationUtil;
import com.proyectomicroservicio.test.service.dto.CocheDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Coche.
 */
@RestController
@RequestMapping("/api")
public class CocheResource {

    private final Logger log = LoggerFactory.getLogger(CocheResource.class);

    private static final String ENTITY_NAME = "microservicioCoche";

    private final CocheService cocheService;

    public CocheResource(CocheService cocheService) {
        this.cocheService = cocheService;
    }

    /**
     * POST  /coches : Create a new coche.
     *
     * @param cocheDTO the cocheDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cocheDTO, or with status 400 (Bad Request) if the coche has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/coches")
    @Timed
    public ResponseEntity<CocheDTO> createCoche(@Valid @RequestBody CocheDTO cocheDTO) throws URISyntaxException {
        log.debug("REST request to save Coche : {}", cocheDTO);
        if (cocheDTO.getId() != null) {
            throw new BadRequestAlertException("A new coche cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CocheDTO result = cocheService.save(cocheDTO);
        return ResponseEntity.created(new URI("/api/coches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /coches : Updates an existing coche.
     *
     * @param cocheDTO the cocheDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cocheDTO,
     * or with status 400 (Bad Request) if the cocheDTO is not valid,
     * or with status 500 (Internal Server Error) if the cocheDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/coches")
    @Timed
    public ResponseEntity<CocheDTO> updateCoche(@Valid @RequestBody CocheDTO cocheDTO) throws URISyntaxException {
        log.debug("REST request to update Coche : {}", cocheDTO);
        if (cocheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CocheDTO result = cocheService.save(cocheDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cocheDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /coches : get all the coches.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of coches in body
     */
    @GetMapping("/coches")
    @Timed
    public ResponseEntity<List<CocheDTO>> getAllCoches(Pageable pageable) {
        log.debug("REST request to get a page of Coches");
        Page<CocheDTO> page = cocheService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/coches");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /coches/:id : get the "id" coche.
     *
     * @param id the id of the cocheDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cocheDTO, or with status 404 (Not Found)
     */
    @GetMapping("/coches/{id}")
    @Timed
    public ResponseEntity<CocheDTO> getCoche(@PathVariable Long id) {
        log.debug("REST request to get Coche : {}", id);
        Optional<CocheDTO> cocheDTO = cocheService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cocheDTO);
    }

    /**
     * DELETE  /coches/:id : delete the "id" coche.
     *
     * @param id the id of the cocheDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/coches/{id}")
    @Timed
    public ResponseEntity<Void> deleteCoche(@PathVariable Long id) {
        log.debug("REST request to delete Coche : {}", id);
        cocheService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/coches?query=:query : search for the coche corresponding
     * to the query.
     *
     * @param query the query of the coche search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/coches")
    @Timed
    public ResponseEntity<List<CocheDTO>> searchCoches(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Coches for query {}", query);
        Page<CocheDTO> page = cocheService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/coches");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
