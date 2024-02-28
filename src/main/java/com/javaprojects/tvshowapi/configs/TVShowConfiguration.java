package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.dao.TVShowDAOImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowDAOImpl tvShowDAOImpl(){
        return new TVShowDAOImpl();
    }

}
