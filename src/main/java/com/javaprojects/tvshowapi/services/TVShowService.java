package com.javaprojects.tvshowapi.services;


import com.javaprojects.tvshowapi.dao.TVShowDAO;
import com.javaprojects.tvshowapi.entity.TVShow;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TVShowService {
    private final TVShowDAO tvShowDAO;

    public TVShowService(TVShowDAO tvShowDAO) {
        this.tvShowDAO = tvShowDAO;
    }

    public List<TVShow> searchByTitle(String title) throws IOException {
        return tvShowDAO.searchByTitle(title);
    }
}
