package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import com.javaprojects.tvshowapi.services.TVShowService;
import com.javaprojects.tvshowapi.services.ViewerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowService tvShowService(TVShowRepository tvShowRepository) {
        return new TVShowService(logger(), tvShowRepository);
    }

    @Bean
    public ViewerService viewerService(ViewerRepository viewerRepository) {
        return new ViewerService(logger(), viewerRepository);
    }

    @Bean
    public Logger logger() {
        return Logger.getLogger(getClass().getName());
    }


}
