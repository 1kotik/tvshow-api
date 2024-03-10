package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.services.TVShowService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tvshows")
public class TVShowController {

    private final TVShowService tvShowService;

    public TVShowController(TVShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    @GetMapping("/get-from-api")
    public List<TVShow> searchByTitleFromAPI(@RequestParam(required = false) String title) throws IOException {
        return tvShowService.searchByTitleFromAPI(title);
    }

    @GetMapping("/get")
    public List<TVShow> searchByTitle(@RequestParam(required = false) String title) {
        return tvShowService.searchByTitle(title);
    }

    @PostMapping("/post")
    public void insertTVShow(@RequestBody TVShow tvShow) {
        tvShowService.insertTVShow(tvShow);
    }

    @DeleteMapping("/delete")
    public void deleteTVShow(@RequestParam Long id) {
        tvShowService.deleteTVShow(id);
    }

    @PutMapping("/update")
    public void updateTVShow(@RequestBody TVShow tvShow) {
        tvShowService.updateTVShow(tvShow);
    }
}

