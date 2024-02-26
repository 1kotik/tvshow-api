package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entity.TVShow;
import com.javaprojects.tvshowapi.services.TVShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tvshows")
public class TVShowController {

    private TVShowService tvShowService;

    @Autowired
    public TVShowController(TVShowService tvShowService){
        this.tvShowService=tvShowService;
    }

    @GetMapping
    public List<TVShow> searchByTitle(@RequestParam(required = false) String title) throws IOException{
        if(title!=null) return tvShowService.searchByTitle(title);
        else return tvShowService.searchByTitle("");
    }

}

