package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.services.ViewerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/viewers")
@AllArgsConstructor
public class ViewerController {
    private final ViewerService viewerService;

    @GetMapping("/get")
    public List<Viewer> searchByName(@RequestParam(required = false) String name) {
        return viewerService.searchByName(name);
    }

    @GetMapping("/get-watched")
    public Set<TVShow> getWatchedTVShows(@RequestParam Long id){
        return viewerService.getWatchedTVShows(id);
    }
    @PostMapping("/post")
    public void insertViewer(@RequestBody Viewer viewer) {
        viewerService.insertViewer(viewer);
    }

    @DeleteMapping("/delete")
    public void deleteViewer(@RequestParam Long id) {
        viewerService.deleteViewer(id);
    }

    @PutMapping("/update")
    public void updateViewer(@RequestBody Viewer viewer) {
        viewerService.updateViewer(viewer);
    }

    @PutMapping("/add-to-watched")
    public void addToWatched(@RequestParam Long vid, @RequestParam Long tv){
        viewerService.addToWatched(vid, tv);
    }
}

