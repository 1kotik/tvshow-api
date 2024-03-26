package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.exceptions.BadRequestException;
import com.javaprojects.tvshowapi.exceptions.NotFoundException;
import com.javaprojects.tvshowapi.exceptions.ServerException;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.javaprojects.tvshowapi.utilities.Constants.*;

@AllArgsConstructor
public class ViewerService {
    private final Logger logger;
    private final ViewerRepository viewerRepository;
    private final TVShowRepository tvShowRepository;
    private EntityCache<Integer, List<Viewer>> cache;

    public List<Viewer> getViewers() {
        try {
            List<Viewer> result = viewerRepository.findAll();
            if (!result.isEmpty()) return result;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Viewer> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(name);
            List<Viewer> viewers = cache.get(hashCode);
            if (viewers != null) {
                logger.log(Level.INFO, "Search in cache");
                return viewers;
            } else {
                try {
                    List<Viewer> result = new ArrayList<>(viewerRepository.searchByName(name));
                    if (!result.isEmpty()) {
                        logger.log(Level.INFO, "Search in database");
                        cache.put(hashCode, result);
                        return result;
                    }
                } catch (Exception e) {
                    logger.log(Level.INFO, e.getMessage());
                    throw new ServerException(SERVER_ERROR_MSG);
                }
                logger.log(Level.INFO, NOT_FOUND_MSG);
                throw new NotFoundException(NOT_FOUND_MSG);
            }
        }
    }

    public void insertViewer(Viewer viewer) {
        if (viewer.getName() == null || viewer.getName().equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        for(TVShow tvShow:viewer.getTvShows()){

            for(Character character: tvShow.getCharacters()) character.setTvShow(tvShow);
        }
        try {
            viewerRepository.save(viewer);
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        cache.remove(Objects.hashCode(viewer.getName()));
        logger.log(Level.INFO, "Successfully added viewer " + viewer.getName());
    }

    public void deleteViewer(Long id) {
        if (id == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<Viewer> viewer = viewerRepository.findById(id);
            if (viewer.isPresent()) {
                cache.remove(Objects.hashCode(viewer.get().getName()));
                viewerRepository.deleteById(id);
                logger.log(Level.INFO, "Delete is successful");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void updateViewer(Viewer viewer) {
        if (viewer.getId() == null || viewer.getName() == null || viewer.getName().equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        for(TVShow tvShow:viewer.getTvShows()){
            for(Character character: tvShow.getCharacters()) character.setTvShow(tvShow);
        }
        try {
            if (viewerRepository.findById(viewer.getId()).isPresent()) {
                cache.remove(Objects.hashCode(viewer.getName()));
                cache.remove(Objects.hashCode(viewerRepository.findById(viewer.getId()).get().getName()));
                viewerRepository.save(viewer);
                logger.log(Level.INFO, "Update is successful");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    @Transactional
    public void addToWatched(Long viewerId, Long tvShowId) {
        if (viewerId == null || tvShowId == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<TVShow> tvShow = tvShowRepository.findById(tvShowId);
            Optional<Viewer> viewer = viewerRepository.findById(viewerId);
            if (tvShow.isPresent() && viewer.isPresent() && !viewer.get().getTvShows().contains(tvShow.get())) {
                viewer.get().getTvShows().add(tvShow.get());
                logger.log(Level.INFO, "Added TV Show in watched");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public Set<TVShow> getWatchedTVShows(Long viewerId) {
        if (viewerId == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<Viewer> viewer = viewerRepository.findById(viewerId);
            if (viewer.isPresent()) {
                logger.log(Level.INFO, "Returned watched TV Shows");
                return viewer.get().getTvShows();
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }
}

