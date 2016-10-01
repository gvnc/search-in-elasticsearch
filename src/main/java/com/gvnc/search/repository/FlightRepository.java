package com.gvnc.search.repository;

import com.gvnc.search.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface FlightRepository extends ElasticsearchRepository<Flight,String> {

    List<Flight> findByPlaneTailnum(String tailnum);

    List<Flight> findByPlaneTailnum(String tailnum, Pageable pageable);

    @Query("{\"bool\" : {\"must\" : {\"query_string\" : {\"query\" : \"?0\", \"fields\" : [ \"plane.tailnum\" ], \"default_operator\" : \"and\" } } } }")
    Page<Flight> findByPlaneTailnumUsingQueryDSL(String tailnum, Pageable pageable);

    List<Flight> findByUniqueCarrierCodeAndOriginAirportAndDestAirport(String code, String origin, String dest);

}
