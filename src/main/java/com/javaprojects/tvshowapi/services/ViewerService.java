package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class ViewerService {
    private final Logger logger;
    private final ViewerRepository viewerRepository;

    public List<Viewer> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, "Return all viewers");
            return viewerRepository.findAll();
        } else {
            logger.log(Level.INFO, "Success");
            return viewerRepository.searchByName(name);
        }
    }

    public void insertViewer(Viewer viewer) {
        if (viewerRepository.findById(viewer.getId()).isEmpty()) {
            viewerRepository.save(viewer);
            logger.log(Level.INFO, "Successfully added viewer " + viewer.getName());
        } else logger.log(Level.INFO, "Viewer with such ID already exists!");
    }

    public void deleteViewer(Long id) {
        if (viewerRepository.findById(id).isPresent()) {
            viewerRepository.deleteById(id);
            logger.log(Level.INFO, "Success");
        } else logger.log(Level.INFO, "Viewer with such ID does not exist!");
    }

    public void updateViewer(Viewer viewer) {
        if (viewerRepository.findById(viewer.getId()).isPresent()) {
            viewerRepository.save(viewer);
            logger.log(Level.INFO, "Success");
        } else logger.log(Level.INFO, "Viewer with such ID does not exist!");
    }
}

