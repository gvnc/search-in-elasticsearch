package com.gvnc.search;

/**
 * Created by EXT01D3678 on 23.9.2016.
 */

import com.gvnc.search.model.Flight;
import com.gvnc.search.repository.FlightRepository;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.gvnc.search")
public class ElasticsearchService {

    // set this true to use embedded elastic search server
    boolean useEmbeddedElasticSearchServer = false;

    @Autowired
    private FlightRepository repository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Bean
    public Client getNodeClient() {
        try {
            if(useEmbeddedElasticSearchServer == true) {
                return (NodeClient) nodeBuilder()
                            .settings(Settings.builder()
                                .put("path.home", "./"))
                        .clusterName("elasticsearch").local(true).node().client();
            } else {
                // cluster.name is elasticsearch by default
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", "ssvk-elastic")
                        .put("client.transport.sniff", true)
                        .build();

                TransportClient client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
                return client;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public List<Flight> getFlightsByTailnum(String tailnum){
        List flightList = repository.findByPlaneTailnum(tailnum);
        System.out.println(flightList.size() + " flights found.");
        return flightList;
    }

    public List<Flight> getFlightsByTailnumUsingPages(String tailnum, int from, int to){
        List flightList = repository.findByPlaneTailnum(tailnum, new PageRequest(from, to));
        System.out.println(flightList.size() + " flights found.");
        return flightList;
    }

    Page<Flight> getFlightsByTailnumUsingQueryDSL (String tailnum, int from, int to){
        Page flightList = repository.findByPlaneTailnumUsingQueryDSL(tailnum, new PageRequest(from, to));
        System.out.println(flightList.getNumberOfElements() + " flights found.");
        return flightList;
    }

    public List<Flight> getFlightsByTailnumUsingQueryBuilder(String tailnum){

        BoolQueryBuilder builder = boolQuery();
        builder.must(queryStringQuery(tailnum).
                field("plane.tailnum").
                defaultOperator(QueryStringQueryBuilder.Operator.AND));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();

        List<Flight> flightList = elasticsearchTemplate.queryForList(searchQuery, Flight.class);

        System.out.println(flightList.size()  + " flights found.");
        return flightList;
    }

    public List<Flight> getFlightsByCodeAndArrival(String code, String origin,String dest) {
        List<Flight> flightList = repository.findByUniqueCarrierCodeAndOriginAirportAndDestAirport(code, origin, dest);
        System.out.println("flight list count : " + flightList.size());
        return flightList;
    }


}
