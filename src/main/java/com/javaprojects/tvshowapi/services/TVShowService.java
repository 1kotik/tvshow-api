package com.javaprojects.tvshowapi.services;


import com.javaprojects.tvshowapi.dao.TVShowDAOImpl;
import com.javaprojects.tvshowapi.entity.TVShow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TVShowService {
    private TVShowDAOImpl tvShowDAO;

    @Autowired
    public TVShowService(TVShowDAOImpl tvShowDAO){
        this.tvShowDAO=tvShowDAO;
    }
    public List<TVShow> searchByTitle(String title) throws IOException {
        return tvShowDAO.searchByTitle(title);
    }
}
