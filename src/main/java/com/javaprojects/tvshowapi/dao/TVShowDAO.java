package com.javaprojects.tvshowapi.dao;

import com.javaprojects.tvshowapi.entity.TVShow;

import java.io.IOException;
import java.util.List;

public interface TVShowDAO {
    List<TVShow> searchByTitle(String name) throws IOException;
} 
