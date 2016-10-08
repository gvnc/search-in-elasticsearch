package com.gvnc.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by EXT01D3678 on 27.9.2016.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        ElasticsearchService elasticsearchService = context.getBean(ElasticsearchService.class);

        String tailnum = "N712SW";

        elasticsearchService.getFlightsByTailnum(tailnum);

        elasticsearchService.getFlightsByTailnumUsingPages(tailnum, 0, 5);

        elasticsearchService.getFlightsByTailnumUsingQueryDSL(tailnum, 0, 5);

        elasticsearchService.getFlightsByTailnumUsingQueryBuilder(tailnum);

        elasticsearchService.getFlightsByCodeAndArrival("WN" , "Kansas City International" , "Denver Intl");
    }
}