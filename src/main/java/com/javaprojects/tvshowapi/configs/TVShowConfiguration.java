package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.dao.TVShowDAOImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowDAOImpl tvShowDAOImpl(){
        return new TVShowDAOImpl();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
