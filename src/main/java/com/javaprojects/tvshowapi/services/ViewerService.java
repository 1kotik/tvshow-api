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


import java.io.Console;
import java.util.*;
import java.util.stream.Collectors;

import static com.javaprojects.tvshowapi.utilities.Constants.SERVER_ERROR_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.INVALID_INFO_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.NOT_FOUND_MSG;

@AllArgsConstructor
public class ViewerService {
    private final ViewerRepository viewerRepository;
    private final TVShowRepository tvShowRepository;
    private EntityCache<Integer, List<Viewer>> cache;

    public List<Viewer> getViewers() {
        try {
            List<Viewer> result = viewerRepository.findAll().stream()
                    .sorted(Comparator.comparing(Viewer::getId)).collect(Collectors.toList());
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Viewer> searchByName(final String name) {
        if (name == null || name.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(name);
            List<Viewer> viewers = cache.get(hashCode);
            if (viewers != null) {
                return viewers;
            } else {
                try {
                    List<Viewer> result = viewerRepository.findAll().stream()
                            .filter(v -> v.getName().contains(name)).toList();
                    if (!result.isEmpty()) {
                        cache.put(hashCode, result);
                        return result;
                    }
                } catch (Exception e) {
                    throw new ServerException(SERVER_ERROR_MSG);
                }
                throw new NotFoundException(NOT_FOUND_MSG);
            }
        }
    }

    public void insertViewer(final Viewer viewer) {
        if (viewer.getName() == null || viewer.getName().equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        try {
            viewerRepository.save(viewer);
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        cache.remove(Objects.hashCode(viewer.getName()));
    }

    public void deleteViewer(final Long id) {
        if (id == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<Viewer> viewer = viewerRepository.findById(id);
            if (viewer.isPresent()) {
                cache.remove(Objects.hashCode(viewer.get().getName()));
                viewerRepository.deleteById(id);
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void updateViewer(final Viewer viewer) {
        if (viewer.getId() == null || viewer.getName() == null || viewer.getName().equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        try {
            if (viewerRepository.findById(viewer.getId()).isPresent()) {
                cache.remove(Objects.hashCode(viewer.getName()));
                cache.remove(Objects.hashCode(viewerRepository.findById(viewer.getId()).get().getName()));
                viewerRepository.save(viewer);
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    @Transactional
    public void addToWatched(final Long viewerId, final Long tvShowId) {
        if (viewerId == null || tvShowId == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<TVShow> tvShow = tvShowRepository.findById(tvShowId);
            Optional<Viewer> viewer = viewerRepository.findById(viewerId);
            if (tvShow.isPresent() && viewer.isPresent() && !viewer.get().getTvShows().contains(tvShow.get())) {
                viewer.get().getTvShows().add(tvShow.get());
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public Set<TVShow> getWatchedTVShows(final Long viewerId) {
        if (viewerId == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Set<TVShow> result = tvShowRepository.findAll().stream()
                    .filter(tv -> tv.getViewers().stream().anyMatch(v -> v.getId().equals(viewerId))).collect(Collectors.toSet());
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }
}
