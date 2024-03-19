package com.javaprojects.tvshowapi.configs;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import com.javaprojects.tvshowapi.services.CharacterService;
import com.javaprojects.tvshowapi.services.TVShowService;
import com.javaprojects.tvshowapi.services.ViewerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.logging.Logger;


@Configuration
public class TVShowConfiguration {
    @Bean
    public TVShowService tvShowService(TVShowRepository tvShowRepository, CharacterRepository characterRepository) {
        return new TVShowService(logger(), tvShowRepository, characterRepository, tvShowCache());
    }

    @Bean
    public ViewerService viewerService(ViewerRepository viewerRepository, TVShowRepository tvShowRepository) {
        return new ViewerService(logger(), viewerRepository, tvShowRepository, viewerCache());
    }

    @Bean
    public CharacterService characterService(CharacterRepository characterRepository, TVShowRepository tvShowRepository) {
        return new CharacterService(logger(), characterRepository, tvShowRepository, characterCache());
    }

    @Bean
    public Logger logger() {
        return Logger.getLogger(getClass().getName());
    }

    @Bean
    public EntityCache<Integer, List<Character>> characterCache() {
        return new EntityCache<>();
    }

    @Bean
    public EntityCache<Integer, List<TVShow>> tvShowCache() {
        return new EntityCache<>();
    }

    @Bean
    public EntityCache<Integer, List<Viewer>> viewerCache() {
        return new EntityCache<>();
    }


}
