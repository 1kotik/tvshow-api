package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.dao.TVShowDAOImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowDAOImpl tvShowDAOImpl(){
        return new TVShowDAOImpl(logger());
    }

    @Bean
    public Logger logger(){
        return Logger.getLogger(getClass().getName());
    }

}
