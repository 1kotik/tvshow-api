package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.dao.TVShowDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowDAO tvShowDAOImpl() {
        return new TVShowDAO(logger());
    }

    @Bean
    public Logger logger() {
        return Logger.getLogger(getClass().getName());
    }

    
}
