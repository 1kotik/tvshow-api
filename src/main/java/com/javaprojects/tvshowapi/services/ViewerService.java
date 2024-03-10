package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class ViewerService {
    private final Logger logger;
    private final ViewerRepository viewerRepository;

    private final TVShowRepository tvShowRepository;

    public List<Viewer> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, "Return all viewers");
            return viewerRepository.findAll();
        } else {
            logger.log(Level.INFO, "Search is successful");
            return viewerRepository.searchByName(name);
        }
    }

    public void insertViewer(Viewer viewer) {
        if (viewerRepository.findById(viewer.getId()).isEmpty()) {
            viewerRepository.save(viewer);
            logger.log(Level.INFO, "Successfully added viewer " + viewer.getName());
        } else logger.log(Level.INFO, "Viewer with such ID already exists!");
    }

    @Transactional
    public void deleteViewer(Long id) {
        if (viewerRepository.findById(id).isPresent()) {
            viewerRepository.deleteById(id);
            logger.log(Level.INFO, "Delete is successful");
        } else logger.log(Level.INFO, "Viewer with such ID does not exist!");
    }

    public void updateViewer(Viewer viewer) {
        if (viewerRepository.findById(viewer.getId()).isPresent()) {
            viewerRepository.save(viewer);
            logger.log(Level.INFO, "Update is successful");
        } else logger.log(Level.INFO, "Viewer with such ID does not exist!");
    }

    @Transactional
    public void addToWatched(Long viewerId, Long tvShowId) {
        Optional<TVShow> tvShow = tvShowRepository.findById(tvShowId);
        Optional<Viewer> viewer = viewerRepository.findById(viewerId);
        if (tvShow.isPresent() && viewer.isPresent() && !viewer.get().getTvShows().contains(tvShow.get())) {
            viewer.get().getTvShows().add(tvShow.get());
            logger.log(Level.INFO, "Added TV Show in watched");
        } else logger.log(Level.INFO, "Cannot do that");
    }

    public Set<TVShow> getWatchedTVShows(Long viewerId) {
        Optional<Viewer> viewer=viewerRepository.findById(viewerId);
        if (viewer.isPresent()) {
            logger.log(Level.INFO, "Returned watched TV Shows");
            return viewer.get().getTvShows();
        } else {
            logger.log(Level.INFO, "Viewer with such ID does not exist!");
            return null;
        }
    }
}

