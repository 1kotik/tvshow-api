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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
            List<Viewer> result = viewerRepository.findAll();
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
                    List<Viewer> result = new ArrayList<>(viewerRepository.searchByName(name));
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

        for (TVShow tvShow : viewer.getTvShows()) {
            for (Character character : tvShow.getCharacters()) {
                character.setTvShow(tvShow);
            }
        }
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
        for (TVShow tvShow : viewer.getTvShows()) {
            for (Character character : tvShow.getCharacters()) {
                character.setTvShow(tvShow);
            }
        }
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
            Optional<Viewer> viewer = viewerRepository.findById(viewerId);
            if (viewer.isPresent() && !viewer.get().getTvShows().isEmpty()) {
                return viewer.get().getTvShows();
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }
}
