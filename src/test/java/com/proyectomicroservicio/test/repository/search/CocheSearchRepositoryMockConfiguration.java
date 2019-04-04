package com.proyectomicroservicio.test.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of CocheSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CocheSearchRepositoryMockConfiguration {

    @MockBean
    private CocheSearchRepository mockCocheSearchRepository;

}
