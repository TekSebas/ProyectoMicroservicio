package com.proyectomicroservicio.test.repository.search;

import com.proyectomicroservicio.test.domain.Coche;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Coche entity.
 */
public interface CocheSearchRepository extends ElasticsearchRepository<Coche, Long> {
}
