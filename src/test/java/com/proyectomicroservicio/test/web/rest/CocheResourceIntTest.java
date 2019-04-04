package com.proyectomicroservicio.test.web.rest;

import com.proyectomicroservicio.test.MicroservicioApp;

import com.proyectomicroservicio.test.domain.Coche;
import com.proyectomicroservicio.test.repository.CocheRepository;
import com.proyectomicroservicio.test.repository.search.CocheSearchRepository;
import com.proyectomicroservicio.test.service.CocheService;
import com.proyectomicroservicio.test.service.dto.CocheDTO;
import com.proyectomicroservicio.test.service.mapper.CocheMapper;
import com.proyectomicroservicio.test.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


import static com.proyectomicroservicio.test.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CocheResource REST controller.
 *
 * @see CocheResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroservicioApp.class)
public class CocheResourceIntTest {

    private static final String DEFAULT_MARCA = "AAAAAAAAAA";
    private static final String UPDATED_MARCA = "BBBBBBBBBB";

    private static final String DEFAULT_MODELO = "AAAAAAAAAA";
    private static final String UPDATED_MODELO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_ITV = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_ITV = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private CocheMapper cocheMapper;

    @Autowired
    private CocheService cocheService;

    /**
     * This repository is mocked in the com.proyectomicroservicio.test.repository.search test package.
     *
     * @see com.proyectomicroservicio.test.repository.search.CocheSearchRepositoryMockConfiguration
     */
    @Autowired
    private CocheSearchRepository mockCocheSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCocheMockMvc;

    private Coche coche;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CocheResource cocheResource = new CocheResource(cocheService);
        this.restCocheMockMvc = MockMvcBuilders.standaloneSetup(cocheResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coche createEntity(EntityManager em) {
        Coche coche = new Coche()
            .marca(DEFAULT_MARCA)
            .modelo(DEFAULT_MODELO)
            .fechaITV(DEFAULT_FECHA_ITV);
        return coche;
    }

    @Before
    public void initTest() {
        coche = createEntity(em);
    }

    @Test
    @Transactional
    public void createCoche() throws Exception {
        int databaseSizeBeforeCreate = cocheRepository.findAll().size();

        // Create the Coche
        CocheDTO cocheDTO = cocheMapper.toDto(coche);
        restCocheMockMvc.perform(post("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isCreated());

        // Validate the Coche in the database
        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeCreate + 1);
        Coche testCoche = cocheList.get(cocheList.size() - 1);
        assertThat(testCoche.getMarca()).isEqualTo(DEFAULT_MARCA);
        assertThat(testCoche.getModelo()).isEqualTo(DEFAULT_MODELO);
        assertThat(testCoche.getFechaITV()).isEqualTo(DEFAULT_FECHA_ITV);

        // Validate the Coche in Elasticsearch
        verify(mockCocheSearchRepository, times(1)).save(testCoche);
    }

    @Test
    @Transactional
    public void createCocheWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cocheRepository.findAll().size();

        // Create the Coche with an existing ID
        coche.setId(1L);
        CocheDTO cocheDTO = cocheMapper.toDto(coche);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCocheMockMvc.perform(post("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coche in the database
        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeCreate);

        // Validate the Coche in Elasticsearch
        verify(mockCocheSearchRepository, times(0)).save(coche);
    }

    @Test
    @Transactional
    public void checkMarcaIsRequired() throws Exception {
        int databaseSizeBeforeTest = cocheRepository.findAll().size();
        // set the field null
        coche.setMarca(null);

        // Create the Coche, which fails.
        CocheDTO cocheDTO = cocheMapper.toDto(coche);

        restCocheMockMvc.perform(post("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isBadRequest());

        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkModeloIsRequired() throws Exception {
        int databaseSizeBeforeTest = cocheRepository.findAll().size();
        // set the field null
        coche.setModelo(null);

        // Create the Coche, which fails.
        CocheDTO cocheDTO = cocheMapper.toDto(coche);

        restCocheMockMvc.perform(post("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isBadRequest());

        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFechaITVIsRequired() throws Exception {
        int databaseSizeBeforeTest = cocheRepository.findAll().size();
        // set the field null
        coche.setFechaITV(null);

        // Create the Coche, which fails.
        CocheDTO cocheDTO = cocheMapper.toDto(coche);

        restCocheMockMvc.perform(post("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isBadRequest());

        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCoches() throws Exception {
        // Initialize the database
        cocheRepository.saveAndFlush(coche);

        // Get all the cocheList
        restCocheMockMvc.perform(get("/api/coches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coche.getId().intValue())))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA.toString())))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO.toString())))
            .andExpect(jsonPath("$.[*].fechaITV").value(hasItem(DEFAULT_FECHA_ITV.toString())));
    }
    
    @Test
    @Transactional
    public void getCoche() throws Exception {
        // Initialize the database
        cocheRepository.saveAndFlush(coche);

        // Get the coche
        restCocheMockMvc.perform(get("/api/coches/{id}", coche.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(coche.getId().intValue()))
            .andExpect(jsonPath("$.marca").value(DEFAULT_MARCA.toString()))
            .andExpect(jsonPath("$.modelo").value(DEFAULT_MODELO.toString()))
            .andExpect(jsonPath("$.fechaITV").value(DEFAULT_FECHA_ITV.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCoche() throws Exception {
        // Get the coche
        restCocheMockMvc.perform(get("/api/coches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoche() throws Exception {
        // Initialize the database
        cocheRepository.saveAndFlush(coche);

        int databaseSizeBeforeUpdate = cocheRepository.findAll().size();

        // Update the coche
        Coche updatedCoche = cocheRepository.findById(coche.getId()).get();
        // Disconnect from session so that the updates on updatedCoche are not directly saved in db
        em.detach(updatedCoche);
        updatedCoche
            .marca(UPDATED_MARCA)
            .modelo(UPDATED_MODELO)
            .fechaITV(UPDATED_FECHA_ITV);
        CocheDTO cocheDTO = cocheMapper.toDto(updatedCoche);

        restCocheMockMvc.perform(put("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isOk());

        // Validate the Coche in the database
        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeUpdate);
        Coche testCoche = cocheList.get(cocheList.size() - 1);
        assertThat(testCoche.getMarca()).isEqualTo(UPDATED_MARCA);
        assertThat(testCoche.getModelo()).isEqualTo(UPDATED_MODELO);
        assertThat(testCoche.getFechaITV()).isEqualTo(UPDATED_FECHA_ITV);

        // Validate the Coche in Elasticsearch
        verify(mockCocheSearchRepository, times(1)).save(testCoche);
    }

    @Test
    @Transactional
    public void updateNonExistingCoche() throws Exception {
        int databaseSizeBeforeUpdate = cocheRepository.findAll().size();

        // Create the Coche
        CocheDTO cocheDTO = cocheMapper.toDto(coche);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCocheMockMvc.perform(put("/api/coches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cocheDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coche in the database
        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Coche in Elasticsearch
        verify(mockCocheSearchRepository, times(0)).save(coche);
    }

    @Test
    @Transactional
    public void deleteCoche() throws Exception {
        // Initialize the database
        cocheRepository.saveAndFlush(coche);

        int databaseSizeBeforeDelete = cocheRepository.findAll().size();

        // Get the coche
        restCocheMockMvc.perform(delete("/api/coches/{id}", coche.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Coche> cocheList = cocheRepository.findAll();
        assertThat(cocheList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Coche in Elasticsearch
        verify(mockCocheSearchRepository, times(1)).deleteById(coche.getId());
    }

    @Test
    @Transactional
    public void searchCoche() throws Exception {
        // Initialize the database
        cocheRepository.saveAndFlush(coche);
        when(mockCocheSearchRepository.search(queryStringQuery("id:" + coche.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(coche), PageRequest.of(0, 1), 1));
        // Search the coche
        restCocheMockMvc.perform(get("/api/_search/coches?query=id:" + coche.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coche.getId().intValue())))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA)))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO)))
            .andExpect(jsonPath("$.[*].fechaITV").value(hasItem(DEFAULT_FECHA_ITV.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coche.class);
        Coche coche1 = new Coche();
        coche1.setId(1L);
        Coche coche2 = new Coche();
        coche2.setId(coche1.getId());
        assertThat(coche1).isEqualTo(coche2);
        coche2.setId(2L);
        assertThat(coche1).isNotEqualTo(coche2);
        coche1.setId(null);
        assertThat(coche1).isNotEqualTo(coche2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CocheDTO.class);
        CocheDTO cocheDTO1 = new CocheDTO();
        cocheDTO1.setId(1L);
        CocheDTO cocheDTO2 = new CocheDTO();
        assertThat(cocheDTO1).isNotEqualTo(cocheDTO2);
        cocheDTO2.setId(cocheDTO1.getId());
        assertThat(cocheDTO1).isEqualTo(cocheDTO2);
        cocheDTO2.setId(2L);
        assertThat(cocheDTO1).isNotEqualTo(cocheDTO2);
        cocheDTO1.setId(null);
        assertThat(cocheDTO1).isNotEqualTo(cocheDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(cocheMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(cocheMapper.fromId(null)).isNull();
    }
}
